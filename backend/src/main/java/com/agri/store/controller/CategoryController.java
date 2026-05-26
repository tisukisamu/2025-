package com.agri.store.controller;

import com.agri.store.entity.Category;
import com.agri.store.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findByActive(true));
    }

    @GetMapping("/tree")
    public ResponseEntity<List<Category>> getCategoryTree() {
        // 获取所有一级分类
        List<Category> topCategories = categoryRepository.findByParentIdAndActive(0L, true);
        return ResponseEntity.ok(topCategories);
    }

    @GetMapping("/children/{parentId}")
    public ResponseEntity<List<Category>> getChildCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryRepository.findByParentIdAndActive(parentId, true));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
            category.setLevel(1);
        } else {
            categoryRepository.findById(category.getParentId())
                    .ifPresent(parent -> category.setLevel(parent.getLevel() + 1));
        }
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(category.getName());
                    existing.setDescription(category.getDescription());
                    existing.setIconUrl(category.getIconUrl());
                    existing.setSortOrder(category.getSortOrder());
                    existing.setActive(category.getActive());
                    return ResponseEntity.ok(categoryRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
