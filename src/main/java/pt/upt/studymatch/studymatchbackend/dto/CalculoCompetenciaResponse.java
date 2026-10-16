package pt.upt.studymatch.studymatchbackend.dto;

public class CalculoCompetenciaResponse {

    private double mediaPonderada;
    private int nivel;

    public CalculoCompetenciaResponse(double mediaPonderada, int nivel) {
        this.mediaPonderada = mediaPonderada;
        this.nivel = nivel;
    }

    public double getMediaPonderada() {
        return mediaPonderada;
    }

    public int getNivel() {
        return nivel;
    }
}