package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.request.PromotionRequest;
import com.codegym.ecommercemanage.model.Promotion;

public interface PromotionService {
    Promotion create(PromotionRequest request);

    Promotion update(Long id, PromotionRequest request);

    void delete(Long id);
}
