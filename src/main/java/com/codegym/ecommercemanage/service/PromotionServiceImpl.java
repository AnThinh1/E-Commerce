package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.request.PromotionRequest;
import com.codegym.ecommercemanage.model.Promotion;
import com.codegym.ecommercemanage.repository.PromotionRepository;
import lombok.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public Promotion create(PromotionRequest request) {
        if (promotionRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Promotion code already exists");
        }

        Promotion promotion = Promotion.builder()
                .code(request.getCode())
                .name(request.getName())
                .discountValue(request.getDiscountValue())
                .discountType(request.getDiscountType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive())
                .build();

        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion update(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        promotion.setName(request.getName());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setActive(request.getActive());

        return promotionRepository.save(promotion);
    }

    @Override
    public void delete(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        promotionRepository.delete(promotion);
    }
}