package dev.sagar.hapi_fhir_client.encounter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@Service
public class EncounterClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(EncounterClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;

        private static final String MEDICARE_IDENTIFIER_SYSTEM =
                        "http://ns.electronichealth.net.au/id/medicare-number";

        public EncounterClientService(IGenericClient fhirClient, FhirContext fhirContext) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
        }

        // Retrieve encounters of a patient by Medicare Card number identifier
        // Example Identifier: http://ns.electronichealth.net.au/id/medicare-number|1234567890
        String getEncountersByIdentifier(String identifier) {
                logger.info("Fetching encounters with identifier: {}", identifier);
                Bundle encounterBundle = this.fhirClient.search().forResource(Encounter.class)
                                .where(Encounter.PATIENT.hasChainedProperty(
                                                Patient.IDENTIFIER.exactly().systemAndIdentifier(
                                                                MEDICARE_IDENTIFIER_SYSTEM,
                                                                identifier)))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(encounterBundle);

        }

}
