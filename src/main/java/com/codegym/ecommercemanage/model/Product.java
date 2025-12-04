package com.codegym.ecommercemanage.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;

    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    private String status;

    private Integer quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}