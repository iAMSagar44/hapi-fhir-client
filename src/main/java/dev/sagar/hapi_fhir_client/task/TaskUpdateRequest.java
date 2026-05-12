package dev.sagar.hapi_fhir_client.task;

import java.util.List;

public record TaskUpdateRequest(
                String status,
                List<NoteEntry> notes,
                List<ParameterEntry> inputs,
                List<ParameterEntry> outputs) {

        public record NoteEntry(String text, String authorReference) {
        }

        public record ParameterEntry(
                        String typeSystem,
                        String typeCode,
                        String typeDisplay,
                        String typeText,
                        String valueString) {
        }
}
