package dev.sagar.hapi_fhir_client.task;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PreferReturnEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import static dev.sagar.hapi_fhir_client.config.IdentifierSystem.forIdentifier;

@Service
public class TaskClientService {
        private static final org.slf4j.Logger logger =
                        org.slf4j.LoggerFactory.getLogger(TaskClientService.class);

        private final IGenericClient fhirClient;
        private final FhirContext fhirContext;
        private final JsonMapper jsonMapper;

        public TaskClientService(IGenericClient fhirClient, FhirContext fhirContext,
                        JsonMapper jsonMapper) {
                this.fhirClient = fhirClient;
                this.fhirContext = fhirContext;
                this.jsonMapper = jsonMapper;
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

        // Apply a JSON Patch (RFC 6902) to a Task: update status and/or append
        // notes, inputs, outputs. Reads the current resource first so that we
        // emit the correct op for absent arrays (add the array vs append to it).
        String updateTask(String taskId, TaskUpdateRequest request) {
                logger.info("Patching task {}", taskId);

                Task existing = fhirClient.read().resource(Task.class)
                                .withId(new IdType("Task", taskId)).execute();

                ArrayNode patchOps = jsonMapper.createArrayNode();

                if (request.status() != null && !request.status().isBlank()) {
                        ObjectNode op = patchOps.addObject();
                        op.put("op", "replace");
                        op.put("path", "/status");
                        op.put("value", request.status());
                }

                appendArrayPatch(patchOps, "note", existing.hasNote(),
                                request.notes(), this::buildNoteValue);
                appendArrayPatch(patchOps, "input", existing.hasInput(),
                                request.inputs(), this::buildParameterValue);
                appendArrayPatch(patchOps, "output", existing.hasOutput(),
                                request.outputs(), this::buildParameterValue);

                if (patchOps.isEmpty()) {
                        logger.info("No-op patch for task {} — returning current resource",
                                        taskId);
                        return fhirContext.newJsonParser().setPrettyPrint(true)
                                        .encodeResourceToString(existing);
                }

                String patchBody = jsonMapper.writeValueAsString(patchOps);
                MethodOutcome outcome = fhirClient.patch().withBody(patchBody)
                                .withId(new IdType("Task", taskId))
                                .prefer(PreferReturnEnum.REPRESENTATION).execute();

                Task updated = (Task) outcome.getResource();
                if (updated == null) {
                        updated = fhirClient.read().resource(Task.class)
                                        .withId(new IdType("Task", taskId)).execute();
                }
                return fhirContext.newJsonParser().setPrettyPrint(true)
                                .encodeResourceToString(updated);
        }

        private <T> void appendArrayPatch(ArrayNode patchOps, String fieldName,
                        boolean fieldExists, java.util.List<T> entries,
                        java.util.function.Function<T, ObjectNode> valueBuilder) {
                if (entries == null || entries.isEmpty()) {
                        return;
                }
                if (!fieldExists) {
                        ObjectNode op = patchOps.addObject();
                        op.put("op", "add");
                        op.put("path", "/" + fieldName);
                        ArrayNode values = op.putArray("value");
                        for (T entry : entries) {
                                values.add(valueBuilder.apply(entry));
                        }
                        return;
                }
                for (T entry : entries) {
                        ObjectNode op = patchOps.addObject();
                        op.put("op", "add");
                        op.put("path", "/" + fieldName + "/-");
                        op.set("value", valueBuilder.apply(entry));
                }
        }

        private ObjectNode buildNoteValue(TaskUpdateRequest.NoteEntry note) {
                ObjectNode value = jsonMapper.createObjectNode();
                value.put("text", note.text());
                if (note.authorReference() != null && !note.authorReference().isBlank()) {
                        value.putObject("authorReference").put("reference",
                                        note.authorReference());
                }
                value.put("time", java.time.OffsetDateTime.now().toString());
                return value;
        }

        private ObjectNode buildParameterValue(TaskUpdateRequest.ParameterEntry entry) {
                ObjectNode value = jsonMapper.createObjectNode();
                ObjectNode type = value.putObject("type");
                if (entry.typeCode() != null && !entry.typeCode().isBlank()) {
                        ObjectNode coding = type.putArray("coding").addObject();
                        if (entry.typeSystem() != null && !entry.typeSystem().isBlank()) {
                                coding.put("system", entry.typeSystem());
                        }
                        coding.put("code", entry.typeCode());
                        if (entry.typeDisplay() != null && !entry.typeDisplay().isBlank()) {
                                coding.put("display", entry.typeDisplay());
                        }
                }
                if (entry.typeText() != null && !entry.typeText().isBlank()) {
                        type.put("text", entry.typeText());
                }
                value.put("valueString", entry.valueString());
                return value;
        }

}
