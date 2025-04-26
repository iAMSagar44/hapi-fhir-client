package dev.sagar.hapi_fhir_client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@Configuration
public class FHIRConfiguration {

    @Value("${fhir.server.base.url}")
    private String fhirServerUrl;

    @Bean
    public FhirContext fhirContext() {
        // Create a FHIR context for the desired FHIR version
        return FhirContext.forR4();
    }

    @Bean
    public IGenericClient fhirClient(FhirContext fhirContext) {
        // Create a FHIR client using the provided FHIR context and server URL
        return fhirContext.newRestfulGenericClient(fhirServerUrl);
    }

}
