package com.example.jiralite.repository;

import com.example.jiralite.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Найти все задачи по ID проекта
    List<Task> findByProjectId(Long projectId);
}