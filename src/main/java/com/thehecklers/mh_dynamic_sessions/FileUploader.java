package com.thehecklers.mh_dynamic_sessions;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.File;

@Component
public class FileUploader {
    public String upload(String url, String token, String filename) throws Exception {
        RestClient client = RestClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();

        FileSystemResource fileResource = new FileSystemResource(new File(filename));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", fileResource, MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = client.post()
                .uri(url)
                .body(builder.build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new Exception("Unexpected code: " + response.getStatusCode());
        }
    }
}
