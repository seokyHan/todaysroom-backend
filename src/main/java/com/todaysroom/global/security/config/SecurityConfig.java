package com.todaysroom.global.security.config;

import com.todaysroom.global.security.filter.ExceptionHandlerFilter;
import com.todaysroom.global.security.filter.JwtFilter;
import com.todaysroom.global.security.props.ExcludeProperties;
import com.todaysroom.oauth2.handler.OAuth2LoginFailureHandler;
import com.todaysroom.oauth2.handler.OAuth2LoginSuccessHandler;
import com.todaysroom.oauth2.service.CustomOAuth2UserService;
import com.todaysroom.global.security.handler.JwtAccessDeniedHandler;
import com.todaysroom.global.security.handler.JwtAuthenticationEntryPoint;
import com.todaysroom.global.security.jwt.TokenProvider;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig{
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final ExcludeProperties excludeProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With", "Set-Cookie",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setMaxAge(6000L);
        configuration.setAllowCredentials(true); // 내 서버가 응답할 때 json을 JS에서 처리할 수 있게 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {
        AuthorityAuthorizationManager<RequestAuthorizationContext> user = AuthorityAuthorizationManager.hasAuthority("ROLE_USER");
        AuthorityAuthorizationManager<RequestAuthorizationContext> admin = AuthorityAuthorizationManager.hasAuthority("ROLE_ADMIN");
        AuthorityAuthorizationManager<RequestAuthorizationContext> guest = AuthorityAuthorizationManager.hasAuthority("ROLE_GUEST");
        AntPathRequestMatcher[] antPathRequestMatchers = stream(excludeProperties.path()).map(AntPathRequestMatcher::antMatcher).toArray(AntPathRequestMatcher[]::new);

        http
                .cors().configurationSource(corsConfigurationSource()).and()
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(antPathRequestMatchers).permitAll()
                                .requestMatchers(getGuestMatchers()).access(guest)
                                .requestMatchers(getUserMatchers()).access(user)
                                .requestMatchers(getAdminMatcher()).access(admin)
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                    exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler)
                                     .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtFilter(tokenProvider, excludeProperties), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtFilter.class)
                //== 소셜 로그인 설정 ==//
                .oauth2Login()
                .successHandler(oAuth2LoginSuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정
                .failureHandler(oAuth2LoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
                .userInfoEndpoint().userService(customOAuth2UserService); // customUserService 설정

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().requestMatchers("/images/**","/upload-dir/**","/h2-console/**","/favicon.ico");
    }

    @NotNull
    private RequestMatcher[] getUserMatchers() {
        return new RequestMatcher[]{antMatcher(POST, "/users/**"),
                antMatcher(GET, "/map/**"),
                antMatcher(POST, "/files/**"),
                antMatcher(POST, "/batch/**")
        };
    }

    @NotNull
    private RequestMatcher[] getAdminMatcher() {
        return new RequestMatcher[]{antMatcher(POST, "/users/**"),
                antMatcher(GET, "/map/**"),
                antMatcher(POST, "/files/**"),
                antMatcher(POST, "/batch/**")
        };
    }

    @NotNull
    private RequestMatcher[] getGuestMatchers() {
        return new RequestMatcher[]{antMatcher(POST, "/users/social/signup"),
        };
    }
}
