package com.example.backend.service;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.entity.Category;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<CategoryDTO> getCategories() {
        User user = getCurrentUser();
        List<Category> categories = categoryRepository.findByUserIdOrUserIdIsNull(user.getId());
        return categories.stream()
            .map(CategoryDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("分类", "id", id));
        return CategoryDTO.fromEntity(category);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) {
        User user = getCurrentUser();
        
        Category category = new Category();
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setColor(dto.getColor());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        category.setUser(user);
        
        Category saved = categoryRepository.save(category);
        return CategoryDTO.fromEntity(saved);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("分类", "id", id));
        
        User currentUser = getCurrentUser();
        if (category.getUser() != null && !category.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(403, "无权修改该分类");
        }
        
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        if (dto.getIcon() != null) {
            category.setIcon(dto.getIcon());
        }
        if (dto.getColor() != null) {
            category.setColor(dto.getColor());
        }
        if (dto.getSortOrder() != null) {
            category.setSortOrder(dto.getSortOrder());
        }
        
        Category saved = categoryRepository.save(category);
        return CategoryDTO.fromEntity(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("分类", "id", id));
        
        User currentUser = getCurrentUser();
        if (category.getUser() != null && !category.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(403, "无权删除该分类");
        }
        
        if (category.getUser() == null) {
            throw new BusinessException(400, "系统分类不能删除");
        }
        
        categoryRepository.delete(category);
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));
    }
}
