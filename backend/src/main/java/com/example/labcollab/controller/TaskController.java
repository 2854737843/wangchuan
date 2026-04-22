package com.example.labcollab.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.labcollab.common.ApiResponse;
import com.example.labcollab.entity.Task;
import com.example.labcollab.entity.Topic;
import com.example.labcollab.exception.NotFoundException;
import com.example.labcollab.mapper.TaskMapper;
import com.example.labcollab.mapper.TopicMapper;
import com.example.labcollab.security.OrgContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/topics/{topicId}/tasks")
public class TaskController {

    private final TaskMapper taskMapper;
    private final TopicMapper topicMapper;

    public TaskController(TaskMapper taskMapper, TopicMapper topicMapper) {
        this.taskMapper = taskMapper;
        this.topicMapper = topicMapper;
    }

    @GetMapping
    public ApiResponse<List<Task>> list(@PathVariable Long topicId) {
        requireTopic(topicId);
        return ApiResponse.ok(taskMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getTopicId, topicId)
                .orderByDesc(Task::getId)));
    }

    @PostMapping
    public ApiResponse<Task> create(@PathVariable Long topicId, @Valid @RequestBody TaskRequest request) {
        requireTopic(topicId);
        Task entity = new Task();
        entity.setOrgId(OrgContext.getOrgId());
        entity.setTopicId(topicId);
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setStatus(request.status());
        entity.setDueDate(request.dueDate());
        taskMapper.insert(entity);
        return ApiResponse.ok(entity);
    }

    @PutMapping("/{id}")
    public ApiResponse<Task> update(@PathVariable Long topicId, @PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        requireTopic(topicId);
        Task task = getTask(topicId, id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setDueDate(request.dueDate());
        taskMapper.updateById(task);
        return ApiResponse.ok(task);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long topicId, @PathVariable Long id) {
        requireTopic(topicId);
        getTask(topicId, id);
        taskMapper.deleteById(id);
        return ApiResponse.ok();
    }

    private void requireTopic(Long topicId) {
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null) {
            throw new NotFoundException("Topic not found");
        }
    }

    private Task getTask(Long topicId, Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null || !task.getTopicId().equals(topicId)) {
            throw new NotFoundException("Task not found");
        }
        return task;
    }

    public record TaskRequest(
            @NotBlank String title,
            String description,
            @NotBlank @Pattern(regexp = "TODO|DOING|DONE", message = "status must be TODO/DOING/DONE") String status,
            LocalDate dueDate
    ) {
    }
}
