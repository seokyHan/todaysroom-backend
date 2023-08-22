package com.todaysroom.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.todaysroom.common.BaseTimeEntity;
import com.todaysroom.inquiry.entity.Inquiry;
import com.todaysroom.types.Role;
import com.todaysroom.types.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Getter
@Table(name = "userEntity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// insert시 null인 필드 제외
// @DynamicInsert
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Email(message = "이메일 형식에 맞지 않습니다.")
    @Column(name = "userEmail")
    private String userEmail;

    @Column(name = "password")
    private String password;

    @Length(min = 2, max = 8, message = "2~10자리의 이름을 입력하세요.")
    @Column(name = "userName")
    private String userName;

    @Length(min = 2, max = 8, message = "2~10자리의 닉네임을 입력하세요.")
    @Column(name = "nickname")
    private String nickname;

    @Column(name = "recentSearch")
    private String recentSearch;

    @Column(name = "activated")
    private boolean activated;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonManagedReference(value = "user-userAuthority")
    private List<UserAuthority> authorities;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonManagedReference(value = "user-inquiry")
    private List<Inquiry> inquiries;

    //OAuth2
    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)
    private String imageUrl; // 프로필 이미지

    @Builder
    public UserEntity(boolean activated,
                      String userEmail,
                      String password,
                      String userName,
                      String nickname,
                      List<UserAuthority> authorities,
                      String imageUrl,
                      SocialType socialType,
                      String socialId,
                      Role role) {
        this.activated = activated;
        this.userEmail = userEmail;
        this.password = password;
        this.userName = userName;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.socialType = socialType;
        this.socialId = socialId;
        this.role = role;
        this.authorities = authorities;
    }

    public void socialUserUpdate(Role role, String nickname) {
        this.role = role;
        this.nickname = nickname;
    }

}

