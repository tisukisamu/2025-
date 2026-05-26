package com.example.backend.service;

import com.example.backend.entity.Category;
import com.example.backend.exception.BusinessException;
import com.example.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIdIsNullOrderBySortOrderAsc();
    }

    public List<Category> getSubCategories(Long parentId) {
        return categoryRepository.findByParentIdOrderBySortOrderAsc(parentId);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
    }

    @Transactional
    public Category createCategory(String name, Long parentId, Integer sortOrder) {
        if (categoryRepository.existsByName(name)) {
            throw new BusinessException("分类名称已存在");
        }
        
        Category category = new Category();
        category.setName(name);
        category.setParentId(parentId);
        category.setSortOrder(sortOrder != null ? sortOrder : 0);
        
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, String name, Integer sortOrder) {
        Category category = findById(id);
        
        if (name != null && !name.equals(category.getName())) {
            if (categoryRepository.existsByName(name)) {
                throw new BusinessException("分类名称已存在");
            }
            category.setName(name);
        }
        
        if (sortOrder != null) {
            category.setSortOrder(sortOrder);
        }
        
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findById(id);
        
        List<Category> children = categoryRepository.findByParentIdOrderBySortOrderAsc(id);
        if (!children.isEmpty()) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }
        
        categoryRepository.delete(category);
    }
}
