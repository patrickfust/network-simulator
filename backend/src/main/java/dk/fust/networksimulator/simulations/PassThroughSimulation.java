package dk.fust.networksimulator.simulations;

import dk.fust.networksimulator.service.ProxyService;
import dk.fust.networksimulator.service.proxy.ProxyRequest;
import dk.fust.networksimulator.service.proxy.ProxyResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class PassThroughSimulation implements Simulation {

    private final ProxyService proxyService;

    @Override
    public void doSimulation(ProxyRequest proxyRequest, ProxyResponse proxyResponse, SimulationChain chain) throws IOException {
        log.info("Passing through request to target application: {} {}", proxyRequest.getMethod(), proxyRequest.getPath());
        proxyService.sendRequest(proxyRequest, proxyResponse);
        chain.doSimulation(proxyRequest, proxyResponse);
    }

    @Override
    public int getOrder() {
        // Must be last in the chain
        return Integer.MAX_VALUE;
    }

}
