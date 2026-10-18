package pt.upt.studymatch.studymatchbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"estudante_id", "course_unit_id", "academic_year"})
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudante_id", nullable = false)
    @JsonBackReference
    private Estudante estudante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_unit_id", nullable = false)
    private CourseUnit courseUnit;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(nullable = false)
    private Double grade;

    public Enrollment() {}

    public Enrollment(Estudante estudante, CourseUnit courseUnit, String academicYear, Double grade) {
        setEstudante(estudante);
        setCourseUnit(courseUnit);
        setAcademicYear(academicYear);
        setGrade(grade);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Estudante getEstudante() { return estudante; }
    public void setEstudante(Estudante estudante) { this.estudante = estudante; }

    public CourseUnit getCourseUnit() { return courseUnit; }
    public void setCourseUnit(CourseUnit courseUnit) { this.courseUnit = courseUnit; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Double getGrade() { return grade; }
    public void setGrade(Double grade) {
        if (grade != null && (grade < 0.0 || grade > 20.0)) {
            throw new IllegalArgumentException("A classificacao deve estar entre 0 e 20 valores.");
        }
        this.grade = grade;
    }
}