package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String name);
}
