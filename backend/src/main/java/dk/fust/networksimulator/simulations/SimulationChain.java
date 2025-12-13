package dk.fust.networksimulator.simulations;

import dk.fust.networksimulator.service.proxy.ProxyRequest;
import dk.fust.networksimulator.service.proxy.ProxyResponse;

import java.io.IOException;

public interface SimulationChain {

    void doSimulation(ProxyRequest proxyRequest, ProxyResponse proxyResponse) throws IOException;

}
