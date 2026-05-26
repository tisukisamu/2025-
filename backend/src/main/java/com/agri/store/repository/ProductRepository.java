package com.agri.store.repository;

import com.agri.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 基础查询：已上架且状态正常
    List<Product> findByActiveTrueAndStatus(Integer status);
    
    Optional<Product> findByIdAndActiveTrueAndStatus(Long id, Integer status);
    
    // 首页推荐
    List<Product> findByActiveTrueAndStatusAndIsNewTrueOrderBySalesDesc(Integer status);
    List<Product> findByActiveTrueAndStatusAndIsHotTrueOrderBySalesDesc(Integer status);
    
    // 搜索 (已审核通过 + 已上架)
    Page<Product> findByActiveTrueAndStatusAndNameContainingIgnoreCase(Integer status, String name, Pageable pageable);
    
    // 分类筛选
    Page<Product> findByActiveTrueAndStatusAndCategoryId(Integer status, Long categoryId, Pageable pageable);

    // 店家管理相关
    List<Product> findByStoreIdAndStatus(Long storeId, Integer status);

    // 店家查询自己的商品 (分页 + 搜索)
    Page<Product> findByStoreId(Long storeId, Pageable pageable);
    Page<Product> findByStoreIdAndNameContainingIgnoreCase(Long storeId, String name, Pageable pageable);
    
    // 店家按分类查询
    Page<Product> findByStoreIdAndCategoryId(Long storeId, Long categoryId, Pageable pageable);
    
    // 管理员查询
    List<Product> findByStatus(Integer status); // 查询待审核(0)等
    Page<Product> findByStatus(Integer status, Pageable pageable);

    // 管理员按名称搜索
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // 管理员按状态和名称搜索
    Page<Product> findByStatusAndNameContainingIgnoreCase(Integer status, String name, Pageable pageable);

    // 管理员按分类查询
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // 管理员按状态和分类查询
    Page<Product> findByStatusAndCategoryId(Integer status, Long categoryId, Pageable pageable);
    
    // 兼容旧方法 (默认查 status=1)
    default List<Product> findByActiveTrue() {
        return findByActiveTrueAndStatus(1);
    }
    
    default Optional<Product> findByIdAndActiveTrue(Long id) {
        return findByIdAndActiveTrueAndStatus(id, 1);
    }
    
    default List<Product> findByActiveTrueAndIsNewTrueOrderBySalesDesc() {
        return findByActiveTrueAndStatusAndIsNewTrueOrderBySalesDesc(1);
    }
    
    default List<Product> findByActiveTrueAndIsHotTrueOrderBySalesDesc() {
        return findByActiveTrueAndStatusAndIsHotTrueOrderBySalesDesc(1);
    }
    
    default Page<Product> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable) {
        return findByActiveTrueAndStatusAndNameContainingIgnoreCase(1, name, pageable);
    }
}
