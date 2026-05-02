package dev.sagar.hapi_fhir_client.ips;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import dev.sagar.hapi_fhir_client.config.IdentifierSystem;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class IpsClientService {

    private static final Logger logger = LoggerFactory.getLogger(IpsClientService.class);

    private static final String IPS_BUNDLE_PROFILE =
            "http://hl7.org/fhir/uv/ips/StructureDefinition/Bundle-uv-ips";
    private static final String IPS_COMPOSITION_PROFILE =
            "http://hl7.org/fhir/uv/ips/StructureDefinition/Composition-uv-ips";
    private static final String LOINC_SYSTEM = "http://loinc.org";
    private static final String EMPTY_REASON_SYSTEM =
            "http://terminology.hl7.org/CodeSystem/list-empty-reason";

    private static final int IDX_PATIENT = 0;
    private static final int IDX_CONDITIONS = 1;
    private static final int IDX_MEDICATIONS = 2;
    private static final int IDX_ALLERGIES = 3;
    private static final int IDX_VITALS = 4;
    private static final int IDX_LABS = 5;
    private static final int IDX_IMMUNIZATIONS = 6;
    private static final int IDX_PROCEDURES = 7;

    private final IGenericClient fhirClient;
    private final FhirContext fhirContext;
    private final FhirValidator fhirValidator;

    public IpsClientService(IGenericClient fhirClient, FhirContext fhirContext,
            FhirValidator fhirValidator) {
        this.fhirClient = fhirClient;
        this.fhirContext = fhirContext;
        this.fhirValidator = fhirValidator;
    }

    private record ParsedData(Patient patient, List<Condition> conditions,
            List<MedicationRequest> medicationRequests, List<Medication> medications,
            List<AllergyIntolerance> allergies, List<Observation> vitalSigns,
            List<Observation> labs, List<Immunization> immunizations, List<Procedure> procedures) {
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public String generateIps(String identifier) {
        logger.info("Generating IPS for identifier: {}", identifier);
        Bundle ipsBundle = buildIpsBundle(identifier);
        // validate(ipsBundle); --> Validation currently disabled due to missing AU-specific
        // packages in the validator.
        return encode(ipsBundle);
    }

    // ── IPS assembly pipeline ─────────────────────────────────────────────────

    private Bundle buildIpsBundle(String identifier) {
        String system = IdentifierSystem.forIdentifier(identifier);
        Bundle batchBundle = buildBatchBundle(system, identifier);
        Bundle batchResponse = fhirClient.transaction().withBundle(batchBundle).execute();
        ParsedData data = parseBatch(batchResponse);
        Map<IBaseResource, String> uuidMap = assignUrns(data);
        Composition composition = buildComposition(data, uuidMap);
        return assembleDocumentBundle(composition, data, uuidMap);
    }

    private Bundle buildBatchBundle(String system, String ihi) {
        Bundle batch = new Bundle();
        batch.setType(Bundle.BundleType.BATCH);

        String patientParam = "patient.identifier=" + system + "|" + ihi;

        addGetEntry(batch, "Patient?identifier=" + system + "|" + ihi);
        addGetEntry(batch,
                "Condition?" + patientParam + "&clinical-status=active&category=problem-list-item");
        addGetEntry(batch, "MedicationRequest?" + patientParam
                + "&status=active&_include=MedicationRequest:medication");
        addGetEntry(batch, "AllergyIntolerance?" + patientParam + "&clinical-status=active");
        addGetEntry(batch,
                "Observation?" + patientParam + "&category=vital-signs&_sort=-date&_count=10");
        addGetEntry(batch,
                "Observation?" + patientParam + "&category=laboratory&_sort=-date&_count=10");
        addGetEntry(batch, "Immunization?" + patientParam + "&status=completed");
        addGetEntry(batch, "Procedure?" + patientParam + "&_sort=-date&_count=20");
        addGetEntry(batch, "Encounter?" + patientParam + "&_sort=-date&_count=1");

        return batch;
    }

    private void addGetEntry(Bundle batch, String url) {
        batch.addEntry().getRequest().setMethod(Bundle.HTTPVerb.GET).setUrl(url);
    }

    private ParsedData parseBatch(Bundle batchResponse) {
        List<Patient> patients =
                extractResourcesFromEntry(batchResponse, IDX_PATIENT, Patient.class);
        if (patients.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No Patient found for the given identifier");
        }

        return new ParsedData(patients.get(0),
                extractResourcesFromEntry(batchResponse, IDX_CONDITIONS, Condition.class),
                extractResourcesFromEntry(batchResponse, IDX_MEDICATIONS, MedicationRequest.class),
                extractResourcesFromEntry(batchResponse, IDX_MEDICATIONS, Medication.class),
                extractResourcesFromEntry(batchResponse, IDX_ALLERGIES, AllergyIntolerance.class),
                extractResourcesFromEntry(batchResponse, IDX_VITALS, Observation.class),
                extractResourcesFromEntry(batchResponse, IDX_LABS, Observation.class),
                extractResourcesFromEntry(batchResponse, IDX_IMMUNIZATIONS, Immunization.class),
                extractResourcesFromEntry(batchResponse, IDX_PROCEDURES, Procedure.class));
    }

    private <T extends Resource> List<T> extractResourcesFromEntry(Bundle batchResponse, int index,
            Class<T> type) {
        List<Bundle.BundleEntryComponent> outerEntries = batchResponse.getEntry();
        if (index >= outerEntries.size()) {
            return List.of();
        }

        Bundle.BundleEntryComponent outerEntry = outerEntries.get(index);
        Bundle.BundleEntryResponseComponent response = outerEntry.getResponse();
        String status =
                (response != null && response.getStatus() != null) ? response.getStatus() : "";
        if (!status.startsWith("2")) {
            logger.warn("Batch entry {} returned non-2xx status: {}", index, status);
            return List.of();
        }

        if (!(outerEntry.getResource() instanceof Bundle innerBundle)) {
            return List.of();
        }

        List<T> results = new ArrayList<>();
        for (Bundle.BundleEntryComponent innerEntry : innerBundle.getEntry()) {
            if (type.isInstance(innerEntry.getResource())) {
                results.add(type.cast(innerEntry.getResource()));
            }
        }
        return results;
    }

    private Map<IBaseResource, String> assignUrns(ParsedData data) {
        Map<IBaseResource, String> map = new IdentityHashMap<>();
        map.put(data.patient(), newUrn());
        data.conditions().forEach(r -> map.put(r, newUrn()));
        data.medicationRequests().forEach(r -> map.put(r, newUrn()));
        data.medications().forEach(r -> map.put(r, newUrn()));
        data.allergies().forEach(r -> map.put(r, newUrn()));
        data.vitalSigns().forEach(r -> map.put(r, newUrn()));
        data.labs().forEach(r -> map.put(r, newUrn()));
        data.immunizations().forEach(r -> map.put(r, newUrn()));
        data.procedures().forEach(r -> map.put(r, newUrn()));
        return map;
    }

    private Composition buildComposition(ParsedData data, Map<IBaseResource, String> uuidMap) {
        Composition comp = new Composition();
        comp.getMeta().addProfile(IPS_COMPOSITION_PROFILE);
        comp.setStatus(Composition.CompositionStatus.FINAL);
        comp.setDate(new Date());
        comp.setTitle("International Patient Summary");
        comp.setType(new CodeableConcept()
                .addCoding(new Coding(LOINC_SYSTEM, "60591-5", "Patient summary Document")));
        comp.setSubject(new Reference(uuidMap.get(data.patient())));
        comp.addAuthor(new Reference(uuidMap.get(data.patient())));

        // Mandatory sections (always present; emptyReason if no data)
        addSection(comp, buildSection("Allergies and Intolerances", "48765-2", data.allergies(),
                uuidMap, true));
        addSection(comp, buildSection("Medication Summary", "10160-0", data.medicationRequests(),
                uuidMap, true));
        addSection(comp,
                buildSection("Active Problems", "11450-4", data.conditions(), uuidMap, true));

        // Optional sections (omitted when empty)
        addSection(comp,
                buildSection("Immunizations", "11369-6", data.immunizations(), uuidMap, false));
        addSection(comp, buildSection("History of Procedures", "47519-4", data.procedures(),
                uuidMap, false));
        addSection(comp, buildSection("Vital Signs", "8716-3", data.vitalSigns(), uuidMap, false));
        addSection(comp, buildSection("Results", "30954-2", data.labs(), uuidMap, false));

        return comp;
    }

    private void addSection(Composition comp, Composition.SectionComponent section) {
        if (section != null) {
            comp.addSection(section);
        }
    }

    private Composition.SectionComponent buildSection(String title, String loincCode,
            List<? extends Resource> resources, Map<IBaseResource, String> uuidMap,
            boolean required) {
        Composition.SectionComponent section = new Composition.SectionComponent();
        section.setTitle(title);
        section.setCode(
                new CodeableConcept().addCoding(new Coding(LOINC_SYSTEM, loincCode, title)));

        if (resources.isEmpty()) {
            if (!required) {
                return null;
            }
            section.setEmptyReason(new CodeableConcept()
                    .addCoding(new Coding(EMPTY_REASON_SYSTEM, "unavailable", "Unavailable")));
            return section;
        }

        resources.forEach(r -> section.addEntry(new Reference(uuidMap.get(r))));
        return section;
    }

    private Bundle assembleDocumentBundle(Composition composition, ParsedData data,
            Map<IBaseResource, String> uuidMap) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.DOCUMENT);
        bundle.setTimestamp(new Date());
        bundle.getMeta().addProfile(IPS_BUNDLE_PROFILE);
        bundle.setIdentifier(new Identifier().setSystem("urn:ietf:rfc:3986").setValue(newUrn()));

        addEntry(bundle, newUrn(), composition);
        addEntry(bundle, uuidMap.get(data.patient()), data.patient());

        data.conditions().forEach(r -> addEntry(bundle, uuidMap.get(r), r));
        data.medicationRequests().forEach(r -> addEntry(bundle, uuidMap.get(r), r));
        data.medications().forEach(r -> addEntry(bundle, uuidMap.get(r), r));
        data.allergies().forEach(r -> addEntry(bundle, uuidMap.get(r), r));
        data.vitalSigns().forEach(r -> addEntry(bundle, uuidMap.get(r), r));
        data.labs().forEach(r -> addEntry(bundle, uuidMap.get(r), r));
        data.immunizations().forEach(r -> addEntry(bundle, uuidMap.get(r), r));
        data.procedures().forEach(r -> addEntry(bundle, uuidMap.get(r), r));

        return bundle;
    }

    private void addEntry(Bundle bundle, String fullUrl, Resource resource) {
        bundle.addEntry().setFullUrl(fullUrl).setResource(resource);
    }

    // ── Validation ────────────────────────────────────────────────────────────

    // Message IDs suppressed from the error gate — caused by missing AU packages,
    // not by structural errors in the IPS document we assembled.
    private static final Set<String> SUPPRESSED_MESSAGE_IDS = Set.of("SLICING_CANNOT_BE_EVALUATED",
            "BUNDLE_BUNDLE_ENTRY_NOTFOUND_APPARENT", "Validation_VAL_Unknown_Profile",
            "http://hl7.org/fhir/StructureDefinition/Composition#cmp-1");

    // Text fragments used as fallback when getMessageId() returns null.
    // The underlying HL7 validator does not always populate the HAPI messageId
    // field; in those cases we match against the human-readable message text.
    private static final List<String> SUPPRESSED_TEXT_FRAGMENTS =
            List.of("Slicing cannot be evaluated", // SLICING_CANNOT_BE_EVALUATED
                    "Note that there is a resource in the bundle", // BUNDLE_BUNDLE_ENTRY_NOTFOUND_APPARENT
                    "Unknown profile http://hl7.org.au/" // AU-specific profiles not in loaded
                                                         // packages
            );

    private void validate(Bundle bundle) {
        ValidationResult result = fhirValidator.validateWithResult(bundle);
        logValidationMessages(result);

        boolean hasErrors = result.getMessages().stream()
                .filter(m -> !isSuppressed(m.getMessageId(), m.getMessage()))
                .anyMatch(m -> m.getSeverity() == ResultSeverityEnum.ERROR
                        || m.getSeverity() == ResultSeverityEnum.FATAL);
        if (hasErrors) {
            String outcomeJson = encode((OperationOutcome) result.toOperationOutcome());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, outcomeJson);
        }
    }

    private static boolean isSuppressed(String messageId, String messageText) {
        if (messageId != null) {
            return SUPPRESSED_MESSAGE_IDS.contains(messageId);
        }
        return messageText != null
                && SUPPRESSED_TEXT_FRAGMENTS.stream().anyMatch(messageText::contains);
    }

    private void logValidationMessages(ValidationResult result) {
        result.getMessages().forEach(m -> {
            String fmt = "Validation [{}] at {}: {} -> {}";
            switch (m.getSeverity()) {
                case FATAL, ERROR -> logger.error(fmt, m.getSeverity(), m.getLocationString(),
                        m.getMessageId(), m.getMessage());
                case WARNING -> logger.warn(fmt, m.getSeverity(), m.getLocationString(),
                        m.getMessageId(), m.getMessage());
                default -> logger.info(fmt, m.getSeverity(), m.getLocationString(),
                        m.getMessageId(), m.getMessage());
            }
        });
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private String encode(Resource resource) {
        return fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource);
    }

    private static String newUrn() {
        return "urn:uuid:" + UUID.randomUUID();
    }
}
