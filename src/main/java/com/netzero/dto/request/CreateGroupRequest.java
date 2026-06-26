package com.netzero.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {

    @NotBlank
    @Size(min = 2, max = 30)
    private String name;

    private String description;
}
