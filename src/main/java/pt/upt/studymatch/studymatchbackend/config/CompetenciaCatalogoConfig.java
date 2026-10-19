package pt.upt.studymatch.studymatchbackend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.upt.studymatch.studymatchbackend.model.Competencia;
import pt.upt.studymatch.studymatchbackend.repository.CompetenciaRepository;

@Configuration
public class CompetenciaCatalogoConfig {
    @Bean
    CommandLineRunner inicializarCompetencias(CompetenciaRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Competencia("Programação", "Desenvolvimento de software"));
                repository.save(new Competencia("Comunicação", "Partilha clara de ideias"));
                repository.save(new Competencia("Trabalho em equipa", "Colaboração em grupo"));
            }
        };
    }
}
