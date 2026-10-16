package pt.upt.studymatch.studymatchbackend.dto;

public class CompetenciaPerfilResponse {

    private String nome;
    private String descricao;
    private int nivel;
    private String origem;

    public CompetenciaPerfilResponse(
            String nome,
            String descricao,
            int nivel,
            String origem) {

        this.nome = nome;
        this.descricao = descricao;
        this.nivel = nivel;
        this.origem = origem;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getNivel() {
        return nivel;
    }

    public String getOrigem() {
        return origem;
    }
}