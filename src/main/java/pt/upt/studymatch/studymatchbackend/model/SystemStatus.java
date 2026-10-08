package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_status")
public class SystemStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private String status;
    private LocalDateTime checkedAt;

    public SystemStatus() {}

    public SystemStatus(String serviceName, String status, LocalDateTime checkedAt) {
        this.serviceName = serviceName;
        this.status = status;
        this.checkedAt = checkedAt;
    }

    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }
}