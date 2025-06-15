package warehouse_management.Audit;

import warehouse_management.ProductAuditLog.ProductAuditLog;
import warehouse_management.ProductAuditLog.ProductAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import warehouse_management.Product.Product;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private ProductAuditLogRepository auditLogRepository;

    public void logProductCreation(Product product, Long userId) {
        ProductAuditLog log = new ProductAuditLog(
                product.getId(),
                userId,
                ProductAuditLog.ActionType.CREATE,
                "PRODUCT_CREATED",
                null,
                "Product created: " + product.getName()
        );
        auditLogRepository.save(log);
    }

    public void logProductUpdate(Product oldProduct, Product newProduct, Long userId) {
        compareAndLog(oldProduct, newProduct, userId, "name");
        compareAndLog(oldProduct, newProduct, userId, "description");
        compareAndLog(oldProduct, newProduct, userId, "price");
        compareAndLog(oldProduct, newProduct, userId, "stockQuantity");
    }

    public void logProductDeletion(Product product, Long userId) {
        ProductAuditLog log = new ProductAuditLog(
                product.getId(),
                userId,
                ProductAuditLog.ActionType.DELETE,
                "PRODUCT_DELETED",
                "Product: " + product.getName(),
                null
        );
        auditLogRepository.save(log);
    }

    private void compareAndLog(Product oldProduct, Product newProduct, Long userId, String fieldName) {
        try {
            Field field = Product.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            Object oldValue = field.get(oldProduct);
            Object newValue = field.get(newProduct);

            if (!java.util.Objects.equals(oldValue, newValue)) {
                ProductAuditLog log = new ProductAuditLog(
                        newProduct.getId(),
                        userId,
                        ProductAuditLog.ActionType.UPDATE,
                        fieldName,
                        oldValue != null ? oldValue.toString() : null,
                        newValue != null ? newValue.toString() : null
                );
                auditLogRepository.save(log);
            }
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Error logging field change for " + fieldName + ": " + e.getMessage());
        }
    }

    public List<ProductAuditLog> getProductAuditHistory(Long productId) {
        return auditLogRepository.findByProductIdOrderByChangedAtDesc(productId);
    }

    public List<ProductAuditLog> getUserAuditHistory(Long userId) {
        return auditLogRepository.findByUserIdOrderByChangedAtDesc(userId);
    }
}
