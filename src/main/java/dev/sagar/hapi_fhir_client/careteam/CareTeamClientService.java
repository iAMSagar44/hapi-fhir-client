package dev.sagar.hapi_fhir_client.careteam;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import static dev.sagar.hapi_fhir_client.config.IdentifierSystem.forIdentifier;

@Service
public class CareTeamClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(CareTeamClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;

        public CareTeamClientService(IGenericClient fhirClient, FhirContext fhirContext) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
        }

        // Retrieve care teams of a patient by Medicare Card number identifier
        // Example Identifier: http://ns.electronichealth.net.au/id/medicare-number|1234567890
        String getCareTeamsByIdentifier(String identifier) {
                logger.info("Fetching care teams with identifier: {}", identifier);
                Bundle careTeamBundle = this.fhirClient.search().forResource(CareTeam.class)
                                .where(CareTeam.PATIENT.hasChainedProperty(
                                                Patient.IDENTIFIER.exactly().systemAndIdentifier(
                                                                forIdentifier(identifier),
                                                                identifier)))
                                .returnBundle(Bundle.class).execute();
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(careTeamBundle);

        }

}
