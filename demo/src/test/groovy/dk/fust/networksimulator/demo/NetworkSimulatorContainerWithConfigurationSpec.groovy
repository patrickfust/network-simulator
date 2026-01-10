package dk.fust.networksimulator.demo

import org.testcontainers.containers.GenericContainer
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Stepwise
class NetworkSimulatorContainerWithConfigurationSpec extends Specification {

    static DockerImageName IMAGE = DockerImageName.parse("patrickfust/network-simulator:latest")
    static GenericContainer<?> simulator = new GenericContainer<>(IMAGE)
            .withExposedPorts(9898)
            .withLogConsumer { logLine -> println("*** Network Simulator ***: ${logLine.utf8StringWithoutLineEnding}") }


    @Shared
    String host

    @Shared
    int port

    @Shared
    String base

    @Shared
    HttpClient client

    def setupSpec() {
        // Using configuration file instead
        simulator.addEnv('NETWORK_SIMULATOR_CONFIGURATION', new File('network-simulator-config.json').text)
        simulator.start()
        host = simulator.host
        port = simulator.getMappedPort(9898)
        base = "http://${host}:${port}"
        client = HttpClient.newHttpClient()
    }

    def cleanupSpec() {
        simulator.stop()
    }

    def "network simulator container starts"() {
        expect:
        simulator.running
    }

    def "call through network simulator to httpbin"() {
        when:
        def reqHttpbin = HttpRequest.newBuilder(URI.create("${base}/forward/httpbin/get"))
                .header("Content-Type", "application/json")
                .GET()
                .build()
        def resHttpbin = client.send(reqHttpbin, HttpResponse.BodyHandlers.ofString())

        then:
        resHttpbin.statusCode() == 200
        String json = resHttpbin.body()

        println json
    }

}
