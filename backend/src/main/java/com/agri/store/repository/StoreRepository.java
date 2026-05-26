package com.agri.store.repository;

import com.agri.store.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    // Refreshing repository
    Optional<Store> findByUserId(Long userId);
    List<Store> findByStatus(Integer status);
    Page<Store> findByStatus(Integer status, Pageable pageable);
    boolean existsByUserId(Long userId);
    boolean existsByStoreName(String storeName);
}
