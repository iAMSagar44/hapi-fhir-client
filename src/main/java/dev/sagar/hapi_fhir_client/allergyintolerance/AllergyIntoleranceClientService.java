package dev.sagar.hapi_fhir_client.allergyintolerance;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import static dev.sagar.hapi_fhir_client.config.IdentifierSystem.forIdentifier;

@Service
public class AllergyIntoleranceClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(AllergyIntoleranceClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;

        public AllergyIntoleranceClientService(IGenericClient fhirClient, FhirContext fhirContext) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
        }

        // Retrieve allergies of a patient by Medicare Card number identifier
        // Example Identifier: http://ns.electronichealth.net.au/id/medicare-number|1234567890

        String getAllergiesByIdentifier(String identifier) {
                logger.info("Fetching allergies with identifier: {}", identifier);
                Bundle allergyIntoleranceBundle = this.fhirClient.search()
                                .forResource(AllergyIntolerance.class)
                                .where(AllergyIntolerance.PATIENT.hasChainedProperty(
                                                Patient.IDENTIFIER.exactly().systemAndIdentifier(
                                                                forIdentifier(identifier),
                                                                identifier)))
                                .and(AllergyIntolerance.CLINICAL_STATUS.exactly().code("active"))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(allergyIntoleranceBundle);

        }

}
