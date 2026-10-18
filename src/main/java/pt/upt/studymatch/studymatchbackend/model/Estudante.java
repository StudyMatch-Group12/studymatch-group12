package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudante")
public class Estudante extends Utilizador {

    @Column(name = "numero_estudante", nullable = false, unique = true)
    private String numeroEstudante;

    @Column(nullable = false)
    private String curso;

    @com.fasterxml.jackson.annotation.JsonManagedReference
    @jakarta.persistence.OneToMany(mappedBy = "estudante", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Enrollment> enrollments = new java.util.ArrayList<>();

    protected Estudante() {}

    public Estudante(String nome, String email, String password, String numeroEstudante, String curso) {
        super(nome, email, password); // Chama o construtor da superclasse Utilizador
        this.numeroEstudante = numeroEstudante;
        this.curso = curso;
    }

    public String getNumeroEstudante() { return numeroEstudante; }
    public void setNumeroEstudante(String numeroEstudante) { this.numeroEstudante = numeroEstudante; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public java.util.List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(java.util.List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setEstudante(this);
    }
}