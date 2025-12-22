package com.codegym.ecommercemanage.repository;

import org.springframework.stereotype.Repository;
import com.codegym.ecommercemanage.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
