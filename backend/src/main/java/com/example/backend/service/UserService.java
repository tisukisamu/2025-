package com.example.backend.service;

import com.example.backend.dto.FavoriteDTO;
import com.example.backend.dto.request.ChangePasswordRequest;
import com.example.backend.dto.request.UpdateUserRequest;
import com.example.backend.dto.response.OrderDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserDTO;
import com.example.backend.entity.Favorite;
import com.example.backend.entity.Order;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.FavoriteRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public UserDTO getUserInfo(Long userId) {
        User user = findById(userId);
        return UserDTO.fromEntity(user);
    }

    @Transactional
    public UserDTO updateUserInfo(Long userId, UpdateUserRequest request) {
        User user = findById(userId);
        
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        
        User updatedUser = userRepository.save(user);
        return UserDTO.fromEntity(updatedUser);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = findById(userId);
        
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public PageResponse<OrderDTO> getUserOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Order> orderPage = orderRepository.findByUserIdWithAllRelations(userId, pageable);
        Page<OrderDTO> dtoPage = orderPage.map(OrderDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    public PageResponse<FavoriteDTO> getUserFavorites(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Favorite> favoritePage = favoriteRepository.findByUserIdWithProduct(userId, pageable);
        Page<FavoriteDTO> dtoPage = favoritePage.map(FavoriteDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public void addFavorite(Long userId, Long productId) {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new BusinessException("已经收藏过该商品");
        }
        
        User user = findById(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
        
        product.setFavoriteCount(product.getFavoriteCount() + 1);
        productRepository.save(product);
    }

    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        Favorite favorite = favoriteRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new BusinessException("未收藏该商品"));
        
        Product product = favorite.getProduct();
        favoriteRepository.delete(favorite);
        
        product.setFavoriteCount(Math.max(0, product.getFavoriteCount() - 1));
        productRepository.save(product);
    }

    public UserDTO getPublicUserInfo(Long userId) {
        User user = findById(userId);
        return UserDTO.fromEntity(user);
    }
}
