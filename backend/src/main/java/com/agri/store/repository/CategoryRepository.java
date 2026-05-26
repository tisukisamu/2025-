package com.agri.store.repository;

import com.agri.store.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentId(Long parentId);
    List<Category> findByLevel(Integer level);
    List<Category> findByActive(Boolean active);
    List<Category> findByParentIdAndActive(Long parentId, Boolean active);
}
