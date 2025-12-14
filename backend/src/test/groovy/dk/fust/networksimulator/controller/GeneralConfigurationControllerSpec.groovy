package dk.fust.networksimulator.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class GeneralConfigurationControllerSpec extends Specification {

    @Autowired(required = false)
    MockMvc mvc

    def "read default configuration"() {
        expect:
        mvc.perform(MockMvcRequestBuilders
                .get('/_/api/v1/general-configuration')
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.id').value('1'))
                .andExpect(jsonPath('$.targetBaseUrl').value(''))
    }

    def "update configuration"() {
        expect:
        mvc.perform(MockMvcRequestBuilders
                .put('/_/api/v1/general-configuration')
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"targetBaseUrl":"http://example.com/api", "timeoutMs": 5000, "followRedirect":true}')
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.id').value('1'))
                .andExpect(jsonPath('$.targetBaseUrl').value('http://example.com/api'))
    }

}
