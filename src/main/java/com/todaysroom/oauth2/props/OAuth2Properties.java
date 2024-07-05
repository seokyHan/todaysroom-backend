package com.todaysroom.oauth2.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth")
public record OAuth2Properties(String redirectUrl,
                               long accessTokenValidityInMilliseconds,
                               long refreshTokenValidityInMilliseconds) {
}
