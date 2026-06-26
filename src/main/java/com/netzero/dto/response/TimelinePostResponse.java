@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimelinePostResponse {

    private Long postId;
    private String authorNickname;
    private String authorProfileImageUrl;
    private String content;
    private String imageUrl;
    private int likeCount;
    private int commentCount;
    private boolean likedByMe;
    private LocalDateTime createdAt;
}