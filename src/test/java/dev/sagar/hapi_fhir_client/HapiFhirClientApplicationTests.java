package dev.sagar.hapi_fhir_client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import dev.sagar.hapi_fhir_client.condition.ConditionClientController;
import dev.sagar.hapi_fhir_client.condition.ConditionClientService;
import dev.sagar.hapi_fhir_client.encounter.EncounterClientController;
import dev.sagar.hapi_fhir_client.encounter.EncounterClientService;
import dev.sagar.hapi_fhir_client.medicationrequest.MedicationRequestClientController;
import dev.sagar.hapi_fhir_client.medicationrequest.MedicationRequestClientService;
import dev.sagar.hapi_fhir_client.medicationstatement.MedicationStatementClientController;
import dev.sagar.hapi_fhir_client.medicationstatement.MedicationStatementClientService;
import dev.sagar.hapi_fhir_client.ips.IpsClientController;
import dev.sagar.hapi_fhir_client.ips.IpsClientService;
import dev.sagar.hapi_fhir_client.observation.ObservationClientController;
import dev.sagar.hapi_fhir_client.observation.ObservationClientService;
import dev.sagar.hapi_fhir_client.careteam.CareTeamClientController;
import dev.sagar.hapi_fhir_client.careteam.CareTeamClientService;

@SpringBootTest
class HapiFhirClientApplicationTests {

	@Autowired
	private ConditionClientController conditionClientController;

	@Autowired
	private ConditionClientService conditionClientService;

	@Autowired
	private EncounterClientController encounterClientController;

	@Autowired
	private EncounterClientService encounterClientService;

	@Autowired
	private MedicationRequestClientController medicationRequestClientController;

	@Autowired
	private MedicationRequestClientService medicationRequestClientService;

	@Autowired
	private MedicationStatementClientController medicationStatementClientController;

	@Autowired
	private MedicationStatementClientService medicationStatementClientService;

	@Autowired
	private ObservationClientController observationClientController;

	@Autowired
	private ObservationClientService observationClientService;

	@Autowired
	private CareTeamClientController careTeamClientController;

	@Autowired
	private CareTeamClientService careTeamClientService;

	@Autowired
	private IpsClientController ipsClientController;

	@Autowired
	private IpsClientService ipsClientService;

	@Test
	void contextLoads() {
		assertNotNull(conditionClientController);
		assertNotNull(conditionClientService);
		assertNotNull(encounterClientController);
		assertNotNull(encounterClientService);
		assertNotNull(medicationRequestClientController);
		assertNotNull(medicationRequestClientService);
		assertNotNull(medicationStatementClientController);
		assertNotNull(medicationStatementClientService);
		assertNotNull(observationClientController);
		assertNotNull(observationClientService);
		assertNotNull(careTeamClientController);
		assertNotNull(careTeamClientService);
		assertNotNull(ipsClientController);
		assertNotNull(ipsClientService);
	}

}
