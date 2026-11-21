package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_generation")
public class GroupGeneration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id", nullable = false)
    private GroupingContext context;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    protected GroupGeneration() {}

    public GroupGeneration(GroupingContext context) {
        this.context = context;
        this.generatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public GroupingContext getContext() { return context; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
}