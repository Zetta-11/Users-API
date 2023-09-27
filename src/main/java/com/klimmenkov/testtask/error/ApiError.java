package com.klimmenkov.testtask.error;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ApiError {

    private int status;

    private String detail;

    private int code;

    private Map<String, String> links;
}
