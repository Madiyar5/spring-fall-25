package com.example.jiralite.service;

import com.example.jiralite.dto.ProjectCreateRequest;
import com.example.jiralite.dto.ProjectResponse;
import com.example.jiralite.entity.Project;
import com.example.jiralite.mapper.ProjectMapper;
import com.example.jiralite.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper jsonMapper;

    @Value("${app.kafka.topic}")
    private String topicName;

    // --- CREATE ---
    @SneakyThrows
    public ProjectResponse createProject(ProjectCreateRequest request) {
        Project project = mapper.toEntity(request);
        Project saved = repository.save(project);
        ProjectResponse response = mapper.toResponse(saved);

        String jsonEvent = jsonMapper.writeValueAsString(response);
        kafkaTemplate.send(topicName, jsonEvent);
        log.info("Produced Kafka Event: {}", jsonEvent);

        return response;
    }

    // --- READ (All) ---
    public List<ProjectResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // --- READ (One) - Добавили для полноты ---
    public ProjectResponse getById(Long id) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        return mapper.toResponse(project);
    }

    // --- UPDATE (Новое) ---
    public ProjectResponse updateProject(Long id, ProjectCreateRequest request) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        // Обновляем поля
        project.setName(request.name());
        project.setDescription(request.description());

        Project updated = repository.save(project);
        return mapper.toResponse(updated);
    }

    // --- DELETE
    public void deleteProject(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Project not found with id: " + id);
        }
        repository.deleteById(id);
    }
}