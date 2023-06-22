package com.todaysroom.user.jwt;

import com.todaysroom.exception.CustomException;
import com.todaysroom.user.dto.UserTokenInfoDto;
import com.todaysroom.user.redis.BlackList;
import com.todaysroom.user.redis.BlackListRepository;
import com.todaysroom.user.redis.RefreshToken;
import com.todaysroom.user.types.AuthType;
import com.todaysroom.user.types.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = AuthType.AUTHORITIES_KEY.getByItem();
    public static final String AUTHORIZATION_HEADER = AuthType.AUTHORIZATION_HEADER.getByItem();
    public static final String REFRESHTOKEN_HEADER = AuthType.REFRESHTOKEN_HEADER.getByItem();
    public static final String TOKEN_HEADER = AuthType.TOKEN_HEADER.getByItem();
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private Key key;
    private final BlackListRepository blackListRepository;
    public TokenProvider(@Value("${jwt.secret}")  String secret,
                         @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds,
                         @Value("${jwt.refresh-token-validity-in-sec}") long refreshTokenValidityInMilliseconds,
                         BlackListRepository blackListRepository) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
        this.blackListRepository = blackListRepository;
    }

    // InitializingBean을 상속받고 afterPropertiesSet을 override한 이유는 빈이 생성되고
    // BASE64로 복호화된 시크릿키로 key를 할당하기 위함
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    //Authentication 을 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public UserTokenInfoDto generateToken(Authentication authentication) {
        return generateToken(authentication.getName(), authentication.getAuthorities());
    }

    //name, authorities 를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public UserTokenInfoDto generateToken(String name, Collection<? extends GrantedAuthority> inputAuthorities) {
        //권한 가져오기
        String authorities = inputAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInMilliseconds);
        Date rtkValidity = new Date(now + refreshTokenValidityInMilliseconds);
        log.info(" tokenValidityInMilliseconds {}", tokenValidityInMilliseconds);
        log.info(" refreshTokenValidityInMilliseconds {}", refreshTokenValidityInMilliseconds);
        log.info(" TokenProvider accessToken {}", validity);
        log.info(" TokenProvider refreshToken {}", rtkValidity);

        //Generate AccessToken
        String accessToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(validity)  //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();


        //Generate RefreshToken
        String refreshToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(rtkValidity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();


        return UserTokenInfoDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Long getExpiration(String token) {
        Date expiration = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expiration.getTime();
    }

    // request header에서 토큰 정보 가져옴
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_HEADER)){
            return bearerToken.substring(TOKEN_HEADER.length());
        }

        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            if(blackListRepository.findByToken(token) != null){
                return false;
            }

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;

    }
}
