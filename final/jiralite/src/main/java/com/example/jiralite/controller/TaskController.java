package com.example.jiralite.controller;

import com.example.jiralite.dto.TaskRequest;
import com.example.jiralite.entity.Task;
import com.example.jiralite.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // POST: Создать задачу внутри проекта
    // http://localhost:8080/api/v1/projects/1/tasks
    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable Long projectId, @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(projectId, request));
    }

    // GET: Получить все задачи проекта
    // http://localhost:8080/api/v1/projects/1/tasks
    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<Task>> getProjectTasks(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    // PATCH: Изменить статус задачи
    // http://localhost:8080/api/v1/projects/tasks/1?status=DONE
    @PatchMapping("/tasks/{taskId}")
    public ResponseEntity<Task> updateStatus(@PathVariable Long taskId, @RequestParam String status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status));
    }
}