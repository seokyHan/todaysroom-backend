package com.todaysroom.global.security.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;


@ConfigurationProperties("exclude")
public record ExcludeProperties(String[] path) {


}
