package pt.upt.studymatch.studymatchbackend.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UtilizadorJsonTest {
    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void studentResponsesDoNotExposePassword() {
        Estudante student = new Estudante(
                "Ana", "ana@upt.pt", "secret123", "50001", "EI");

        String json = mapper.writeValueAsString(student);

        assertFalse(json.contains("password"));
        assertFalse(json.contains("secret123"));
    }

    @Test
    void registrationRequestsCanStillProvidePassword() {
        String json = """
                {
                  "nome": "Ana",
                  "email": "ana@upt.pt",
                  "password": "secret123",
                  "numeroEstudante": "50001",
                  "curso": "EI"
                }
                """;

        Estudante student = mapper.readValue(json, Estudante.class);

        assertEquals("secret123", student.getPassword());
    }
}
