package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "perfil_competencias")
public class PerfilCompetencias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataAtualizacao;

    @OneToOne(optional = false)
    @JoinColumn(
            name = "estudante_id",
            nullable = false,
            unique = true
    )
    private Estudante estudante;

    public PerfilCompetencias() {
    }

    public PerfilCompetencias(LocalDateTime dataAtualizacao, Estudante estudante) {
        this.dataAtualizacao = dataAtualizacao;
        this.estudante = estudante;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public void setEstudante(Estudante estudante) {
        this.estudante = estudante;
    }
}