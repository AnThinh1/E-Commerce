package com.codegym.ecommercemanage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class FashionAdvisorService {

    // Inject Bean xử lý gọi Gemini API (bạn đã có từ phần trước hoặc dùng RestTemplate)
    private final GeminiApiClient geminiClient;

    public String getAdvice(MultipartFile imageFile, String option) throws IOException {
        // 1. Chuyển ảnh sang Base64
        String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
        String mimeType = imageFile.getContentType(); // vd: image/jpeg

        // 2. Chọn Prompt dựa trên option người dùng gửi
        String promptText = buildPrompt(option);

        // 3. Gọi Gemini API (Sử dụng model gemini-1.5-flash cho nhanh)
        return geminiClient.generateContentWithImage(promptText, base64Image, mimeType);
    }

    private String buildPrompt(String option) {
        switch (option) {
            case "evaluate":
                return "Bạn là một stylist thời trang chuyên nghiệp. Hãy nhìn bức ảnh này và đánh giá: " +
                        "1. Bộ trang phục này có phù hợp với dáng người của họ không? " +
                        "2. Màu sắc phối hợp như thế nào? " +
                        "3. Đưa ra lời khuyên ngắn gọn để cải thiện phong cách."+
                        "Hãy chân thành đánh giá không nịnh bợ. ";

            case "suggest_pants":
                return "Bạn là stylist. Người trong ảnh đang mặc một chiếc áo (hoặc trang phục phần trên). " +
                        "Dựa trên màu sắc và kiểu dáng của chiếc áo đó, hãy gợi ý 3 mẫu quần (màu sắc, kiểu dáng, chất liệu) " +
                        "để phối hợp đẹp nhất. Giải thích ngắn gọn tại sao.";

            case "suggest_shirt":
                return "Bạn là stylist. Người trong ảnh đang mặc một chiếc quần (hoặc chân váy). " +
                        "Dựa trên chiếc quần đó, hãy gợi ý 3 mẫu áo (màu sắc, kiểu dáng) " +
                        "để tạo nên một bộ trang phục hoàn chỉnh và thời trang. Giải thích ngắn gọn.";

            default:
                return "Hãy mô tả trang phục trong ảnh và đưa ra lời khuyên thời trang chung.";
        }
    }
}
