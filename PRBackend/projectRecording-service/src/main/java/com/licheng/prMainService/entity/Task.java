package com.licheng.pmMainService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("PM_TASK_LIST")
public class Task {

    @TableId(type = IdType.AUTO, value = "UUID")
    private String uuid;

    private String projectName;

    private String taskName;

    private Integer priority;

    private String createBy;

    private String assignedTo;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
//    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
//    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm")
    private Date startTime;

    private Date estimateEndTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;

    private String taskDetail;

    private String memo;
}
