package com.netzero.dto.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationRequest {

    private Boolean attackNotification;
    private Boolean questNotification;
    private Boolean verificationNotification;
}
