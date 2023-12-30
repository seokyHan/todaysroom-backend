package com.todaysroom.global.security.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JWTProperties(String host,
                            String secret,
                            long tokenValidityInMilliseconds,
                            long refreshTokenValidityInMilliseconds) {
}
