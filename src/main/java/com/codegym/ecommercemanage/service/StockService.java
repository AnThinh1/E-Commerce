package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.request.StockRequest;
import com.codegym.ecommercemanage.model.Product;
import com.codegym.ecommercemanage.model.StockHistory;
import com.codegym.ecommercemanage.repository.ProductRepository;
import com.codegym.ecommercemanage.repository.StockHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepo;
    private final StockHistoryRepository stockRepo;

    // ================= IMPORT =================
    @Transactional
    public void importStock(StockRequest request) {

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy sản phẩm")
                );

        product.setQuantity(
                product.getQuantity() + request.getQuantity()
        );
        productRepo.save(product);

        StockHistory history = StockHistory.builder()
                .product(product)
                .type("IMPORT")
                .quantity(request.getQuantity())
                .note(request.getNote())
                .createdAt(LocalDateTime.now())
                .build();

        stockRepo.save(history);
    }

    // ================= EXPORT =================
    @Transactional
    public void exportStock(StockRequest request) {

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy sản phẩm")
                );

        if (product.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Không đủ hàng trong kho");
        }

        product.setQuantity(
                product.getQuantity() - request.getQuantity()
        );
        productRepo.save(product);

        StockHistory history = StockHistory.builder()
                .product(product)
                .type("EXPORT")
                .quantity(request.getQuantity())
                .note(request.getNote())
                .createdAt(LocalDateTime.now())
                .build();

        stockRepo.save(history);
    }

    // ================= HISTORY =================
    public List<StockHistory> getHistory() {
        return stockRepo.findAll();
    }

    public List<StockHistory> getHistoryByProduct(Integer productId) {
        return stockRepo.findByProductId(productId);
    }
}
