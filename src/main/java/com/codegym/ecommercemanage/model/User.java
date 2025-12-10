package com.codegym.ecommercemanage.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String fullName;

    private String phone;

    private String address;

    private String email;

    private LocalDateTime createdAt;

    // CẤU HÌNH QUAN HỆ N-N
    // FetchType.EAGER: Để khi query User thì lấy luôn Role (cần thiết cho Spring Security login)
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles", // Tên bảng trung gian sẽ được tạo trong DB
            joinColumns = @JoinColumn(name = "user_id"), // Khóa ngoại trỏ về User
            inverseJoinColumns = @JoinColumn(name = "role_id") // Khóa ngoại trỏ về Role
    )
    @ToString.Exclude // Quan trọng: Tránh lỗi StackOverflow khi dùng Lombok
    @Builder.Default // Để Builder không set null cho Set này
    private Set<Role> roles = new HashSet<>();
}