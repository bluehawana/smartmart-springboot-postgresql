// src/main/java/com/bluehawana/smrtmart/dto/UserDTO.java
package com.bluehawana.smrtmart.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id;
    private String email;
    private String role;
    private boolean enabled;
}