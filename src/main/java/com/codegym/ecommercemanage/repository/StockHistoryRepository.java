package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    List<StockHistory> findByProductId(Integer productId);
}
