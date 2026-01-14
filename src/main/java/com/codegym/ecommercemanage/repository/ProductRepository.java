package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("""
        SELECT p FROM Product p
        WHERE (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
    """)
    List<Product> filterProducts(
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("categoryId") Long categoryId,
            Sort sort
    );

    // cho STAFF (chỉ product thuộc category được quản lý)
    @Query("""
        SELECT p FROM Product p
        WHERE p.category.id IN :categoryIds
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
    """)
    List<Product> filterProductsForStaff(
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("categoryId") Long categoryId,
            Sort sort
    );
}