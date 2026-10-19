package pt.upt.studymatch.studymatchbackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.upt.studymatch.studymatchbackend.dto.ColdStartRequest;
import pt.upt.studymatch.studymatchbackend.dto.EnrollmentRequestDTO;
import pt.upt.studymatch.studymatchbackend.model.*;
import pt.upt.studymatch.studymatchbackend.repository.*;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:coldstart;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@Transactional
class ColdStartIntegrationTest {
    @Autowired EstudanteRepository estudantes;
    @Autowired CompetenciaRepository competencias;
    @Autowired CourseUnitRepository unidades;
    @Autowired CompetenciaPesoRepository pesos;
    @Autowired ColdStartService coldStartService;
    @Autowired AcademicHistoryService academicHistoryService;
    @Autowired PerfilCompetenciasService perfilService;

    @Test
    void perfilProvisorioPersisteETransitaComNotaReal() {
        Estudante estudante = estudantes.save(new Estudante("Ana", "ana.cold@upt.pt", "pass", "COLD01", "EI"));
        Competencia competencia = competencias.save(new Competencia("Análise", "Raciocínio"));
        CourseUnit unidade = unidades.save(new CourseUnit("COLD-UC", "Análise de Dados", 1, 1, 6));
        pesos.save(new CompetenciaPeso(unidade, competencia, 100.0));
        ColdStartRequest request = new ColdStartRequest();
        request.setInteresses("Dados");
        request.setPreferenciaPapel("Análise");
        request.setNiveis(Map.of(competencia.getId(), 5));

        coldStartService.criarPerfil(estudante.getId(), request);
        var inicial = perfilService.consultarPerfil("COLD01");
        assertEquals(5, inicial.get(0).getNivel());
        assertEquals("AUTOAVALIACAO", inicial.get(0).getOrigem());

        EnrollmentRequestDTO nota = new EnrollmentRequestDTO();
        nota.setCourseUnitId(unidade.getId());
        nota.setAcademicYear("2026/27");
        nota.setGrade(10.0);
        academicHistoryService.registerEnrollment(estudante.getId(), nota);

        var atualizado = perfilService.consultarPerfil("COLD01");
        assertEquals(4, atualizado.get(0).getNivel());
        assertEquals("MISTO", atualizado.get(0).getOrigem());
    }
}
