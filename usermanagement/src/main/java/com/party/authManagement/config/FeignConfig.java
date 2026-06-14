package com.party.authManagement.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Request;
import feign.Retryer;

@Configuration
public class FeignConfig {
    @Bean
    public Request.Options requestOptions() {
        // Connect timeout, Read timeout, and boolean to follow redirects
        return new Request.Options(
            5000, TimeUnit.MILLISECONDS, 
            10000, TimeUnit.MILLISECONDS, 
            true
        );
    }

    @Bean
    public Retryer feignRetryer() {
        // period: initial backoff interval (100ms)
        // maxPeriod: maximum backoff interval (1000ms)
        // maxAttempts: total attempts including the first execution (e.g., 3 means 1 call + 2 retries)
        return new Retryer.Default(100, 1000, 3);
    }

}
