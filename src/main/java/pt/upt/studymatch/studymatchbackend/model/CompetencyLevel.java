package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.*;

// Proficiency level (0-100) of a student in one competency
@Entity
@Table(name = "competency_level",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "competency_id"}))
public class CompetencyLevel {

    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private Estudante student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competency_id")
    private Competency competency;

    @Column(nullable = false)
    private int level;

    // Where the level comes from, e.g. "SELF_ASSESSMENT" or "ACADEMIC_HISTORY"
    private String source;

    protected CompetencyLevel() {}

    public CompetencyLevel(Estudante student, Competency competency, int level, String source) {
        this.student = student;
        this.competency = competency;
        this.level = level;
        this.source = source;
    }

    public Long getId() { return id; }
    public Estudante getStudent() { return student; }
    public Competency getCompetency() { return competency; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
