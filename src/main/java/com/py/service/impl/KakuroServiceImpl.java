package com.py.service.impl;

import com.py.service.KakuroService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class KakuroServiceImpl implements KakuroService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String generateKakuro(int size) {
        String url = "http://localhost:5000/generate-game?size=" + size;
        return restTemplate.getForObject(url, String.class);
    }

    public String solveKakuro(String boardJson) {
        String url = "http://localhost:5000/solve-kakuro";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(boardJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }
}
