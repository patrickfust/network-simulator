package dk.fust.networksimulator.controller;

import dk.fust.networksimulator.model.Scenario;
import dk.fust.networksimulator.model.TargetSystem;
import dk.fust.networksimulator.service.ScenarioService;
import dk.fust.networksimulator.service.SimulatorService;
import dk.fust.networksimulator.service.TargetSystemService;
import dk.fust.networksimulator.service.ThrottleService;
import dk.fust.networksimulator.service.proxy.ProxyRequest;
import dk.fust.networksimulator.service.proxy.ProxyResponse;
import dk.fust.networksimulator.simulations.SimulationChain;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;

@Slf4j
@RestController
@RequestMapping("/forward")
@AllArgsConstructor
@Hidden
public class ForwardController {

    private final ScenarioService scenarioService;
    private final SimulatorService simulatorService;
    private final TargetSystemService targetSystemService;
    private final ThrottleService throttleService;

    /**
     * Method for handling requests, we want to proxy to the target application.
     *
     * @param systemName the name of the target system we want to proxy to.
     * @param headers    the headers send by the client to this proxy.
     * @param request    telling spring that we want access to the servlet request object.
     * @param body       optional - the body send by the client to this proxy.
     * @return whatever the target application, returned to this proxy.
     */
    @RequestMapping(path = {"/{systemName}/**"}, produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<StreamingResponseBody> getRoot(@PathVariable String systemName,
                                     @RequestHeader Map<String, String> headers,
                                     HttpServletRequest request,
                                     @RequestBody(required = false) byte[] body) throws IOException {
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String path = fullPath.substring(9 + systemName.length());
        log.info("received request to proxy. Method: {}, Path: {}", request.getMethod(), path);

        TargetSystem targetSystem = targetSystemService.getTargetSystemByName(systemName).orElseThrow(() -> new RuntimeException("There are no target system with name: " + systemName));
        List<Scenario> scenarios = scenarioService.findScenariosByPath(path, targetSystem.getId()).orElseThrow();
        ProxyRequest proxyRequest = makeProxyRequest(targetSystem, request, path, scenarios, headers, body);
        ProxyResponse proxyResponse = makeProxyResponse(scenarios);

        List<Long> scenarioIds = scenarios.stream().map(Scenario::getId).toList();
        log.debug("forwarding request to target application. Scenarios: {}, ProxyRequest: {}", scenarioIds, proxyRequest);

        SimulationChain simulationChain = simulatorService.makeSimulationChain(scenarios);
        simulationChain.doSimulation(proxyRequest, proxyResponse);
        String hasBody = proxyResponse.getBody() != null && proxyResponse.getBody().length > 0 ? "with body" : "without body";
        log.trace("ProxyResponse: {}", proxyResponse);
        log.info("Received status code: {} ({}) from target/simulations, for path: {}", proxyResponse.getStatusCode(), hasBody, path);

        return makeResponseEntity(proxyResponse);
    }

    private ResponseEntity<StreamingResponseBody> makeResponseEntity(ProxyResponse proxyResponse) {
        HttpHeaders filteredHeaders = returnHttpHeadersForProxyResponse(
                proxyResponse.getHeaders(),
                proxyResponse.getBytesPerSecond() != null
        );

        if (proxyResponse.getBytesPerSecond() != null) {
            StreamingResponseBody stream = throttleService.makeThrottledResponseStream(proxyResponse, proxyResponse.getBytesPerSecond());
            return ResponseEntity
                    .status(proxyResponse.getStatusCode())
                    .headers(filteredHeaders)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(stream);
        } else {
            ResponseEntity.BodyBuilder builder = ResponseEntity
                    .status(proxyResponse.getStatusCode())
                    .headers(filteredHeaders);

            if (proxyResponse.getContentType() != null) {
                MediaType mediaType = MediaType.parseMediaType(proxyResponse.getContentType());
                builder.contentType(mediaType);
            }
            // Return as StreamingResponseBody to maintain consistent return type
            return builder.body(outputStream -> {
                if (proxyResponse.getBody() != null) {
                    outputStream.write(proxyResponse.getBody());
                }
            });
        }
    }

    /**
     * Removes the Transfer-Encoding header from the response to avoid duplicate headers.
     * Removes Content-Type header as it is set separately in the ResponseEntity.
     * Removes Content-Length header if throttling is enabled, as the content length will change due
     */
    private HttpHeaders returnHttpHeadersForProxyResponse(HttpHeaders responseHeaders, boolean isThrottling) {
        HttpHeaders newHeaders = new HttpHeaders();
        for (Map.Entry<String, List<String>> entry : responseHeaders.headerSet()) {
            if (!"Transfer-Encoding".equalsIgnoreCase(entry.getKey()) &&
                    !(isThrottling && "Content-Length".equalsIgnoreCase(entry.getKey())) &&
                    !(isThrottling && "Content-Type".equalsIgnoreCase(entry.getKey()))) {
                newHeaders.addAll(entry.getKey(), entry.getValue());
            }
        }
        log.debug("Filtered response headers: {}", newHeaders);
        return newHeaders;
    }

    private ProxyRequest makeProxyRequest(TargetSystem targetSystem, HttpServletRequest request, String path, List<Scenario> scenarios, Map<String, String> headers, byte[] body) {
        String queryString = request.getQueryString();
        String url = path + (queryString != null ? "?" + queryString : "");

        long timeoutMillis = scenarios.stream()
                .filter(s -> s.getTimeoutMs() != null && s.getTimeoutMs() > 0)
                .mapToLong(Scenario::getTimeoutMs)
                .min()
                .orElse(targetSystem.getTimeoutMs());

        boolean followRedirect = scenarios.stream()
                .map(Scenario::getFollowRedirect)
                .filter(Objects::nonNull)
                .anyMatch(Boolean::booleanValue) ||
                scenarios.stream().allMatch(s -> s.getFollowRedirect() == null) && targetSystem.isFollowRedirect();

        return new ProxyRequest(
                targetSystem.getTargetBaseUrl(),
                url,
                request.getMethod(),
                body,
                headers,
                timeoutMillis,
                followRedirect
        );
    }

    private ProxyResponse makeProxyResponse(List<Scenario> scenarios) {
        ProxyResponse proxyResponse = new ProxyResponse();

        OptionalLong responseBytesPerSecond = scenarios.stream()
                .filter(s -> s.getResponseBytesPerSecond() != null && s.getResponseBytesPerSecond() > 0)
                .mapToLong(Scenario::getResponseBytesPerSecond)
                .min();
        if (responseBytesPerSecond.isPresent()) {
            proxyResponse.setBytesPerSecond(responseBytesPerSecond.getAsLong());
        }

        return proxyResponse;
    }

}
