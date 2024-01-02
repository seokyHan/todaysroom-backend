package com.todaysroom.map.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kakao.api")
public record KaKaoProperties(String host, String secret) {
}
