package com.todaysroom.global.common.config.props;

import com.todaysroom.global.common.config.redis.props.RedisProperties;
import com.todaysroom.global.security.props.ExcludeProperties;
import com.todaysroom.global.security.props.JWTProperties;
import com.todaysroom.map.props.HouseDealProperties;
import com.todaysroom.map.props.KaKaoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {
        JWTProperties.class,
        HouseDealProperties.class,
        KaKaoProperties.class,
        RedisProperties.class,
        ExcludeProperties.class
})
public class PropertiesConfiguration {
}
