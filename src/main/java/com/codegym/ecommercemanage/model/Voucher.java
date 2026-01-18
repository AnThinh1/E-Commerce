package com.codegym.ecommercemanage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private Integer discountPercent;

    private Long maxDiscount;

    private Long minOrderValue;

    private Integer quantity;

    private Boolean active;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToMany
    @JoinTable(
            name = "voucher_categories",
            joinColumns = @JoinColumn(name = "voucher_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}
