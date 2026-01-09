package com.codegym.ecommercemanage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GeminiApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String generateContentWithImage(String promptText, String base64Image, String mimeType) {
        // 1. Tạo URL có chứa API Key
        String finalUrl = apiUrl + "?key=" + apiKey;

        // 2. Tạo Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. Xây dựng Body JSON theo cấu trúc của Gemini API
        /*
         Cấu trúc JSON cần gửi:
         {
           "contents": [{
             "parts": [
               { "text": "Lời nhắc (prompt)..." },
               {
                 "inline_data": {
                   "mime_type": "image/jpeg",
                   "data": "base64_string..."
                 }
               }
             ]
           }]
         }
        */
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();

        // Part 1: Text Prompt
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", promptText);
        parts.add(textPart);

        // Part 2: Image Data (Nếu có ảnh)
        if (base64Image != null && !base64Image.isEmpty()) {
            Map<String, Object> imagePart = new HashMap<>();
            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mime_type", mimeType);
            inlineData.put("data", base64Image);

            imagePart.put("inline_data", inlineData);
            parts.add(imagePart);
        }

        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        // 4. Gửi Request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(finalUrl, entity, Map.class);
            return extractTextFromResponse(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi gọi Gemini AI: " + e.getMessage();
        }
    }

    // Hàm phụ để bóc tách text từ cục JSON phản hồi loằng ngoằng của Google
    private String extractTextFromResponse(Map<String, Object> responseBody) {
        if (responseBody == null) return "Không nhận được phản hồi.";

        try {
            // Cấu trúc response: candidates[0] -> content -> parts[0] -> text
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> firstCandidate = candidates.get(0);
                Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

                if (parts != null && !parts.isEmpty()) {
                    return (String) parts.get(0).get("text");
                }
            }
        } catch (Exception e) {
            return "Lỗi đọc dữ liệu phản hồi.";
        }
        return "AI không trả lời được nội dung này.";
    }
}
