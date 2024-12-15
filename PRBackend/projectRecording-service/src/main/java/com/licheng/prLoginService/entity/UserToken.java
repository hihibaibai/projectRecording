package com.licheng.prLoginService.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserToken {
    private String userName;
    private Date expTime;
}
