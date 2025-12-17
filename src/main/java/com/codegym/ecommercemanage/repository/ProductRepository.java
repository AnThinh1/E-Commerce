package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    public List<Product> getProducts();
    public Product getProduct(int id);
}
