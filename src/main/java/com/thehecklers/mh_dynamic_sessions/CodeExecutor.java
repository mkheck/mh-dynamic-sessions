package com.thehecklers.mh_dynamic_sessions;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CodeExecutor {
    public void execute(String url, String token) throws Exception {
        RestClient client = RestClient.create();

        // Define JSON payload
        // Note: inline code execution is python
        String json = """
                {
                    "properties": {
                    "codeInputType": "inline",
                    "executionType": "synchronous",
                    "code": "print('Hello, world!')"
                    }
                }
                """;

        ResponseEntity<String> response = client.post()
                .uri(url)
                .body(json)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println(response.getBody());
        } else {
            // MH: Workshop this
            throw new Exception("Unexpected code: " + response.getStatusCode());
//            System.out.println("Unexpected code from " + this.getClass().getName()
//                    + ": " + response.getStatusCode());
        }
    }
}
