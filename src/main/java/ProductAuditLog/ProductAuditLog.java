package ProductAuditLog;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_audit_log")
public class ProductAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }

    public enum ActionType {
        CREATE, UPDATE, DELETE
    }

    // Constructors
    public ProductAuditLog() {}

    public ProductAuditLog(Long productId, Long userId, ActionType actionType,
                           String fieldName, String oldValue, String newValue) {
        this.productId = productId;
        this.userId = userId;
        this.actionType = actionType;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
