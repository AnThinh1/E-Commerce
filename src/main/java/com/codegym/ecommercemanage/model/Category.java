package com.codegym.ecommercemanage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include   // 🔥 DÒNG QUYẾT ĐỊNH
    private Integer id;

    private String categoryName;

    @ManyToMany(mappedBy = "managedCategories")
    @JsonIgnore
    @ToString.Exclude
    private Set<User> staffs;
}