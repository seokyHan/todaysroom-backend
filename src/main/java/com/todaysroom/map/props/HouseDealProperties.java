package com.todaysroom.map.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("housedeal.api")
public record HouseDealProperties(String host, String secret) {
}
