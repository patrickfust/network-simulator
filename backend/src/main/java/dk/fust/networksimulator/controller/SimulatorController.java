package dk.fust.networksimulator.controller;

import dk.fust.networksimulator.model.GeneralConfiguration;
import dk.fust.networksimulator.model.Scenario;
import dk.fust.networksimulator.service.GeneralConfigurationService;
import dk.fust.networksimulator.service.ProxyService;
import dk.fust.networksimulator.service.ScenarioService;
import dk.fust.networksimulator.service.SimulatorService;
import dk.fust.networksimulator.service.proxy.ProxyRequest;
import dk.fust.networksimulator.service.proxy.ProxyResponse;
import dk.fust.networksimulator.simulations.SimulationChain;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/")
@AllArgsConstructor
@Hidden
public class SimulatorController {

    private final ProxyService proxyService;
    private final ScenarioService scenarioService;
    private final GeneralConfigurationService generalConfigurationService;
    private final SimulatorService simulatorService;

    /**
     * Method for handling requests, we want to proxy to the target application.
     * Handles all paths except those starting with "_", which are reserved for internal use, like api and swagger.
     *
     * @param headers the headers send by the client to this proxy.
     * @param request telling spring that we want access to the servlet request object.
     * @param body   the body send by the client to this proxy.
     * @return what ever the target application, returned to this proxy.
     */
    @RequestMapping(path = {"/", "{path:^(?!_).*}/**"}, produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getRoot(@RequestHeader Map<String, String> headers, HttpServletRequest request,
                                     @RequestBody(required = false) byte[] body) throws IOException {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        List<Scenario> scenarios = scenarioService.findScenariosByPath(path).orElseThrow();
        ProxyRequest proxyRequest = makeProxyRequest(request, path, scenarios, headers, body);

        log.info("forwarding request to target application. Scenarios: {}, URL: {}", scenarios.size(), proxyRequest);

        ProxyResponse proxyResponse = new ProxyResponse();
        SimulationChain simulationChain = simulatorService.makeSimulationChain(scenarios);
        simulationChain.doSimulation(proxyRequest, proxyResponse);
        return ResponseEntity
                .status(proxyResponse.getStatusCode())
                .headers(proxyResponse.getHeaders())
                .body(proxyResponse.getBody());
    }

    private ProxyRequest makeProxyRequest(HttpServletRequest request, String path, List<Scenario> scenarios, Map<String, String> headers, byte[] body) {
        String queryString = request.getQueryString();
        String url = path + (queryString != null ? "?" + queryString : "");

        GeneralConfiguration generalConfig = generalConfigurationService.getGeneralConfiguration();
        long timeoutMillis = scenarios.stream()
                .filter(s -> s.getTimeoutMs() != null && s.getTimeoutMs() > 0)
                .mapToLong(Scenario::getTimeoutMs)
                .min()
                .orElse(generalConfig.getTimeoutMs());
        boolean followRedirect = scenarios.stream()
                .map(Scenario::getFollowRedirect)
                .filter(Objects::nonNull)
                .anyMatch(Boolean::booleanValue) ||
                scenarios.stream().allMatch(s -> s.getFollowRedirect() == null) && generalConfig.isFollowRedirect();

        return new ProxyRequest(
                generalConfig.getTargetBaseUrl(),
                url,
                request.getMethod(),
                body,
                headers,
                timeoutMillis,
                followRedirect
        );
    }

}
