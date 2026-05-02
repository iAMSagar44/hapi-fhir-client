package dev.sagar.hapi_fhir_client.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import java.io.IOException;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FHIRConfiguration {

    @Value("${fhir.server.base.url}")
    private String fhirServerUrl;

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    @Bean
    public IGenericClient fhirClient(FhirContext fhirContext) {
        return fhirContext.newRestfulGenericClient(fhirServerUrl);
    }

    @Bean
    public FhirValidator fhirValidator(FhirContext fhirContext) throws IOException {
        NpmPackageValidationSupport npmPackageSupport =
                new NpmPackageValidationSupport(fhirContext);
        npmPackageSupport.loadPackageFromClasspath("package/package.tgz");
        ValidationSupportChain chain = new ValidationSupportChain(npmPackageSupport,
                new DefaultProfileValidationSupport(fhirContext),
                new InMemoryTerminologyServerValidationSupport(fhirContext),
                new CommonCodeSystemsTerminologyService(fhirContext),
                new SnapshotGeneratingValidationSupport(fhirContext));

        FhirInstanceValidator instanceValidator = new FhirInstanceValidator(chain);
        // Skip external terminology server lookups — LOINC/SNOMED codes are
        // structurally valid; full terminology validation requires a server.
        instanceValidator.setNoTerminologyChecks(true);
        instanceValidator.setAnyExtensionsAllowed(true);
        // Downgrade unknown profile references to warnings so that IPS profile URLs
        // not resolvable in the loaded NPM packages do not cause a 422 response.
        instanceValidator.setErrorForUnknownProfiles(false);

        FhirValidator validator = fhirContext.newValidator();
        validator.registerValidatorModule(instanceValidator);
        return validator;
    }
}
