package com.netzero.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long notificationId;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private Long referenceId;
    private String senderNickname;
    private String senderProfileImageUrl;
    private LocalDateTime createdAt;
}
