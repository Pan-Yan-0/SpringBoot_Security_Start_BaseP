package com.py.service;

import org.springframework.stereotype.Service;

@Service
public interface KakuroService {
    String generateKakuro(int size);
    String solveKakuro(String boardJson);
}
