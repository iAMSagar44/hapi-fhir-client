package dev.sagar.hapi_fhir_client.encounter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/encounter")
public class EncounterClientController {

    private final EncounterClientService encounterClientService;

    public EncounterClientController(EncounterClientService encounterClientService) {
        this.encounterClientService = encounterClientService;
    }

    @GetMapping("")
    public String getEncountersByPatientIdentifier(@RequestParam String identifier) {
        return encounterClientService.getEncountersByIdentifier(identifier);
    }

}
