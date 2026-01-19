package com.codegym.ecommercemanage.service;
import com.codegym.ecommercemanage.dto.request.PredictionRequestDTO;
import com.codegym.ecommercemanage.dto.response.PredictionResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AIService {

    private final RestClient restClient;

    // Spring sẽ tự động lấy Bean restClient từ file WebConfig ném vào đây
    public AIService(RestClient restClient) {
        this.restClient = restClient;
    }

    // 1. Hàm Train
    public String trainModel() {
        try {
            return restClient.post()
                    .uri("/train")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Lỗi kết nối AI: " + e.getMessage();
        }
    }

    // 2. Hàm Predict
    public PredictionResponseDTO predictPotential(Integer categoryId, Long price) {
        // Giả sử bạn đã có class PredictionRequest
        var request = new PredictionRequestDTO(categoryId, price);

        try {
            return restClient.post()
                    .uri("/predict")
                    .body(request)
                    .retrieve()
                    .body(PredictionResponseDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}