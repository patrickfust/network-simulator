package dk.fust.networksimulator.controller

import dk.fust.networksimulator.model.TargetSystem
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Stepwise
class TargetSystemControllerSpec extends Specification {

    @Autowired(required = false)
    MockMvc mockMvc

    @Shared
    Long targetSystemId

    def "create target system"() {
        given:
        TargetSystem targetSystem = new TargetSystem(systemName: 'Test System', targetBaseUrl: 'http://example.com', timeoutMs: 3000)

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.
                post('/api/v1/target-systems')
                .contentType("application/json")
                .content(new JsonBuilder(targetSystem).toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.systemName').value('Test System'))
                .andReturn().response
        Map<String, Long> created = new JsonSlurper().parseText(response.contentAsString)
        targetSystemId = created.id

        then:
        targetSystemId
    }

    def "update target system"() {
        given:
        TargetSystem targetSystem = new TargetSystem(systemName: 'Test System 2', targetBaseUrl: 'http://example2.com', timeoutMs: 30000, followRedirect: false)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.
                put("/api/v1/target-systems/${targetSystemId}")
                .contentType("application/json")
                .content(new JsonBuilder(targetSystem).toString()))
                .andExpect(jsonPath('$.id').value(targetSystemId))
                .andExpect(jsonPath('$.systemName').value('Test System 2'))
                .andExpect(jsonPath('$.targetBaseUrl').value('http://example2.com'))
                .andExpect(jsonPath('$.timeoutMs').value(30000))
                .andExpect(jsonPath('$.followRedirect').value(false))
    }

    def "delete target system"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.
                delete("/api/v1/target-systems/${targetSystemId}")
                .contentType("application/json"))
                .andExpect(status().isNoContent())
    }

}
