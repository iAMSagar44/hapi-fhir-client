package dev.sagar.hapi_fhir_client.condition;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/condition")
public class ConditionClientController {

    private final ConditionClientService conditionClientService;

    public ConditionClientController(ConditionClientService conditionClientService) {
        this.conditionClientService = conditionClientService;
    }

    @GetMapping("")
    public String getConditionsByPatientIdentifier(@RequestParam String identifier) {
        return conditionClientService.getConditionsByIdentifier(identifier);
    }

}
