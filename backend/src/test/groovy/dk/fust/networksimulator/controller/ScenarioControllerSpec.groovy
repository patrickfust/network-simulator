package dk.fust.networksimulator.controller

import dk.fust.networksimulator.model.Scenario
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import groovy.json.JsonBuilder

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Stepwise
class ScenarioControllerSpec extends Specification {

    @Autowired(required = false)
    MockMvc mockMvc

    @Shared
    Long scenarioId

    def "create scenario"() {
        given:
        Scenario scenario = new Scenario(name: "Test Scenario", path: "/test", description: "desc", latencyMs: 100, statusCode: 200, responseBody: "ok", timeoutMs: 500, followRedirect: true)

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.
            post('/api/v1/scenarios')
                .contentType("application/json")
                .content(new JsonBuilder(scenario).toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.name').value('Test Scenario'))
                .andReturn().response
        Map<String, Long> created = new JsonSlurper().parseText(response.contentAsString)
        scenarioId = created.id

        then:
        scenarioId
    }

    def "update scenario"() {
        given:
        Scenario scenario = new Scenario([name: "Updated", path: "/update", description: "desc", latencyMs: 50, statusCode: 201, responseBody: "body", timeoutMs: 200, followRedirect: false])

        expect:
        mockMvc.perform(MockMvcRequestBuilders.
                put("/api/v1/scenarios/${scenarioId}")
                .contentType("application/json")
                .content(new JsonBuilder(scenario).toString()))
                .andExpect(jsonPath('$.id').value(scenarioId))
                .andExpect(jsonPath('$.name').value('Updated'))
                .andExpect(jsonPath('$.responseBody').value('body'))
                .andExpect(jsonPath('$.headers.length()').value(0))
    }

    def "update scenario with headers"() {
        given:
        def scenario = [name: "With Headers", path: "/headers", description: "desc",
                        headers: [[headerName: "X-Test", headerValue: "123", headerReplaceValue: true],
                                  [headerName: "Y-Test", headerValue: "456", headerReplaceValue: false]]]

        expect:
        mockMvc.perform(MockMvcRequestBuilders.
            put("/api/v1/scenarios/${scenarioId}")
                .contentType("application/json")
                .content(new JsonBuilder(scenario).toString()))
                .andExpect(jsonPath('$.id').value(scenarioId))
                .andExpect(jsonPath('$.name').value('With Headers'))
                .andExpect(jsonPath('$.responseBody').doesNotExist())
                .andExpect(jsonPath('$.headers.length()').value(2))
                .andExpect(jsonPath('$.headers[0].headerName').value('X-Test'))
                .andExpect(jsonPath('$.headers[0].headerValue').value('123'))
                .andExpect(jsonPath('$.headers[0].headerReplaceValue').value(true))
                .andExpect(jsonPath('$.headers[1].headerName').value('Y-Test'))
                .andExpect(jsonPath('$.headers[1].headerValue').value('456'))
                .andExpect(jsonPath('$.headers[1].headerReplaceValue').value(false))
    }

    def "update scenario with headers - remove one and add one"() {
        given:
        def scenario = [name: "With Headers", path: "/headers", description: "desc",
                        headers: [[headerName: "X-Test", headerValue: "123", headerReplaceValue: false],
                                  [headerName: "YZ-Test", headerValue: "789", headerReplaceValue: true]]]

        expect:
        mockMvc.perform(MockMvcRequestBuilders.
            put("/api/v1/scenarios/${scenarioId}")
                .contentType("application/json")
                .content(new JsonBuilder(scenario).toString()))
                .andExpect(jsonPath('$.id').value(scenarioId))
                .andExpect(jsonPath('$.name').value('With Headers'))
                .andExpect(jsonPath('$.responseBody').doesNotExist())
                .andExpect(jsonPath('$.headers.length()').value(2))
                .andExpect(jsonPath('$.headers[0].headerName').value('X-Test'))
                .andExpect(jsonPath('$.headers[0].headerValue').value('123'))
                .andExpect(jsonPath('$.headers[0].headerReplaceValue').value(false))
                .andExpect(jsonPath('$.headers[1].headerName').value('YZ-Test'))
                .andExpect(jsonPath('$.headers[1].headerValue').value('789'))
                .andExpect(jsonPath('$.headers[1].headerReplaceValue').value(true))
    }

    def "activate"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.
                get("/api/v1/scenarios/${scenarioId}/activate")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.enableScenario').value(true))
    }

    def "deactivate"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.
                get("/api/v1/scenarios/${scenarioId}/deactivate")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.enableScenario').value(false))
    }

    def "delete scenario"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.
                delete("/api/v1/scenarios/${scenarioId}")
                .contentType("application/json"))
                .andExpect(status().isNoContent())
    }

}
