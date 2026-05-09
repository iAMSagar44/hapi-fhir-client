package dev.sagar.hapi_fhir_client.servicerequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/servicerequest")
public class ServiceRequestClientController {

    private final ServiceRequestClientService serviceRequestClientService;

    public ServiceRequestClientController(ServiceRequestClientService serviceRequestClientService) {
        this.serviceRequestClientService = serviceRequestClientService;
    }

    @GetMapping("")
    public String getServiceRequestsByPatientIdentifier(@RequestParam String identifier) {
        return serviceRequestClientService.getServiceRequestsByIdentifier(identifier);
    }

}
