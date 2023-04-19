package com.todaysroom.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Entity
@Getter
@Table(name = "userEntity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    @Builder
    public UserEntity(Long id, String userEmail, String password, String userName, String nickname) {
        this.id = id;
        this.userEmail = userEmail;
        this.password = password;
        this.userName = userName;
        this.nickname = nickname;
    }
}

