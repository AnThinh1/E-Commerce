package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
