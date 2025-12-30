package com.example.jiralite;

import com.example.jiralite.dto.TaskRequest;
import com.example.jiralite.entity.Project;
import com.example.jiralite.repository.ProjectRepository;
import com.example.jiralite.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
    }

    // ТЕСТ 1 (Integration): Создание задачи через API (POST)
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createTask_Integration() throws Exception {
        // 1. Создаем проект в базе (ОБЯЗАТЕЛЬНО ЗАПОЛНЯЕМ ВСЕ ПОЛЯ)
        Project project = new Project();
        project.setName("Integration Project");
        project.setDescription("Test Description");
        project.setStatus("PLANNED"); // <--- ВОТ ЭТОГО НЕ ХВАТАЛО!
        project = projectRepository.save(project);

        // 2. Готовим JSON запроса
        TaskRequest request = new TaskRequest();
        request.setTitle("Integration Task");
        request.setDescription("Testing full flow");

        // 3. Делаем POST запрос
        mockMvc.perform(post("/api/v1/projects/" + project.getId() + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Task"));

        // 4. Проверяем базу
        assert taskRepository.count() == 1;

        // 5. Проверяем Кафку
        verify(kafkaTemplate).send(any(), any());
    }

    // ТЕСТ 2 (Integration): Получение списка задач (GET)
    @Test
    @WithMockUser(username = "user")
    void getTasks_Integration() throws Exception {
        // 1. Создаем проект
        Project project = new Project();
        project.setName("Read Project");
        project.setDescription("For reading");
        project.setStatus("PLANNED"); // <--- И ТУТ ТОЖЕ
        project = projectRepository.save(project);

        // 2. Делаем GET запрос
        mockMvc.perform(get("/api/v1/projects/" + project.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Read Project"));
    }

    // ТЕСТ 3 (Security): Проверка защиты (401 без токена)
    @Test
    void createProject_Unauthorized() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Hacker Task");

        mockMvc.perform(post("/api/v1/projects") // Пытаемся создать проект без прав
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}