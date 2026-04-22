package com.example.labcollab.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.labcollab.common.ApiResponse;
import com.example.labcollab.entity.Project;
import com.example.labcollab.exception.NotFoundException;
import com.example.labcollab.mapper.ProjectMapper;
import com.example.labcollab.security.OrgContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectMapper projectMapper;

    public ProjectController(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @GetMapping
    public ApiResponse<List<Project>> list() {
        return ApiResponse.ok(projectMapper.selectList(new LambdaQueryWrapper<Project>().orderByDesc(Project::getId)));
    }

    @PostMapping
    public ApiResponse<Project> create(@Valid @RequestBody ProjectRequest request) {
        Project entity = new Project();
        entity.setOrgId(OrgContext.getOrgId());
        entity.setName(request.name());
        entity.setDescription(request.description());
        projectMapper.insert(entity);
        return ApiResponse.ok(entity);
    }

    @PutMapping("/{id}")
    public ApiResponse<Project> update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        Project entity = getProject(id);
        entity.setName(request.name());
        entity.setDescription(request.description());
        projectMapper.updateById(entity);
        return ApiResponse.ok(entity);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        getProject(id);
        projectMapper.deleteById(id);
        return ApiResponse.ok();
    }

    private Project getProject(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new NotFoundException("Project not found");
        }
        return project;
    }

    public record ProjectRequest(@NotBlank String name, String description) {
    }
}
