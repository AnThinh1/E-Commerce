package com.codegym.ecommercemanage.service;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.types.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.util.*;

@Service
public class ImageGenerationService {

    // Fallback models - Thử tuần tự nếu model trước fail
    private static final List<String> FALLBACK_MODELS = List.of(
            "gemini-2.5-flash-image",              // Stable, fast
            "gemini-3-pro-image-preview",          // Mới nhất, chất lượng cao
            "gemini-2.0-flash-exp-image-generation" // Backup cũ
    );

    private final Client genaiClient;

    public ImageGenerationService(Client genaiClient) {
        this.genaiClient = genaiClient;
    }

    /**
     * IMAGE → IMAGE (Virtual Try-On) với retry và fallback
     */
    public byte[] generateTryOnRaw(
            String prompt,
            List<byte[]> imageByteList,
            List<String> mimeTypes
    ) {

        List<Part> parts = new ArrayList<>();
        parts.add(Part.fromText(prompt));

        for (int i = 0; i < imageByteList.size(); i++) {
            parts.add(
                    Part.fromBytes(
                            imageByteList.get(i),
                            mimeTypes.get(i)
                    )
            );
        }

        Content content = Content.builder()
                .parts(parts)
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseModalities(List.of("Image"))
                .build();

        // Thử từng model trong danh sách fallback
        Exception lastException = null;
        
        for (String model : FALLBACK_MODELS) {
            try {
                System.out.println("🔄 Trying model: " + model);
                
                GenerateContentResponse response =
                        genaiClient.models.generateContent(model, content, config);

                List<byte[]> images = extractImages(response);
                
                if (images.isEmpty()) {
                    System.err.println("⚠️ Model " + model + " returned empty image");
                    continue; // Thử model tiếp theo
                }
                
                System.out.println("✅ Success with " + model + " - Image size: " + images.get(0).length + " bytes");
                return images.get(0);

            } catch (Exception e) {
                lastException = e;
                String errorMsg = e.getMessage();
                
                System.err.println("❌ Model " + model + " failed: " + e.getClass().getSimpleName());
                System.err.println("   Reason: " + errorMsg);
                
                // Nếu lỗi quota/rate limit, không cần thử model khác (cùng API key)
                if (errorMsg != null && (errorMsg.contains("429") || 
                                         errorMsg.contains("quota") || 
                                         errorMsg.contains("rate limit"))) {
                    System.err.println("🚫 QUOTA EXCEEDED - Cần API key mới hoặc đợi reset");
                    break; // Dừng thử, không fallback nữa
                }
                
                // Với lỗi khác, thử model tiếp theo
                System.out.println("   → Trying next fallback model...");
            }
        }

        // Tất cả models đều fail
        System.err.println("💥 All models failed!");
        if (lastException != null) {
            lastException.printStackTrace();
        }
        return null;
    }

    /**
     * Tách image bytes từ response
     */
    private List<byte[]> extractImages(GenerateContentResponse response) {

        ImmutableList<Part> parts = response.parts();
        if (parts == null || parts.isEmpty()) return List.of();

        return parts.stream()
                .map(Part::inlineData)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(d -> d.data().isPresent())
                .map(d -> d.data().get())
                .toList();
    }
}
