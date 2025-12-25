package dk.fust.networksimulator.controller;

import dk.fust.networksimulator.model.Scenario;
import dk.fust.networksimulator.model.TargetSystem;
import dk.fust.networksimulator.service.ProxyService;
import dk.fust.networksimulator.service.ScenarioService;
import dk.fust.networksimulator.service.SimulatorService;
import dk.fust.networksimulator.service.TargetSystemService;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/forward")
@AllArgsConstructor
@Hidden
public class SimulatorController {

    private final ProxyService proxyService;
    private final ScenarioService scenarioService;
    private final SimulatorService simulatorService;
    private final TargetSystemService targetSystemService;

    /**
     * Method for handling requests, we want to proxy to the target application.
     * Handles all paths except those starting with "_", which are reserved for internal use, like api and swagger.
     *
     * @param headers the headers send by the client to this proxy.
     * @param request telling spring that we want access to the servlet request object.
     * @param body   the body send by the client to this proxy.
     * @return what ever the target application, returned to this proxy.
     */
    @RequestMapping(path = {"/{systemName}/**"}, produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getRoot(@PathVariable String systemName,
                                     @RequestHeader Map<String, String> headers, HttpServletRequest request,
                                     @RequestBody(required = false) byte[] body) throws IOException {
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String path = fullPath.substring(9 + systemName.length());
        log.info("received request to proxy. Method: {}, Path: {}", request.getMethod(), path);

        TargetSystem targetSystem = targetSystemService.getTargetSystemByName(systemName).orElseThrow(() -> new RuntimeException("There are no target system with name: " + systemName));
        List<Scenario> scenarios = scenarioService.findScenariosByPath(path).orElseThrow();
        ProxyRequest proxyRequest = makeProxyRequest(targetSystem, request, path, scenarios, headers, body);

        log.debug("forwarding request to target application. Scenarios: {}, ProxyRequest: {}", scenarios.size(), proxyRequest);

        ProxyResponse proxyResponse = new ProxyResponse();
        SimulationChain simulationChain = simulatorService.makeSimulationChain(scenarios);
        simulationChain.doSimulation(proxyRequest, proxyResponse);
        String hasBody = proxyResponse.getBody() != null && proxyResponse.getBody().length > 0 ? "with body" : "without body";
        log.debug("ProxyResponse: {}", proxyResponse);
        log.info("received status code: {} ({}) from target/simulations, for path: {}", proxyResponse.getStatusCode(), hasBody, path);
        return ResponseEntity
                .status(proxyResponse.getStatusCode())
                .headers(returnHttpHeadersForProxyResponse(proxyResponse.getHeaders()))
                .body(proxyResponse.getBody());
    }

    /**
     * Removes the Transfer-Encoding header from the response to avoid duplicate headers
     */
    private HttpHeaders returnHttpHeadersForProxyResponse(HttpHeaders responseHeaders) {
        HttpHeaders newHeaders = new HttpHeaders();
        newHeaders.addAll(responseHeaders);
        newHeaders.remove("Transfer-Encoding");
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

}
