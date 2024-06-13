package com.thehecklers.mh_dynamic_sessions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MhDynamicSessionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MhDynamicSessionsApplication.class, args);
	}

}
