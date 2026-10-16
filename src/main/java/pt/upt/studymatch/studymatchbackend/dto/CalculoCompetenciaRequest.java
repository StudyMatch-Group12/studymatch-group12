package pt.upt.studymatch.studymatchbackend.dto;

import java.util.List;

public class CalculoCompetenciaRequest {

    private List<Double> notas;
    private List<Double> pesos;

    public CalculoCompetenciaRequest() {
    }

    public List<Double> getNotas() {
        return notas;
    }

    public void setNotas(List<Double> notas) {
        this.notas = notas;
    }

    public List<Double> getPesos() {
        return pesos;
    }

    public void setPesos(List<Double> pesos) {
        this.pesos = pesos;
    }
}