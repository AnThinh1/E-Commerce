package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
}
