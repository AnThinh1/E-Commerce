package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.model.Product;
import com.codegym.ecommercemanage.repository.CategoryRepository;
import com.codegym.ecommercemanage.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> findAll() {
        return productRepository.getProducts();
    }

    public Product findById(int id) {
        return productRepository.getProduct(id);
    }

    public void delete(int id) {
        productRepository.deleteById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product update(Product product) {
        return productRepository.save(product);
    }
}
