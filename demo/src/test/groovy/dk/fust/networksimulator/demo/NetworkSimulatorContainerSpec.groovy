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
class NetworkSimulatorContainerSpec extends Specification {

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

    @Shared
    int targetSystemId

    def setupSpec() {
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

    def "add target system"() {
        given:
        def targetJson = '''{
              "followRedirect": true,
              "systemName": "httpbin",
              "targetBaseUrl": "https://httpbin.org",
              "timeoutMs": 1000
            }'''

        when:
        def reqTarget = HttpRequest.newBuilder(URI.create("${base}/api/v1/target-systems"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(targetJson))
                .build()
        def resTarget = client.send(reqTarget, HttpResponse.BodyHandlers.ofString())
        String json = resTarget.body()

        def mapper = new ObjectMapper()
        def target = mapper.readTree(json)

        targetSystemId = target.path("id").asInt()

        then:
        resTarget.statusCode() == 201
        targetSystemId
    }

    def "add scenario"() {
        given:
        def scenarioJson = """
            {
              "description": "Simulates a response for downloads",
              "enableScenario": true,
              "followRedirect": true,
              "name": "Just for the test",
              "targetSystemId": $targetSystemId
            }
        """

        when:
        def reqScenario = HttpRequest.newBuilder(URI.create("${base}/api/v1/scenarios"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(scenarioJson))
                .build()
        def resScenario = client.send(reqScenario, HttpResponse.BodyHandlers.ofString())

        then:
        resScenario.statusCode() == 201
        String json = resScenario.body()

        def mapper = new ObjectMapper()
        def scenario = mapper.readTree(json)

        int scenarioId = scenario.path("id").asInt()
        scenarioId
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

    def "getting complete configuration"() {
        given:
        def resultFile = new File('network-simulator-config.json')
        if (resultFile.exists()) {
            resultFile.delete()
        }

        when:
        def reqConfig = HttpRequest.newBuilder(URI.create("${base}/api/v1/configuration"))
                .header("Content-Type", "application/json")
                .GET()
                .build()
        def resConfig = client.send(reqConfig, HttpResponse.BodyHandlers.ofString())

        def mapper = new ObjectMapper()
        def jsonNode = mapper.readTree(resConfig.body())
        def prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)

        resultFile << prettyJson

        then:
        resultFile.exists()
    }

}
