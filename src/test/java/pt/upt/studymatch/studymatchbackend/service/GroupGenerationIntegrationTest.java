package pt.upt.studymatch.studymatchbackend.service;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.upt.studymatch.studymatchbackend.model.*;
import pt.upt.studymatch.studymatchbackend.repository.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:groups;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false"
})
@Transactional
class GroupGenerationIntegrationTest {
    @Autowired EstudanteRepository students;
    @Autowired GroupingContextRepository contexts;
    @Autowired GroupGenerationService service;

    @Test
    void generatesPersistsAndRetrievesGroups() {
        Set<Estudante> eligible = new LinkedHashSet<>();
        for (int i = 1; i <= 8; i++) {
            eligible.add(students.save(new Estudante(
                "Aluno " + i,
                "aluno" + i + "@upt.pt",
                "pass",
                "G" + i,
                "EI"
            )));
        }

        GroupingContext context = contexts.save(
            new GroupingContext("Projeto QS", eligible)
        );

        var first = service.generate(context.getId());
        assertEquals(2, first.groups().size());
        assertEquals(8, first.groups().stream()
            .mapToInt(group -> group.members().size())
            .sum());

        var retrieved = service.get(first.id());
        assertEquals(first.id(), retrieved.id());
        assertEquals(2, retrieved.groups().size());

        var second = service.generate(context.getId());
        assertNotEquals(first.id(), second.id());
        assertEquals(2, service.list(context.getId()).size());
    }

    @Test
    void invalidConfigurationDoesNotCreateGeneration() {
        Set<Estudante> eligible = new LinkedHashSet<>();
        for (int i = 1; i <= 6; i++) {
            eligible.add(students.save(new Estudante(
                "Inválido " + i,
                "invalido" + i + "@upt.pt",
                "pass",
                "X" + i,
                "EI"
            )));
        }

        GroupingContext context = contexts.save(
            new GroupingContext("Impossível", eligible)
        );

        assertThrows(
            org.springframework.web.server.ResponseStatusException.class,
            () -> service.generate(context.getId())
        );
        assertTrue(service.list(context.getId()).isEmpty());
    }
}
