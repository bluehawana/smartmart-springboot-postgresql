package com.bluehawana.smrtmart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor

public class ApiResponse {
    private String message;
    private boolean success;
    private Object data;
}