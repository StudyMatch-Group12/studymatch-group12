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

    @ManyToOne(optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    private Competencia competencia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "perfil_competencias_id", nullable = false)
    private PerfilCompetencias perfilCompetencias;

    public NivelCompetencia() {
    }

    public NivelCompetencia(
            int nivel,
            String origem,
            Competencia competencia,
            PerfilCompetencias perfilCompetencias) {

        this.nivel = nivel;
        this.origem = origem;
        this.competencia = competencia;
        this.perfilCompetencias = perfilCompetencias;
    }

    public Long getId() {
        return id;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Competencia competencia) {
        this.competencia = competencia;
    }

    public PerfilCompetencias getPerfilCompetencias() {
        return perfilCompetencias;
    }

    public void setPerfilCompetencias(PerfilCompetencias perfilCompetencias) {
        this.perfilCompetencias = perfilCompetencias;
    }
}