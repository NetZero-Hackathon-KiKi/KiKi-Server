package com.netzero.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimelineCommentRequest {

    @NotBlank
    @Size(max = 500)
    private String content;
}
