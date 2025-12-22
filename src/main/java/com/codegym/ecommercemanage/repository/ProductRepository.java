package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id); // đã có sẵn (không cần viết)

    List<Product> findByNameContainingIgnoreCase(String name); // search
}