package dev.sagar.hapi_fhir_client.procedure;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/procedure")
public class ProcedureClientController {

    private final ProcedureClientService procedureClientService;

    public ProcedureClientController(ProcedureClientService procedureClientService) {
        this.procedureClientService = procedureClientService;
    }

    @GetMapping("")
    public String getProceduresByPatientIdentifier(
            @RequestParam(required = false) String identifier) {
        return procedureClientService.getProceduresByIdentifier(identifier);
    }

}
