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
}