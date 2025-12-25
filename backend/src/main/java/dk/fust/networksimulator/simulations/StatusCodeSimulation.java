package dk.fust.networksimulator.simulations;

import dk.fust.networksimulator.service.proxy.ProxyRequest;
import dk.fust.networksimulator.service.proxy.ProxyResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.io.IOException;

@AllArgsConstructor
public class StatusCodeSimulation implements Simulation {

    private final HttpStatusCode statusCode;
    private final byte[] body;

    @Override
    public void doSimulation(ProxyRequest proxyRequest, ProxyResponse proxyResponse, SimulationChain chain) throws IOException {
        proxyResponse.setStatusCode(statusCode);
        proxyResponse.setBody(body);
        chain.doSimulation(proxyRequest, proxyResponse);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 2; // Just before PassThroughSimulation
    }

}
