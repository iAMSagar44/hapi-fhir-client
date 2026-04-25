package dev.sagar.hapi_fhir_client.immunization;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import static dev.sagar.hapi_fhir_client.config.IdentifierSystem.forIdentifier;

@Service
public class ImmunizationClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(ImmunizationClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;

        public ImmunizationClientService(IGenericClient fhirClient, FhirContext fhirContext) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
        }

        // Retrieve immunizations of a patient by Medicare Card number identifier
        // Example Identifier: http://ns.electronichealth.net.au/id/medicare-number|1234567890

        String getImmunizationsByIdentifier(String identifier) {
                logger.info("Fetching immunizations with identifier: {}", identifier);
                Bundle immunizationBundle = this.fhirClient.search().forResource(Immunization.class)
                                .where(Immunization.PATIENT.hasChainedProperty(
                                                Patient.IDENTIFIER.exactly().systemAndIdentifier(
                                                                forIdentifier(identifier),
                                                                identifier)))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(immunizationBundle);

        }

}
