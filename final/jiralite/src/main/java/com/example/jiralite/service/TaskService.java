package com.example.jiralite.service;

import com.example.jiralite.dto.TaskRequest;
import com.example.jiralite.entity.Project;
import com.example.jiralite.entity.Task;
import com.example.jiralite.repository.ProjectRepository;
import com.example.jiralite.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Название топика (тот же самый или новый, давай использовать тот же для простоты)
    private final String TOPIC = "project-events";

    public Task createTask(Long projectId, TaskRequest request) {
        // 1. Ищем проект. Если нет - ошибка
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // 2. Создаем задачу
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status("TODO")
                .createdAt(LocalDateTime.now())
                .project(project)
                .build();

        Task savedTask = taskRepository.save(task);

        // 3. Отправляем в Kafka сообщение: "Создана задача X в проекте Y"
        String message = String.format("NEW TASK created: '%s' (ID: %d) in Project: '%s'",
                savedTask.getTitle(), savedTask.getId(), project.getName());

        kafkaTemplate.send(TOPIC, message);
        log.info("Kafka event sent: {}", message);

        return savedTask;
    }

    public Task updateTaskStatus(Long taskId, String newStatus) {
        // 1. Ищем задачу
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // 2. Меняем статус
        String oldStatus = task.getStatus();
        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);

        // 3. Если статус DONE, кидаем особое уведомление
        if ("DONE".equalsIgnoreCase(newStatus)) {
            String message = String.format("COMPLETED TASK: '%s' (ID: %d). Good job!",
                    task.getTitle(), task.getId());
            kafkaTemplate.send(TOPIC, message);
            log.info("Kafka event sent: {}", message);
        }

        return updatedTask;
    }

    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }
}