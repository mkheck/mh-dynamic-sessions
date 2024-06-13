package com.thehecklers.mh_dynamic_sessions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class FileLister {
    public String list(String url, String token) throws Exception {
        RestClient client = RestClient.create();
        final ObjectMapper om = new ObjectMapper();

        ResponseEntity<String> response = client.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Unexpected code " + response);
        }

        List<String> files = new ArrayList<>();

        JsonNode propNodes = null;
        propNodes = om.readTree(response.getBody()).get("value");

        om.readTree(propNodes.toString())
                .forEach(f -> files.add(f.get("properties").get("filename").asText()));

        return files.get(0);
    }
}
