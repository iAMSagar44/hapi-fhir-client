package dev.sagar.hapi_fhir_client.observation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/observation")
public class ObservationClientController {

    private final ObservationClientService observationClientService;

    public ObservationClientController(ObservationClientService observationClientService) {
        this.observationClientService = observationClientService;
    }

    @GetMapping("")
    public String getObservationsByPatientIdentifier(@RequestParam String identifier) {
        return observationClientService.getObservationsByIdentifier(identifier);
    }

}
