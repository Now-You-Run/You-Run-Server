package com.running.you_run.auth.dto;

import com.running.you_run.auth.Enum.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private Long id;
    private String email;
    private String nickname;
    private String bio;
    private String profileImageUrl;
    private LocalDate birthDate;
    private Double height;
    private Double weight;
    private UserRole userRole;
}
