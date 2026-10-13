package com.weex.spring_playground.common.api;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiErrorResponse(
    String timestamp, int status, String error, String message, String path, @JsonInclude (JsonInclude.Include.NON_NULL) Map<String, String> errors
) {}
