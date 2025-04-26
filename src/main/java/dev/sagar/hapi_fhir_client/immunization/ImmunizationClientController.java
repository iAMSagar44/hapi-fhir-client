package dev.sagar.hapi_fhir_client.immunization;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/immunization")
public class ImmunizationClientController {

    private final ImmunizationClientService immunizationClientService;

    public ImmunizationClientController(ImmunizationClientService immunizationClientService) {
        this.immunizationClientService = immunizationClientService;
    }

    @GetMapping("")
    public String getImmunizationsByPatientIdentifier(@RequestParam String identifier) {
        return immunizationClientService.getImmunizationsByIdentifier(identifier);
    }

}
