package com.example.jiralite.controller;

import com.example.jiralite.dto.ProjectCreateRequest;
import com.example.jiralite.dto.ProjectResponse;
import com.example.jiralite.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;

    @Operation(summary = "Create Project", description = "Creates a project and triggers Kafka event")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectCreateRequest request) {
        return service.createProject(request);
    }

    @Operation(summary = "Get All Projects")
    @GetMapping
    public List<ProjectResponse> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Get Project by ID")
    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Update Project")
    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectCreateRequest request) {
        return service.updateProject(id, request);
    }

    @Operation(summary = "Delete Project")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content - стандарт для удаления
    public void delete(@PathVariable Long id) {
        service.deleteProject(id);
    }
}