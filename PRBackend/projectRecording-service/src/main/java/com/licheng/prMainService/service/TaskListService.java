package com.licheng.prMainService.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.licheng.prLoginService.entity.User;
import com.licheng.prLoginService.entity.UserToken;
import com.licheng.prLoginService.service.UserService;
import com.licheng.prMainService.entity.Task;
import com.licheng.prMainService.mapper.TaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class TaskListService extends ServiceImpl<TaskMapper, Task> implements IService<Task> {
    @Resource
    private UserService userService;

    public Boolean saveTask(Task task){
        if (task.getUuid() != null) {
            updateById(task);
        }
        else {
            task.setUuid(UUID.randomUUID().toString());
            UserToken user = userService.getCurrentUser();
            task.setCreateBy(user.getUserName());
            task.setCreateTime(new Date());
            save(task);
        }
        return true;
    }

    public List<Task> getTaskList(){
        UserToken user = userService.getCurrentUser();
//        if (user.getUserName().equals("admin")) { //临时措施
//            return list();
//        }
        LambdaQueryWrapper<Task> qw = new LambdaQueryWrapper<>();
//        qw.eq(Task::getAssignedTo, user.getUserName());
//        qw.or();
//        qw.eq(Task::getCreateBy, user.getUserName());
        return list(qw);
    }

    public Task getTaskByUid(String taskUid) {
        LambdaQueryWrapper<Task> qw = new LambdaQueryWrapper<>();
        qw.eq(Task::getUuid, taskUid);
        return getOne(qw);
    }

    public Boolean removeTaskByUid(String taskUid) {
        LambdaQueryWrapper<Task> qw = new LambdaQueryWrapper<>();
        qw.eq(Task::getUuid, taskUid);
        return remove(qw);
    }
}
