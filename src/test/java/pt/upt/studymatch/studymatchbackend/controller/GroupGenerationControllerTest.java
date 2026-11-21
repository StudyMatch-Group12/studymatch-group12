package pt.upt.studymatch.studymatchbackend.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GenerateRequest;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GenerationResponse;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.GroupResponse;
import pt.upt.studymatch.studymatchbackend.dto.GroupGenerationDtos.StudentResponse;
import pt.upt.studymatch.studymatchbackend.service.GroupGenerationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupGenerationController.class)
class GroupGenerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupGenerationService service;

    @Test
    void generateReturnsCreatedGeneration() throws Exception {
        when(service.generate(eq(1L), eq(new GenerateRequest(2))))
                .thenReturn(sampleGeneration());

        mockMvc.perform(post("/api/grouping-contexts/1/generations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groupSize\": 2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.contextName").value("Projeto QS"))
                .andExpect(jsonPath("$.groups[0].number").value(1))
                .andExpect(jsonPath("$.groups[0].members[0].name").value("Ana"));
    }

    @Test
    void generateReturnsBadRequestForInvalidRequest() throws Exception {
        when(service.generate(eq(1L), any()))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Tamanho do grupo é obrigatório"));

        mockMvc.perform(post("/api/grouping-contexts/1/generations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generateReturnsNotFoundForUnknownContext() throws Exception {
        when(service.generate(eq(99L), any()))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Contexto não encontrado"));

        mockMvc.perform(post("/api/grouping-contexts/99/generations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groupSize\": 2}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listReturnsGenerationsOfContext() throws Exception {
        when(service.list(1L)).thenReturn(List.of(sampleGeneration()));

        mockMvc.perform(get("/api/grouping-contexts/1/generations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].groups.length()").value(1));
    }

    @Test
    void getReturnsSavedGeneration() throws Exception {
        when(service.get(1L, 5L)).thenReturn(sampleGeneration());

        mockMvc.perform(get("/api/grouping-contexts/1/generations/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contextId").value(1))
                .andExpect(jsonPath("$.generatedAt").exists());
    }

    @Test
    void getReturnsNotFoundForUnknownGeneration() throws Exception {
        when(service.get(1L, 404L))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Geração não encontrada"));

        mockMvc.perform(get("/api/grouping-contexts/1/generations/404"))
                .andExpect(status().isNotFound());
    }

    private GenerationResponse sampleGeneration() {
        return new GenerationResponse(
                5L,
                1L,
                "Projeto QS",
                LocalDateTime.of(2026, 11, 21, 14, 0),
                List.of(new GroupResponse(1, List.of(
                        new StudentResponse(1L, "Ana", "1001", "EI"),
                        new StudentResponse(2L, "Bruno", "1002", "EI"))))
        );
    }
}
