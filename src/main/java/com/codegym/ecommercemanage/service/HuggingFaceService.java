package com.codegym.ecommercemanage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Service Virtual Try-On với Free Tier
 * Sử dụng kỹ thuật image blending đơn giản
 */
@Service
public class HuggingFaceService {

    // Stable Diffusion Inpainting - FREE tier
    private static final String SD_API_URL = "https://api-inference.huggingface.co/models/runwayml/stable-diffusion-inpainting";

    @Value("${huggingface.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Virtual Try-On: Ghép ảnh người với quần áo
     * Approach: Image blending + AI enhancement (nếu có)
     */
    public byte[] generateVirtualTryOn(byte[] personImage, byte[] garmentImage) throws IOException {
        
        System.out.println("=== VIRTUAL TRY-ON (Free Method) ===");
        System.out.println("Person image: " + personImage.length + " bytes");
        System.out.println("Garment image: " + garmentImage.length + " bytes");

        try {
            // Method 1: Thử gọi Stable Diffusion (có thể fail do rate limit)
            return tryStableDiffusion(personImage, garmentImage);
        } catch (Exception e) {
            System.err.println("⚠️ AI API failed, using fallback method...");
            
            // Method 2: Fallback - Simple image overlay
            return simpleImageBlend(personImage, garmentImage);
        }
    }

    /**
     * Method 1: Dùng Stable Diffusion Inpainting
     */
    private byte[] tryStableDiffusion(byte[] personImage, byte[] garmentImage) throws IOException {
        System.out.println("📡 Trying Stable Diffusion API...");
        
        // Tạo prompt mô tả
        String prompt = "A person wearing the clothing item, photorealistic, high quality, natural lighting";
        
        // Payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("inputs", prompt);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("image", Base64.getEncoder().encodeToString(personImage));
        payload.put("parameters", parameters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
            SD_API_URL,
            HttpMethod.POST,
            request,
            byte[].class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            System.out.println("✅ Stable Diffusion success!");
            return response.getBody();
        }
        
        throw new IOException("SD API failed");
    }

    /**
     * Method 2: Simple Image Blending (100% Free, Always Works)
     * Ghép ảnh garment lên ảnh person ở giữa
     */
    private byte[] simpleImageBlend(byte[] personBytes, byte[] garmentBytes) throws IOException {
        System.out.println("🎨 Using simple image blending...");
        
        // Load images
        BufferedImage personImg = ImageIO.read(new ByteArrayInputStream(personBytes));
        BufferedImage garmentImg = ImageIO.read(new ByteArrayInputStream(garmentBytes));
        
        if (personImg == null || garmentImg == null) {
            throw new IOException("Cannot read images");
        }

        // Tạo canvas với kích thước ảnh người
        int width = personImg.getWidth();
        int height = personImg.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = result.createGraphics();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // 1. Vẽ ảnh người làm background
        g2d.drawImage(personImg, 0, 0, width, height, null);
        
        // 2. Resize ảnh quần áo
        int garmentWidth = (int)(width * 0.4);  // 40% chiều rộng
        int garmentHeight = (int)(garmentImg.getHeight() * ((double)garmentWidth / garmentImg.getWidth()));
        
        // 3. Vị trí ghép (giữa - phần ngực)
        int x = (width - garmentWidth) / 2;
        int y = (int)(height * 0.3); // 30% từ trên xuống
        
        // 4. Vẽ ảnh quần áo với transparency
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // 70% opacity
        g2d.drawImage(garmentImg, x, y, garmentWidth, garmentHeight, null);
        
        // 5. Thêm border cho garment
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, garmentWidth, garmentHeight);
        
        g2d.dispose();
        
        // Convert BufferedImage to byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "PNG", baos);
        byte[] resultBytes = baos.toByteArray();
        
        System.out.println("✅ Blended image created: " + resultBytes.length + " bytes");
        return resultBytes;
    }
}
