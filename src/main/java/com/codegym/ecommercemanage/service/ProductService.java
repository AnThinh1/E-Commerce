package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.response.CommentResponseDTO;
import com.codegym.ecommercemanage.dto.response.ProductDetailResponseDTO;
import com.codegym.ecommercemanage.model.Comment;
import com.codegym.ecommercemanage.model.Product;
import com.codegym.ecommercemanage.model.User;
import com.codegym.ecommercemanage.repository.CategoryRepository;
import com.codegym.ecommercemanage.repository.CommentRepository;
import com.codegym.ecommercemanage.repository.ProductRepository;
import com.codegym.ecommercemanage.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.codegym.ecommercemanage.dto.request.ProductRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    public void delete(int id) {
        productRepository.deleteById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product update(Product product) {
        return productRepository.save(product);
    }

    // --- HÀM MỚI: Lấy chi tiết sản phẩm kèm comment ---
    public ProductDetailResponseDTO getProductDetailWithComments(int productId) {
        // 1. Lấy Product
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return null;

        // 2. Lấy danh sách Comment thuộc product đó
        List<Comment> comments = commentRepository.findByProduct_IdOrderByCreatedAtDesc(productId);

        // 3. Convert List<Comment> sang List<CommentResponseDTO>
        List<CommentResponseDTO> commentDTOs = comments.stream().map(c -> {
            CommentResponseDTO dto = new CommentResponseDTO();
            dto.setId(c.getId());
            dto.setContent(c.getContent());
            dto.setCreatedAt(c.getCreatedAt());
            // Lấy tên người dùng, nếu user null thì để ẩn danh
            dto.setUserFullName(c.getUser() != null ? c.getUser().getFullName() : "Người dùng ẩn danh");
            return dto;
        }).collect(Collectors.toList());

        // 4. Map sang ProductDetailResponseDTO
        ProductDetailResponseDTO response = new ProductDetailResponseDTO();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        response.setImage(product.getImage());
        response.setStatus(product.getStatus());
        response.setQuantity(product.getQuantity());
        response.setCategoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : "");

        // Gán list comment vào
        response.setComments(commentDTOs);

        return response;
    }

    @Transactional
    public void updateProductByStaff(
            Integer productId,
            ProductRequest req,
            Long staffId
    ) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // CHECK QUYỀN THEO CATEGORY
        if (!staff.getManagedCategories().contains(product.getCategory())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa ngành hàng này");
        }

        // UPDATE
        if (req.getName() != null)
            product.setName(req.getName());

        if (req.getPrice() != null)
            product.setPrice(req.getPrice()); // hoặc Long nếu đã đổi entity

        if (req.getQuantity() != null)
            product.setQuantity(req.getQuantity());

        if (req.getStatus() != null)
            product.setStatus(req.getStatus());

        if (req.getDescription() != null)
            product.setDescription(req.getDescription());
    }
}
