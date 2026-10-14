package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nivel_competencia")
public class NivelCompetencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int nivel;

    private String origem;

    public NivelCompetencia() {
    }

    public NivelCompetencia(int nivel, String origem) {
        this.nivel = nivel;
        this.origem = origem;
    }

    public Long getId() {
        return id;
    }

    public int getNivel() {
        return nivel;
    }

    public String getOrigem() {
        return origem;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }
}