package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.dto.response.CommentResponseDTO;
import com.codegym.ecommercemanage.model.Comment;
import com.codegym.ecommercemanage.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProduct_IdOrderByCreatedAtDesc(Integer id);
}
