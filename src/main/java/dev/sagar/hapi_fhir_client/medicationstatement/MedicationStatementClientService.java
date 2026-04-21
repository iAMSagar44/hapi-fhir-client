package dev.sagar.hapi_fhir_client.medicationstatement;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@Service
public class MedicationStatementClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(MedicationStatementClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;

        private static final String MEDICARE_IDENTIFIER_SYSTEM =
                        "http://ns.electronichealth.net.au/id/medicare-number";

        public MedicationStatementClientService(IGenericClient fhirClient,
                        FhirContext fhirContext) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
        }

        // Retrieve medication statements of a patient by Medicare Card number identifier
        // Example Identifier: http://ns.electronichealth.net.au/id/medicare-number|1234567890
        String getMedicationStatementsByIdentifier(String identifier) {
                logger.info("Fetching medication statements with identifier: {}", identifier);
                Bundle medicationStatementBundle = this.fhirClient.search()
                                .forResource(MedicationStatement.class)
                                .where(MedicationStatement.PATIENT.hasChainedProperty(
                                                Patient.IDENTIFIER.exactly().systemAndIdentifier(
                                                                MEDICARE_IDENTIFIER_SYSTEM,
                                                                identifier)))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(medicationStatementBundle);

        }

}
