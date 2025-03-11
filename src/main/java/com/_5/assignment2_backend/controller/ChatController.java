package com._5.assignment2_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final WebClient webClient; // Use WebClient for async calls
    private final String agentUID = "booking-agent"; // Replace with actual agent UID
    private final HttpHeaders headers;

    @Autowired
    public ChatController(WebClient.Builder webClientBuilder) {
        String chatUrl = "https://" + "271252330adce836" + ".api-" + "eu" + ".cometchat.io/v3";
        this.webClient = webClientBuilder.baseUrl(chatUrl).build();
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
        this.headers.add("apiKey", "0c4c9e3143feaa21b5e8704b344285de022e197d");
    }

    @GetMapping("/create")
    public ResponseEntity createUser() {
        HashMap<Object, Object> data = new HashMap<>();
        data.put("uid", System.currentTimeMillis());
        data.put("name", "customer");

        return webClient.post()
                .uri("/users")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(data)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(response -> {
                    String uid = response.get("data").get("uid").toString();
                    return requestAuthToken(uid).map(token -> ResponseEntity.ok(token));
                })
                .block();
    }

    @GetMapping("/users")
    public ResponseEntity getUsers() {
        return webClient.get()
                .uri("/users")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    List<JsonNode> users = StreamSupport.stream(response.get("data").spliterator(), false)
                            .filter(user -> !user.get("uid").asText().equals(agentUID))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(users);
                })
                .block();
    }

    @GetMapping("/auth")
    public ResponseEntity authenticateUser(@RequestParam String uid) {
        return requestAuthToken(uid)
                .map(token -> ResponseEntity.ok(token))
                .block();
    }

    private Mono<JsonNode> requestAuthToken(String uid) {
        return webClient.post()
                .uri("/users/" + uid + "/auth_tokens")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> response.get("data"));
    }
}
