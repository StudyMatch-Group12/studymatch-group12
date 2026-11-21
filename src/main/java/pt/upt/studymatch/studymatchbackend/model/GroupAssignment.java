package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "group_assignment",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"generation_id", "student_id"}
    )
)
public class GroupAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id", nullable = false)
    private GroupGeneration generation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Estudante student;

    @Column(nullable = false)
    private int groupNumber;

    protected GroupAssignment() {}

    public GroupAssignment(
            GroupGeneration generation,
            Estudante student,
            int groupNumber) {
        this.generation = generation;
        this.student = student;
        this.groupNumber = groupNumber;
    }

    public Estudante getStudent() { return student; }
    public int getGroupNumber() { return groupNumber; }
}