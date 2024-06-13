package com.thehecklers.mh_dynamic_sessions;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties
public record Dynaprops(String region,
                        String subscriptionId,
                        String resourceGroup,
                        String sessionPoolName,
                        String sessionId,
                        String filename) {
    @ConstructorBinding
    public Dynaprops {
    }
}
