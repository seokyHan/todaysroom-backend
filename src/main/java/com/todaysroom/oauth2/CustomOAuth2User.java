package com.todaysroom.oauth2;

import com.todaysroom.types.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    // OAuth 로그인 시 처음 로그인일 경우, 내 서비스에 Resource Server가 제공하지 않는 정보가 필요한 경우,
    // Resource Server가 아닌 내 서비스에서 해당 정보를 사용자에게 입력 받아야 함 (ex - 사는 도시, 나이등)
    private String email;

    // OAuth 로그인 시 위의 추가 정보(사는 도시, 나이 등)을 입력했는지 (처음 OAuth 로그인인지)를 판단하기 위함
    // 첫 로그인시 ROLE_GUEST, 추가 정보 입력 후 회원가입 진행시 ROLE_USER
    //private Role role;

    /**
     * Constructs a {@code DefaultOAuth2User} using the provided parameters.
     *
     * @param authorities      the authorities granted to the user
     * @param attributes       the attributes about the user
     * @param nameAttributeKey the key used to access the user's &quot;name&quot; from
     *                         {@link #getAttributes()}
     */
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            String email
                            //Role role
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        //this.role = role;

    }
}
