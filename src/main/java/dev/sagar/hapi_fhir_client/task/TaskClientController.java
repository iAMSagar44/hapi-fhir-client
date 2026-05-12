package dev.sagar.hapi_fhir_client.task;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task")
public class TaskClientController {

    private final TaskClientService taskClientService;

    public TaskClientController(TaskClientService taskClientService) {
        this.taskClientService = taskClientService;
    }

    @GetMapping("")
    public String getTasksByPatientIdentifier(@RequestParam String identifier) {
        return taskClientService.getTasksByIdentifier(identifier);
    }

    @PatchMapping("/{id}")
    public String updateTask(@PathVariable String id,
            @RequestBody TaskUpdateRequest request) {
        return taskClientService.updateTask(id, request);
    }

}
