package com.example.jiralite.mapper;

import com.example.jiralite.dto.ProjectCreateRequest;
import com.example.jiralite.dto.ProjectResponse;
import com.example.jiralite.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public Project toEntity(ProjectCreateRequest request) {
        return Project.builder()
                .name(request.name())
                .description(request.description())
                .status("PLANNED") // Статус по умолчанию
                .build();
    }

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getCreatedAt()
        );
    }
}