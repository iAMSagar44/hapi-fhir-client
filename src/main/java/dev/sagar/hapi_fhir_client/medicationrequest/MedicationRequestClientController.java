package dev.sagar.hapi_fhir_client.medicationrequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicationrequest")
public class MedicationRequestClientController {

    private final MedicationRequestClientService medicationRequestClientService;

    public MedicationRequestClientController(
            MedicationRequestClientService medicationRequestClientService) {
        this.medicationRequestClientService = medicationRequestClientService;
    }

    @GetMapping("")
    public String getMedicationRequestsByPatientIdentifier(@RequestParam String identifier) {
        return medicationRequestClientService.getMedicationRequestsByIdentifier(identifier);
    }

}
