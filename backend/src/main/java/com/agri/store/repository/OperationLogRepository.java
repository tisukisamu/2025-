package com.agri.store.repository;

import com.agri.store.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    List<OperationLog> findByUserId(Long userId);
    List<OperationLog> findByModule(String module);
    List<OperationLog> findAllByOrderByCreateTimeDesc();
}
