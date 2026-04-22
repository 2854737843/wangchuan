package com.example.labcollab.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.labcollab.common.ApiResponse;
import com.example.labcollab.entity.Project;
import com.example.labcollab.entity.Topic;
import com.example.labcollab.exception.NotFoundException;
import com.example.labcollab.mapper.ProjectMapper;
import com.example.labcollab.mapper.TopicMapper;
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
@RequestMapping("/api/projects/{projectId}/topics")
public class TopicController {

    private final TopicMapper topicMapper;
    private final ProjectMapper projectMapper;

    public TopicController(TopicMapper topicMapper, ProjectMapper projectMapper) {
        this.topicMapper = topicMapper;
        this.projectMapper = projectMapper;
    }

    @GetMapping
    public ApiResponse<List<Topic>> list(@PathVariable Long projectId) {
        requireProject(projectId);
        return ApiResponse.ok(topicMapper.selectList(new LambdaQueryWrapper<Topic>()
                .eq(Topic::getProjectId, projectId)
                .orderByDesc(Topic::getId)));
    }

    @PostMapping
    public ApiResponse<Topic> create(@PathVariable Long projectId, @Valid @RequestBody TopicRequest request) {
        requireProject(projectId);
        Topic entity = new Topic();
        entity.setOrgId(OrgContext.getOrgId());
        entity.setProjectId(projectId);
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        topicMapper.insert(entity);
        return ApiResponse.ok(entity);
    }

    @PutMapping("/{id}")
    public ApiResponse<Topic> update(@PathVariable Long projectId, @PathVariable Long id, @Valid @RequestBody TopicRequest request) {
        requireProject(projectId);
        Topic topic = getTopic(projectId, id);
        topic.setTitle(request.title());
        topic.setDescription(request.description());
        topicMapper.updateById(topic);
        return ApiResponse.ok(topic);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long projectId, @PathVariable Long id) {
        requireProject(projectId);
        getTopic(projectId, id);
        topicMapper.deleteById(id);
        return ApiResponse.ok();
    }

    private void requireProject(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found");
        }
    }

    private Topic getTopic(Long projectId, Long id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null || !topic.getProjectId().equals(projectId)) {
            throw new NotFoundException("Topic not found");
        }
        return topic;
    }

    public record TopicRequest(@NotBlank String title, String description) {
    }
}
