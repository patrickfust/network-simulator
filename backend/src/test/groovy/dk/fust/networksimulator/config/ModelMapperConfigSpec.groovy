package dk.fust.networksimulator.config

import dk.fust.networksimulator.dto.ScenarioDto
import dk.fust.networksimulator.dto.TargetSystemDto
import dk.fust.networksimulator.model.Scenario
import dk.fust.networksimulator.model.TargetSystem
import dk.fust.networksimulator.repository.TargetSystemRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class ModelMapperConfigSpec extends Specification {

    @Autowired
    ModelMapper modelMapper

    @Autowired
    TargetSystemRepository targetSystemRepository

    def "Map TargetSystem"() {
        given:
        TargetSystem targetSystem = new TargetSystem(id: 1, systemName: "System A", targetBaseUrl: "https://system-a.com", timeoutMs: 1000)

        when:
        TargetSystemDto targetSystemDto = modelMapper.map(targetSystem, TargetSystemDto)

        then:
        targetSystemDto.id == targetSystem.id
        targetSystemDto.systemName == targetSystem.systemName
        targetSystemDto.targetBaseUrl == targetSystem.targetBaseUrl
        targetSystemDto.timeoutMs == targetSystem.timeoutMs
        targetSystemDto.followRedirect == targetSystem.followRedirect
    }

    def "Map Scenario"() {
        given:
        TargetSystem targetSystem = new TargetSystem(systemName: "System A", targetBaseUrl: "https://system-a.com", timeoutMs: 1000)
        targetSystemRepository.save(targetSystem)
        Scenario scenario = new Scenario(id: 1, name: "Test Scenario", path: "/test", description: "A test scenario", latencyMs: 200,
                statusCode: 200, responseBody: "OK", timeoutMs: 5000, followRedirect: true, targetSystem: targetSystem)

        when:
        ScenarioDto scenarioDto = modelMapper.map(scenario, ScenarioDto)

        then:
        scenarioDto.id == scenario.id
        scenarioDto.name == scenario.name
        scenarioDto.path == scenario.path
        scenarioDto.description == scenario.description
        scenarioDto.latencyMs == scenario.latencyMs
        scenarioDto.statusCode == scenario.statusCode
        scenarioDto.responseBody == scenario.responseBody
        scenarioDto.timeoutMs == scenario.timeoutMs
        scenarioDto.followRedirect == scenario.followRedirect
        scenarioDto.targetSystemId == scenario.targetSystem.id

        when: 'mapping it back again'
        Scenario backAgain = modelMapper.map(scenarioDto, Scenario)

        then:
        backAgain.id == scenarioDto.id
        backAgain.name == scenarioDto.name
        backAgain.path == scenarioDto.path
        backAgain.description == scenarioDto.description
        backAgain.latencyMs == scenarioDto.latencyMs
        backAgain.statusCode == scenarioDto.statusCode
        backAgain.responseBody == scenarioDto.responseBody
        backAgain.timeoutMs == scenarioDto.timeoutMs
        backAgain.followRedirect == scenarioDto.followRedirect
        backAgain.targetSystem.id == scenarioDto.targetSystemId
        backAgain.targetSystem.systemName == targetSystem.systemName
    }

}
