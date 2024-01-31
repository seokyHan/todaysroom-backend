package com.todaysroom.global.common.config.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import java.net.URI;


@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) //milliseconds
                .doOnConnected(
                        connection -> connection.addHandlerLast(new ReadTimeoutHandler(5)) // sec
                                .addHandlerLast(new WriteTimeoutHandler(60)) // sec
                );

        //Memory 조정: 2M(default 256KB)
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2*1024*1024))
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filters(filters -> {
                    filters.add(replaceUrlFilter());
                    filters.add(requestFilter());
                    filters.add(responseFilter());
                })
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    private ExchangeFilterFunction requestFilter(){
        return ExchangeFilterFunction.ofRequestProcessor(
                clientRequest -> {
                    log.info(">>>>>>>>>> REQUEST <<<<<<<<<<");
                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
                    clientRequest.headers().forEach(
                            (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
                    );
                    return Mono.just(clientRequest);
                }
        );
    }

    private ExchangeFilterFunction responseFilter(){
        return ExchangeFilterFunction.ofResponseProcessor(
                clientResponse -> {
                    log.info(">>>>>>>>>> RESPONSE <<<<<<<<<<");
                    clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{} : {}", name, value)));
                    return Mono.just(clientResponse);
                }
        );
    }

    private ExchangeFilterFunction replaceUrlFilter(){
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            URI originalUri = clientRequest.url();
            String escapedQuery = originalUri.getRawQuery().replace("+", "%2B");
            URI modifiedUri = UriComponentsBuilder.fromUri(originalUri)
                    .replaceQuery(escapedQuery)
                    .build(true).toUri();

            return Mono.just(ClientRequest.from(clientRequest).url(modifiedUri).build());
        });
    }

}
