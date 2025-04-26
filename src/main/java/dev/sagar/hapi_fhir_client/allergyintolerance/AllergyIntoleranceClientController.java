package dev.sagar.hapi_fhir_client.allergyintolerance;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/allergyintolerance")
public class AllergyIntoleranceClientController {

    private final AllergyIntoleranceClientService allergyIntoleranceClientService;

    public AllergyIntoleranceClientController(
            AllergyIntoleranceClientService allergyIntoleranceClientService) {
        this.allergyIntoleranceClientService = allergyIntoleranceClientService;
    }


    @GetMapping("")
    public String getAllergyIntoleranceByPatientIdentifier(@RequestParam String identifier) {
        return allergyIntoleranceClientService.getAllergiesByIdentifier(identifier);
    }

}
