package dev.sagar.hapi_fhir_client.patient;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@Service
public class PatientClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(PatientClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;

        private static final String MEDICARE_IDENTIFIER_SYSTEM =
                        "http://ns.electronichealth.net.au/id/medicare-number";

        public PatientClientService(IGenericClient fhirClient, FhirContext fhirContext) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
        }

        // Fetch patient by ID
        String getPatientById(String patientId) {
                logger.info("Fetching patient with ID: {}", patientId);
                // Use the fhirClient to fetch the patient by ID
                // Example: return
                // fhirClient.read().resource(Patient.class).withId(patientId).execute();
                Patient patientResource = this.fhirClient.read().resource(Patient.class)
                                .withId(patientId).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(patientResource);
        }

        // Fetch patient by Medicare Card number
        // Example Identifier: http://ns.electronichealth.net.au/id/medicare-number|1234567890
        String getPatientByIdentifier(String identifier) {
                logger.info("Fetching patient with identifier: {}", identifier);
                // Use the fhirClient to fetch the patient by identifier
                // Example: return
                // fhirClient.search().forResource(Patient.class).where(Patient.IDENTIFIER.exactly().code(identifier)).execute();
                Bundle patientResource = this.fhirClient.search().forResource(Patient.class)
                                .where(Patient.IDENTIFIER.exactly().systemAndIdentifier(
                                                MEDICARE_IDENTIFIER_SYSTEM, identifier))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(patientResource);
        }

        // Fetch patient by family name and date of birth
        String getPatientByFamilyNameAndDOB(String familyName, String dob) {
                logger.info("Fetching patient with family name: {} and DOB: {}", familyName, dob);

                if (familyName == null || familyName.isEmpty() || dob == null || dob.isEmpty()) {
                        logger.warn("Family name or date of birth is missing");
                        throw new IllegalArgumentException(
                                        "Family name and date of birth must be provided");
                }

                Bundle patientResource = this.fhirClient.search().forResource(Patient.class)
                                .where(Patient.FAMILY.matches().value(familyName))
                                .and(Patient.BIRTHDATE.exactly().day(dob))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(patientResource);
        }

}
