package dev.sagar.hapi_fhir_client.careteam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/careteam")
public class CareTeamClientController {

    private final CareTeamClientService careTeamClientService;

    public CareTeamClientController(CareTeamClientService careTeamClientService) {
        this.careTeamClientService = careTeamClientService;
    }

    @GetMapping("")
    public String getCareTeamsByPatientIdentifier(@RequestParam String identifier) {
        return careTeamClientService.getCareTeamsByIdentifier(identifier);
    }

}
