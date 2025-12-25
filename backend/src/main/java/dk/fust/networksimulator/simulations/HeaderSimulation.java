package dk.fust.networksimulator.simulations;

import dk.fust.networksimulator.model.ScenarioHeader;
import dk.fust.networksimulator.service.proxy.ProxyRequest;
import dk.fust.networksimulator.service.proxy.ProxyResponse;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeaderSimulation implements Simulation{

    private final List<ScenarioHeader> headersToAdd = new ArrayList<>();

    public void addHeaders(List<ScenarioHeader> headers) {
        this.headersToAdd.addAll(headers);
    }

    @Override
    public void doSimulation(ProxyRequest proxyRequest, ProxyResponse proxyResponse, SimulationChain chain) throws IOException {
        HttpHeaders headers = proxyResponse.getHeaders();
        if (headers == null) {
            headers = new HttpHeaders();
            proxyResponse.setHeaders(headers);
        }
        for (ScenarioHeader header : headersToAdd) {
            if (header.getHeaderReplaceValue() != null && header.getHeaderReplaceValue()) {
                headers.remove(header.getHeaderName());
            }
            headers.add(header.getHeaderName(), header.getHeaderValue());
        }
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

}
