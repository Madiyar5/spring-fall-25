package com.example.jiralite.service;

import com.example.jiralite.dto.TaskRequest; // <--- ВАЖНО: Добавили DTO
import com.example.jiralite.entity.Project;
import com.example.jiralite.entity.Task;
import com.example.jiralite.repository.ProjectRepository;
import com.example.jiralite.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private TaskService taskService;

    // ТЕСТ 1: Успешное создание задачи
    @Test
    void createTask_Success() {
        Long projectId = 1L;
        Project mockProject = new Project();
        mockProject.setId(projectId);

        // ИСПРАВЛЕНИЕ: Используем TaskRequest вместо Task
        TaskRequest inputTask = new TaskRequest();
        inputTask.setTitle("Unit Test Task");
        inputTask.setDescription("Test Description");

        Task savedTask = new Task();
        savedTask.setId(10L);
        savedTask.setTitle("Unit Test Task");
        savedTask.setStatus("TODO");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createTask(projectId, inputTask);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(kafkaTemplate, times(1)).send(any(), any());
    }

    // ТЕСТ 2: Ошибка, если проект не найден
    @Test
    void createTask_ProjectNotFound() {
        Long projectId = 999L;
        // ИСПРАВЛЕНИЕ: Тут тоже используем TaskRequest
        TaskRequest inputTask = new TaskRequest();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            taskService.createTask(projectId, inputTask);
        });

        verify(taskRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any());
    }

    // ТЕСТ 3: Получение задач по ID проекта
    @Test
    void getTasksByProject_Success() {
        Long projectId = 1L;
        Task t1 = new Task();
        Task t2 = new Task();
        List<Task> mockTasks = List.of(t1, t2);

        when(taskRepository.findByProjectId(projectId)).thenReturn(mockTasks);

        List<Task> result = taskService.getTasksByProject(projectId);

        assertEquals(2, result.size());
    }
}