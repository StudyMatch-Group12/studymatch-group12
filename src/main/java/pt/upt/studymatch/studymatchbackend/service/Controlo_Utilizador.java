package pt.upt.studymatch.studymatchbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.upt.studymatch.studymatchbackend.model.Estudante;
import pt.upt.studymatch.studymatchbackend.repository.EstudanteRepository;

import java.util.List;
import java.util.Optional;


@Service
public class Controlo_Utilizador {

    private final EstudanteRepository estudanteRepository;

    @Autowired
    public Controlo_Utilizador(EstudanteRepository estudanteRepository) {
        this.estudanteRepository = estudanteRepository;
    }

    @Transactional
    public Estudante registarEstudante(Estudante estudante) {
        if (estudante == null) {
            throw new IllegalArgumentException("Os dados do estudante não podem ser nulos.");
        }
        if (estudante.getNome() == null || estudante.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do estudante é obrigatório.");
        }
        if (estudante.getNumeroEstudante() == null || estudante.getNumeroEstudante().trim().isEmpty()) {
            throw new IllegalArgumentException("O número de estudante é obrigatório.");
        }
        if (estudante.getEmail() == null || estudante.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        if (estudante.getCurso() == null || estudante.getCurso().trim().isEmpty()) {
            throw new IllegalArgumentException("O curso é obrigatório.");
        }

        String email = estudante.getEmail().trim().toLowerCase();
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("O e-mail introduzido é inválido.");
        }
        if (!email.endsWith("@upt.pt")) {
             throw new IllegalArgumentException("O e-mail deve pertencer ao domínio institucional da UPT.");
        }

        if (estudanteRepository.findByNumeroEstudante(estudante.getNumeroEstudante().trim()).isPresent()) {
            throw new IllegalArgumentException("Já existe um estudante registado com o número: " + estudante.getNumeroEstudante());
        }

        if (estudanteRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Já existe um utilizador registado com o e-mail: " + email);
        }

        estudante.setEmail(email);
        estudante.setNumeroEstudante(estudante.getNumeroEstudante().trim());
        estudante.setNome(estudante.getNome().trim());

        // Se a password não tiver sido definida no registo, define uma password padrão provisória (confirmar com a professora)
        if (estudante.getPassword() == null || estudante.getPassword().isEmpty()) {
            estudante.setPassword("TroqueAPassword");
        }

        // Persistência na BD
        return estudanteRepository.save(estudante);
    }


    @Transactional(readOnly = true)
    public List<Estudante> obterTodosEstudantes() {
        return estudanteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Estudante> obterEstudantePorId(Long id) {
        return estudanteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Estudante> obterEstudantePorNumero(String numeroEstudante) {
        return estudanteRepository.findByNumeroEstudante(numeroEstudante);
    }
}