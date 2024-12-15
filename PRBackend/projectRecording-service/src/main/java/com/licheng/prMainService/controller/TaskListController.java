package com.licheng.prMainService.controller;


import com.licheng.prMainService.entity.Task;
import com.licheng.prMainService.service.TaskListService;
import com.licheng.prMainService.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/task")
public class TaskListController {
    @Resource
    private TaskListService taskListService;

    @GetMapping("/getTaskList")
    public Result getTaskList() {
        Result result = new Result();
        result.setResult(taskListService.getTaskList());
        return result;
    }

    @GetMapping("/getTask")
    public Result getTask(@RequestParam String taskUid) {
        Result result = new Result();
        result.setResult(taskListService.getTaskByUid(taskUid));
        return result;
    }

    @PostMapping("/saveTask")
    public Result saveTask(@RequestBody Task task) {
        Result result = new Result();
        taskListService.saveTask(task);
        return result;
    }

    @PostMapping("/removeTask")
    public Result removeTask(@RequestBody String taskUid) {
        Result result = new Result();
        taskListService.removeById(taskUid);
        return result;
    }
}
