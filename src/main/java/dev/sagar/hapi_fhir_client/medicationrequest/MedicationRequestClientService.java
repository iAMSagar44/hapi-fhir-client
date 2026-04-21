package dev.sagar.hapi_fhir_client.medicationrequest;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@Service
public class MedicationRequestClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(MedicationRequestClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;

        private static final String MEDICARE_IDENTIFIER_SYSTEM =
                        "http://ns.electronichealth.net.au/id/medicare-number";

        public MedicationRequestClientService(IGenericClient fhirClient, FhirContext fhirContext) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
        }

        // Retrieve medication requests of a patient by Medicare Card number identifier
        // Example Identifier: http://ns.electronichealth.net.au/id/medicare-number|1234567890
        String getMedicationRequestsByIdentifier(String identifier) {
                logger.info("Fetching medication requests with identifier: {}", identifier);
                Bundle medicationRequestBundle = this.fhirClient.search()
                                .forResource(MedicationRequest.class)
                                .where(MedicationRequest.PATIENT.hasChainedProperty(
                                                Patient.IDENTIFIER.exactly().systemAndIdentifier(
                                                                MEDICARE_IDENTIFIER_SYSTEM,
                                                                identifier)))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(medicationRequestBundle);

        }

}
