package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    boolean existsByCode(String code);
}