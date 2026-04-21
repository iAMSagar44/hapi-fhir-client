package dev.sagar.hapi_fhir_client.condition;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@Service
public class ConditionClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(ConditionClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;

        private static final String MEDICARE_IDENTIFIER_SYSTEM =
                        "http://ns.electronichealth.net.au/id/medicare-number";

        public ConditionClientService(IGenericClient fhirClient, FhirContext fhirContext) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
        }

        // Retrieve conditions of a patient by Medicare Card number identifier
        // Example Identifier: http://ns.electronichealth.net.au/id/medicare-number|1234567890
        String getConditionsByIdentifier(String identifier) {
                logger.info("Fetching conditions with identifier: {}", identifier);
                Bundle conditionBundle = this.fhirClient.search().forResource(Condition.class)
                                .where(Condition.PATIENT.hasChainedProperty(
                                                Patient.IDENTIFIER.exactly().systemAndIdentifier(
                                                                MEDICARE_IDENTIFIER_SYSTEM,
                                                                identifier)))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(conditionBundle);

        }

}
