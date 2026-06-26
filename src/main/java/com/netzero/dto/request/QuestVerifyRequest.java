package com.netzero.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestVerifyRequest {

    @NotBlank
    private String verificationImageUrl;

    private String content;
}
