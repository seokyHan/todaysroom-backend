package com.todaysroom.global.security.jwt;

import com.todaysroom.global.security.jwt.types.TokenType;
import com.todaysroom.global.security.props.JWTProperties;
import com.todaysroom.global.types.Role;
import com.todaysroom.user.dto.UserTokenInfoDto;
import com.todaysroom.global.types.AuthType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static com.todaysroom.global.security.jwt.types.TokenType.ACCESS_TOKEN;
import static com.todaysroom.global.security.jwt.types.TokenType.REFRESH_TOKEN;
import static com.todaysroom.global.types.AuthType.AUTHORITIES_KEY;

@Slf4j
@Component
public class TokenProvider{
    private static final Map<String, Object> jwtHeader = Map.of("alg", "HS512", "typ", "JWT");
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final RedisTemplate redisTemplate;
    private final Key key;

    @Autowired
    public TokenProvider(JWTProperties properties,
                         RedisTemplate redisTemplate) {
        this.tokenValidityInMilliseconds = properties.tokenValidityInMilliseconds();
        this.refreshTokenValidityInMilliseconds = properties.refreshTokenValidityInMilliseconds();
        byte[] keyBytes = Decoders.BASE64.decode(properties.secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisTemplate = redisTemplate;
    }

    public String oAuth2CreateAccessToken(String email, Role role) {

        return Jwts.builder()
                .setHeader(jwtHeader)
                .setSubject(email)
//                .setIssuer(format("https://%s",host))
//                .setAudience(host)
                .claim(AUTHORITIES_KEY.getItem(), role)
                .setExpiration(getTokenExpiration(ACCESS_TOKEN))  //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String oAuth2CreateRefreshToken(String email, Role role) {

        return Jwts.builder()
                .setHeader(jwtHeader)
                .setSubject(email)
                .claim(AUTHORITIES_KEY.getItem(), role)
                .setExpiration(getTokenExpiration(REFRESH_TOKEN))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    //name, authorities 를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public UserTokenInfoDto generateToken(Authentication authentication) {
        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        //Generate AccessToken
        String accessToken = Jwts.builder()
                .setHeader(jwtHeader)
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY.getItem(), authorities)
                .setExpiration(getTokenExpiration(ACCESS_TOKEN))  //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();


        //Generate RefreshToken
        String refreshToken = Jwts.builder()
                .setHeader(jwtHeader)
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY.getItem(), authorities)
                .setExpiration(getTokenExpiration(REFRESH_TOKEN))
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

    public long getExpiration(String token) {
        Date expiration = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expiration.getTime();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            if(redisTemplate.opsForValue().get(token) != null){
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

    private Date getTokenExpiration(final TokenType tokenType){
        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        long now = zdt.toInstant().toEpochMilli();
        Date validity = new Date(now + tokenValidityInMilliseconds);
        if(tokenType.equals(REFRESH_TOKEN)){
            validity = new Date(now + refreshTokenValidityInMilliseconds);
        }

        return validity;
    }
}
