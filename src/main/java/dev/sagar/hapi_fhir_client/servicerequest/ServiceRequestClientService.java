package dev.sagar.hapi_fhir_client.servicerequest;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import static dev.sagar.hapi_fhir_client.config.IdentifierSystem.forIdentifier;

@Service
public class ServiceRequestClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(ServiceRequestClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;

        public ServiceRequestClientService(IGenericClient fhirClient, FhirContext fhirContext) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
        }

        // Retrieve service requests of a patient by Medicare Card number identifier
        // Example Identifier: http://ns.electronichealth.net.au/id/medicare-number|1234567890
        String getServiceRequestsByIdentifier(String identifier) {
                logger.info("Fetching service requests with identifier: {}", identifier);
                Bundle serviceRequestBundle = this.fhirClient.search().forResource(ServiceRequest.class)
                                .where(ServiceRequest.PATIENT.hasChainedProperty(Patient.IDENTIFIER.exactly()
                                                .systemAndIdentifier(forIdentifier(identifier),
                                                                identifier)))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(serviceRequestBundle);

        }

}
