package com.netzero.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Size(min = 2, max = 10)
    private String nickname;

    @NotBlank
    private String university;

    @NotBlank
    private String department;
}
