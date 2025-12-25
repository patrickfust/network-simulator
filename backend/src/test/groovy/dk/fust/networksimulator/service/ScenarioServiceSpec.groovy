package dk.fust.networksimulator.service

import dk.fust.networksimulator.model.Scenario
import dk.fust.networksimulator.repository.ScenarioRepository
import spock.lang.Specification

class ScenarioServiceSpec extends Specification {

    ScenarioRepository scenarioRepository = Mock()
    ScenarioService scenarioService = new ScenarioService(scenarioRepository)

    def "should get all scenarios"() {
        given: "two scenarios exist"
        def scenario1 = new Scenario(1L, true, "Test Scenario 1", "http://x", "Description 1", 100L, 100, null, 200L, true)
        def scenario2 = new Scenario(2L, true, "Test Scenario 2", "http://y", "Description 2", 200L, 404, null, null, null)

        when: "getting all scenarios"
        def scenarios = scenarioService.getAllScenarios()

        then: "both scenarios are returned"
        scenarios.size() == 2
        scenarios[0].name == "Test Scenario 1"
        scenarios[1].name == "Test Scenario 2"
        1 * scenarioRepository.findAll() >> [scenario1, scenario2]
    }

}
