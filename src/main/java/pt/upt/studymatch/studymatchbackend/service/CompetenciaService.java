package pt.upt.studymatch.studymatchbackend.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetenciaService {

    public double calcularMediaPonderada(List<Double> notas, List<Double> pesos) {

        if (notas == null || pesos == null || notas.isEmpty()) {
            throw new IllegalArgumentException("Notas e pesos não podem estar vazios.");
        }

        if (notas.size() != pesos.size()) {
            throw new IllegalArgumentException(
                    "O número de notas deve ser igual ao número de pesos."
            );
        }

        double somaPesos = pesos.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (Math.abs(somaPesos - 100.0) > 0.01) {
            throw new IllegalArgumentException(
                    "A soma dos pesos deve ser igual a 100%."
            );
        }

        double resultado = 0.0;

        for (int i = 0; i < notas.size(); i++) {
            double nota = notas.get(i);
            double peso = pesos.get(i);

            if (nota < 0 || nota > 20) {
                throw new IllegalArgumentException(
                        "As notas devem estar entre 0 e 20."
                );
            }

            if (peso < 0) {
                throw new IllegalArgumentException(
                        "Os pesos não podem ser negativos."
                );
            }

            resultado += nota * (peso / 100.0);
        }

        return resultado;
    }

    public int converterParaNivel(double mediaPonderada) {

        if (mediaPonderada < 0 || mediaPonderada > 20) {
            throw new IllegalArgumentException(
                    "A média ponderada deve estar entre 0 e 20."
            );
        }

        if (mediaPonderada < 10) return 1;
        if (mediaPonderada < 13) return 2;
        if (mediaPonderada < 16) return 3;
        if (mediaPonderada < 18) return 4;

        return 5;
    }

    public int calcularNivelCompetencia(List<Double> notas, List<Double> pesos) {
        double media = calcularMediaPonderada(notas, pesos);
        return converterParaNivel(media);
    }
}