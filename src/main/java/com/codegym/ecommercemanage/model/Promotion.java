package com.codegym.ecommercemanage.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // VD: SALE10, NEWYEAR2025

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double discountValue; // 10, 20, 50000

    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // PERCENT, FIXED

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean active;
}