package dev.sagar.hapi_fhir_client.patient;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/patient")
public class PatientClientController {

    private final PatientClientService patientClientService;

    public PatientClientController(PatientClientService patientClientService) {
        this.patientClientService = patientClientService;
    }

    @GetMapping("/{id}")
    public String getPatientById(@PathVariable String id) {
        return patientClientService.getPatientById(id);
    }

    @GetMapping("")
    public String getPatientByIdentifier(@RequestParam(required = false) String identifier,
            @RequestParam(required = false) String familyName,
            @RequestParam(required = false) String birthDate) {
        if (identifier != null) {
            return patientClientService.getPatientByIdentifier(identifier);
        } else if (familyName != null && birthDate != null) {
            return patientClientService.getPatientByFamilyNameAndDOB(familyName, birthDate);
        } else {
            return "Please provide either identifier or family name and birth date.";
        }
    }


}
