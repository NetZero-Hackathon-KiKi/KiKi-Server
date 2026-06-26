package com.netzero.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsResponse {

    private String nickname;
    private String email;
    private String university;
    private String department;
    private String profileImageUrl;
    private boolean attackNotification;
    private boolean questNotification;
    private boolean verificationNotification;
}
