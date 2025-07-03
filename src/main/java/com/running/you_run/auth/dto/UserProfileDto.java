package com.running.you_run.auth.dto;

import com.running.you_run.auth.Enum.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String email;
    private String nickname;
    private UserRole userRole;
}
