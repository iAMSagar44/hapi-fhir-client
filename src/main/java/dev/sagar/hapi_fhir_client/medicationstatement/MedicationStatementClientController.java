package dev.sagar.hapi_fhir_client.medicationstatement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicationstatement")
public class MedicationStatementClientController {

    private final MedicationStatementClientService medicationStatementClientService;

    public MedicationStatementClientController(
            MedicationStatementClientService medicationStatementClientService) {
        this.medicationStatementClientService = medicationStatementClientService;
    }

    @GetMapping("")
    public String getMedicationStatementsByPatientIdentifier(@RequestParam String identifier) {
        return medicationStatementClientService.getMedicationStatementsByIdentifier(identifier);
    }

}
