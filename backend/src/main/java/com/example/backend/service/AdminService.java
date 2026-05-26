package com.example.backend.service;

import com.example.backend.dto.response.OrderDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.ProductDTO;
import com.example.backend.dto.response.UserDTO;
import com.example.backend.entity.Order;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public java.util.List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public PageResponse<UserDTO> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserDTO> dtoPage = userPage.map(UserDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public UserDTO updateUserStatus(Long id, User.Status status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setStatus(status);
        return UserDTO.fromEntity(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUserRole(Long id, User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setRole(role);
        return UserDTO.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    public PageResponse<ProductDTO> getProducts(int page, int size, Product.AuditStatus auditStatus) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Product> productPage;
        
        if (auditStatus != null) {
            productPage = productRepository.findByStatusAndAuditStatus(
                    Product.ProductStatus.PENDING, auditStatus, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }
        
        Page<ProductDTO> dtoPage = productPage.map(ProductDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public ProductDTO auditProduct(Long id, Product.AuditStatus auditStatus, String reason) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        product.setAuditStatus(auditStatus);
        product.setAuditReason(reason);
        
        if (auditStatus == Product.AuditStatus.APPROVED) {
            product.setStatus(Product.ProductStatus.ON_SALE);
        }
        
        Product updatedProduct = productRepository.save(product);
        return ProductDTO.fromEntity(updatedProduct);
    }

    public PageResponse<OrderDTO> getOrders(int page, int size, Order.OrderStatus status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Order> orderPage;
        
        if (status != null) {
            orderPage = orderRepository.findByStatusWithRelations(status, pageable);
        } else {
            orderPage = orderRepository.findAllWithRelations(pageable);
        }
        
        Page<OrderDTO> dtoPage = orderPage.map(OrderDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        
        long totalProducts = productRepository.count();
        long pendingProducts = productRepository.countByStatusAndAuditStatus(
                Product.ProductStatus.PENDING, Product.AuditStatus.PENDING);
        stats.put("totalProducts", totalProducts);
        stats.put("pendingProducts", pendingProducts);
        
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        long completedOrders = orderRepository.countByStatus(Order.OrderStatus.COMPLETED);
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("completedOrders", completedOrders);
        
        return stats;
    }
}
