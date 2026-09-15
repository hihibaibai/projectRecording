package com.licheng.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.licheng.entity.TaskTimerRecord;
import com.licheng.mapper.TaskTimerRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class TaskTimerRecordService {

    @Resource
    private TaskTimerRecordMapper taskTimerRecordMapper;

    public List<TaskTimerRecord> getTimerRecordsByTaskUuid(String taskUuid) {
        QueryWrapper<TaskTimerRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("TASK_UUID", taskUuid);
        queryWrapper.orderByDesc("START_TIME");
        return taskTimerRecordMapper.selectList(queryWrapper);
    }

    public void saveTimerRecord(TaskTimerRecord record) {
        if (record.getUuid() == null || record.getUuid().isEmpty()) {
            record.setUuid(UUID.randomUUID().toString());
        }
        taskTimerRecordMapper.insert(record);
    }

    public void updateTimerRecord(TaskTimerRecord record) {
        taskTimerRecordMapper.updateById(record);
    }
}
