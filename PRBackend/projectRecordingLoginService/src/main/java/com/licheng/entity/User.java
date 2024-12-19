package com.licheng.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("PM_USER")
public class User {
    @TableId(type = IdType.AUTO, value = "UUID")
    private String UUID;

    private String username;

    private String passwordSalted;

    private String department;
}
