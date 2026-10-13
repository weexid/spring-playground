package com.weex.spring_playground.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiSuccessResponse<T>(
    String timestamp,
    int status,
    String message,
    String path,
    @JsonInclude(JsonInclude.Include.NON_NULL) T data
) {}
