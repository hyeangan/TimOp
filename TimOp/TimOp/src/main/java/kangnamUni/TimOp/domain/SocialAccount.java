package kangnamUni.TimOp.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(
        name = "social_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "providerUserId"})
)
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    // "google", "kakao" 같은 제공자 이름
    private String provider;

    // 제공자 쪽 사용자의 고유 ID (구글: sub, 카카오: id)
    private String providerMemberId;

    // 제공자가 전달한 이메일
    private String emailFromProvider;

    private Instant linkedAt;

}
