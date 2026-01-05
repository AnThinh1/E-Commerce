package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    List<StockHistory> findByProductId(Integer productId);
    @Query("""
        SELECT COALESCE(SUM(s.quantity), 0)
        FROM StockHistory s
        WHERE s.product.id = :productId
        AND s.type = 'IMPORT'
    """)
    Integer totalImport(Integer productId);

    @Query("""
        SELECT COALESCE(SUM(s.quantity), 0)
        FROM StockHistory s
        WHERE s.product.id = :productId
        AND s.type = 'EXPORT'
    """)
    Integer totalExport(Integer productId);
}
