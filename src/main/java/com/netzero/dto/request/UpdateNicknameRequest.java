package com.netzero.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNicknameRequest {

    @NotBlank
    @Size(min = 2, max = 10)
    private String nickname;
}
