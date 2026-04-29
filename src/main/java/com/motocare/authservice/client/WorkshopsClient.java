package com.motocare.authservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorkshopsClient {

    private final RestTemplate restTemplate;


    public WorkshopsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Long createWorkshop(String name, String address) {

        Map<String, String> request = new HashMap<>();
        request.put("name", name);
        request.put("address", address);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:8080/workshops/internal",
                request,
                Map.class
        );

        return Long.valueOf(response.getBody().get("id").toString());
    }
}
