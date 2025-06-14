package ProductAuditLog;

import org.springframework.data.jpa.repository   .JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAuditLogRepository extends JpaRepository<ProductAuditLog, Long> {
    List<ProductAuditLog> findByProductIdOrderByChangedAtDesc(Long productId);
    List<ProductAuditLog> findByUserIdOrderByChangedAtDesc(Long userId);
    List<ProductAuditLog> findByProductIdAndUserIdOrderByChangedAtDesc(Long productId, Long userId);
}