package com.todaysroom.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Getter
@Table(name = "userEntity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// insert시 null인 필드 제외
// @DynamicInsert
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    @Column(name = "userEmail")
    private String userEmail;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Column(name = "password")
    private String password;

    @NotBlank(message = "이름을 입력하세요.")
    @Length(min = 2, max = 8, message = "2~10자리의 이름을 입력하세요.")
    @Column(name = "userName")
    private String userName;

    @NotBlank(message = "닉네임을 입력하세요.")
    @Length(min = 2, max = 8, message = "2~10자리의 닉네임을 입력하세요.")
    @Column(name = "nickname")
    private String nickname;

    @Column(name = "recentSearch")
    private String recentSearch;

    @Column(name = "activated")
    private boolean activated;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonManagedReference(value = "user-userAuthority")
    private List<UserAuthority> authorities;

    @Builder
    public UserEntity(boolean activated, String userEmail, String password, String userName, String nickname, List<UserAuthority> authorities) {
        this.activated = activated;
        this.userEmail = userEmail;
        this.password = password;
        this.userName = userName;
        this.nickname = nickname;
    }
}

