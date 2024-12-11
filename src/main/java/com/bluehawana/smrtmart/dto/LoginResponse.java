package com.bluehawana.smrtmart.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class LoginResponse {
    private String message;
    private String token;
    private String email;
}
