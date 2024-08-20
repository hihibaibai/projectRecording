package com.licheng.pmMainService.vo;

import lombok.Data;

@Data
public class Result<T> {
    private Boolean success = true;
    private T result;
    private String message = "操作成功";
}
