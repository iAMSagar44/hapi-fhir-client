package dev.sagar.hapi_fhir_client.goal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goal")
public class GoalClientController {

    private final GoalClientService goalClientService;

    public GoalClientController(GoalClientService goalClientService) {
        this.goalClientService = goalClientService;
    }

    @GetMapping("")
    public String getGoalsByPatientIdentifier(@RequestParam String identifier) {
        return goalClientService.getGoalsByIdentifier(identifier);
    }

}
