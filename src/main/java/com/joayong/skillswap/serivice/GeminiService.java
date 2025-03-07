package com.joayong.skillswap.serivice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joayong.skillswap.domain.gemini.dto.response.ParseGeminiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GeminiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public GeminiResponse getGeminiResponse(String prompt) {
        String url = "YOUR_GEMINI_API_URL"; // 실제 Gemini API URL로 대체
        String apiKey = "YOUR_API_KEY"; // 실제 API 키로 대체

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("x-goog-api-key", apiKey);

        String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + prompt + "\"}]}]}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        try {
            return objectMapper.readValue(responseEntity.getBody(), GeminiResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
