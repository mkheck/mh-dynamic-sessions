package com.thehecklers.mh_dynamic_sessions;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FileDownloader {
    public void download(String url, String token) throws Exception {

        final RestClient client = RestClient.create();

        ResponseEntity<String> response = client.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println(response.getBody()); }
        else {
            throw new Exception("Unexpected code " + response);
//            System.out.println("Unexpected code from " + this.getClass().getName()
//                    + ": " + response.getStatusCode());
        }
    }

}
