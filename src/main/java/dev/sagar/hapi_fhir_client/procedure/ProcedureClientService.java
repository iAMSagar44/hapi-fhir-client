package dev.sagar.hapi_fhir_client.procedure;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import static dev.sagar.hapi_fhir_client.config.IdentifierSystem.forIdentifier;

@Service
public class ProcedureClientService {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ProcedureClientService.class);

    private final IGenericClient fhirClient;
    private final FhirContext fhirContext;

    public ProcedureClientService(IGenericClient fhirClient, FhirContext fhirContext) {
        this.fhirClient = fhirClient;
        this.fhirContext = fhirContext;
    }

    String getProceduresByIdentifier(String identifier) {
        logger.info("Fetching procedures for identifier: {}", identifier);
        Bundle procedureBundle = this.fhirClient.search().forResource(Procedure.class)
                .where(Procedure.PATIENT.hasChainedProperty(Patient.IDENTIFIER.exactly()
                        .systemAndIdentifier(forIdentifier(identifier), identifier)))
                .returnBundle(Bundle.class).execute();
        return fhirContext.newJsonParser().setPrettyPrint(true)
                .encodeResourceToString(procedureBundle);

    }

}
