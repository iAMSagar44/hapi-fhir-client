package dev.sagar.hapi_fhir_client.ips;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ips")
public class IpsClientController {

    private final IpsClientService ipsClientService;

    public IpsClientController(IpsClientService ipsClientService) {
        this.ipsClientService = ipsClientService;
    }

    @GetMapping("")
    public String getIps(@RequestParam String identifier) {
        return ipsClientService.generateIps(identifier);
    }

}
