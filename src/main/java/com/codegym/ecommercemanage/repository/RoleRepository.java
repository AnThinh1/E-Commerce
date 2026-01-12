package com.codegym.ecommercemanage.repository;
import java.util.Optional;
import com.codegym.ecommercemanage.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository


public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
