package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "grouping_context")
public class GroupingContext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "grouping_context_student",
            joinColumns = @JoinColumn(name = "context_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"context_id", "student_id"}
            )
    )
    private Set<Estudante> students = new LinkedHashSet<>();

    protected GroupingContext() {}

    public GroupingContext(String name, Set<Estudante> students) {
        this.name = name;
        this.students.addAll(students);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Set<Estudante> getStudents() { return students; }
}