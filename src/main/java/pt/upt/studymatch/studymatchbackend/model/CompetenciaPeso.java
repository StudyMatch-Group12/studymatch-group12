package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "competencia_pesos",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"course_unit_id", "competencia_id"}
                )
        }
)
public class CompetenciaPeso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "course_unit_id", nullable = false)
    private CourseUnit courseUnit;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    private Competencia competencia;

    @Column(nullable = false)
    private Double peso;

    public CompetenciaPeso() {
    }

    public CompetenciaPeso(
            CourseUnit courseUnit,
            Competencia competencia,
            Double peso) {

        this.courseUnit = courseUnit;
        this.competencia = competencia;
        setPeso(peso);
    }

    public Long getId() {
        return id;
    }

    public CourseUnit getCourseUnit() {
        return courseUnit;
    }

    public void setCourseUnit(CourseUnit courseUnit) {
        this.courseUnit = courseUnit;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Competencia competencia) {
        this.competencia = competencia;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        if (peso == null || peso <= 0 || peso > 100) {
            throw new IllegalArgumentException(
                    "O peso da competência deve estar entre 0 e 100."
            );
        }

        this.peso = peso;
    }
}