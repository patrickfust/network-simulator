package dk.fust.networksimulator.simulations;

import dk.fust.networksimulator.service.proxy.ProxyRequest;
import dk.fust.networksimulator.service.proxy.ProxyResponse;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class SimulationChainDecorator implements SimulationChain {

    private final Set<Simulation> simulations = new TreeSet<>(Comparator.comparing(Simulation::getOrder));

    private Iterator<Simulation> iterator;

    public void addSimulation(Simulation simulation) {
        simulations.add(simulation);
    }

    public Simulation getSimulationOfClass(Class<? extends Simulation> simulationClass) {
        for (Simulation simulation : simulations) {
            if (simulation.getClass() == simulationClass) {
                return simulation;
            }
        }
        return null;
    }

    @Override
    public void doSimulation(ProxyRequest proxyRequest, ProxyResponse proxyResponse) throws IOException {
        if (iterator == null) {
            iterator = simulations.iterator();
        }

        if (iterator.hasNext()) {
            Simulation simulation = iterator.next();
            simulation.doSimulation(proxyRequest, proxyResponse, this);
        }
    }

}
