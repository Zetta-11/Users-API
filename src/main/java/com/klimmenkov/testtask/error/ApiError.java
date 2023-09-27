package com.klimmenkov.testtask.error;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ApiError {

    private List<String> errors;

    private int status;

    private String detail;

    private int code;

    private Map<String, String> links;
}
