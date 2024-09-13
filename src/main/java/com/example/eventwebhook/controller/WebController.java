package com.example.eventwebhook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
public class WebController {


    @GetMapping("/hello")
    public String hello(String msg) {
        return "hello " + msg;
    }

    @PostMapping("/eventgrid")
    public ResponseEntity<Map<String, String>> event(@RequestBody List<Map<String, Object>> events) {

        Map<String, String> result = new HashMap<>();

        System.out.println("received events: " + events);
        for (Map<String, Object> event : events) {
            Map<String, String> data = (Map<String, String>) event.get("data");
            if (data.containsKey("validationCode")) {
                result.put("validationResponse", data.get("validationCode"));
                break;
            }
        }
        ResponseEntity<Map<String, String>> responseEntity = null;

        if (result.isEmpty()) {
            responseEntity = ResponseEntity.badRequest().build();
        } else {
            responseEntity = ResponseEntity.ok(result);
        }

        return responseEntity;
    }

    @PostMapping("/sendEvent")
    public ResponseEntity< String> sendEvent(String key, String value) throws IOException, InterruptedException {

        System.out.println("received event: key " + key + " value " + value);

        String id = UUID.randomUUID().toString();
        Map<String, String> map = new HashMap<>();
        map.put(key,value);
        String dataStr = new ObjectMapper().writeValueAsString(map);


        String eventData = "[ {\"id\": \"$RANDOM\", \"eventType\": \"recordInserted\", \"subject\": \"myapp/vehicles/motorcycles\", \"eventTime\": \"2024-09-13T09:09:50Z\", \"data\": $DATA,\"dataVersion\": \"1.0\"} ]";

        String body = eventData.replace("$RANDOM", id).replace("$DATA", dataStr);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://m01custopic.eastasia-1.eventgrid.azure.net/api/events"))
                .headers(
                        "aeg-sas-key", "3mPUn3bXshucJVk9xm4RvATH4jVyEh4KRk9n5WvJ4DqEFpQwMOnvJQQJ99AIAC3pKaRXJ3w3AAABAZEGVYcK",
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE
                ).POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(send.statusCode());

        return ResponseEntity.ok("sent");
    }

}
