package com.licheng.controller;

import com.licheng.entity.TaskTimerRecord;
import com.licheng.service.TaskTimerRecordService;
import com.licheng.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/taskTimerRecord")
public class TaskTimerRecordController {

    @Resource
    private TaskTimerRecordService taskTimerRecordService;

    @GetMapping("/getTimerRecords")
    public Result getTimerRecords(@RequestParam String taskUuid) {
        Result result = new Result();
        result.setResult(taskTimerRecordService.getTimerRecordsByTaskUuid(taskUuid));
        return result;
    }

    @PostMapping("/startTimer")
    public Result startTimer(@RequestBody TaskTimerRecord record) {
        Result result = new Result();
        record.setStartTime(new Date());
        taskTimerRecordService.saveTimerRecord(record);
        result.setResult(record);
        return result;
    }

    @PostMapping("/endTimer")
    public Result endTimer(@RequestBody TaskTimerRecord record) {
        Result result = new Result();
        record.setEndTime(new Date());
        taskTimerRecordService.updateTimerRecord(record);
        result.setResult(record);
        return result;
    }
}
