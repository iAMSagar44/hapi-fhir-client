package dev.sagar.hapi_fhir_client.task;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import static dev.sagar.hapi_fhir_client.config.IdentifierSystem.forIdentifier;

@Service
public class TaskClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(TaskClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;

        public TaskClientService(IGenericClient fhirClient, FhirContext fhirContext) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
        }

        // Retrieve tasks of a patient by Medicare Card number identifier
        // Example Identifier: http://ns.electronichealth.net.au/id/medicare-number|1234567890
        String getTasksByIdentifier(String identifier) {
                logger.info("Fetching tasks with identifier: {}", identifier);
                Bundle taskBundle = this.fhirClient.search().forResource(Task.class)
                                .where(Task.PATIENT.hasChainedProperty(Patient.IDENTIFIER.exactly()
                                                .systemAndIdentifier(forIdentifier(identifier),
                                                                identifier)))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(taskBundle);

        }

}
