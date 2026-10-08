package pt.upt.studymatch.studymatchbackend.service;

import org.springframework.stereotype.Service;
import pt.upt.studymatch.studymatchbackend.dto.StatusResponseDTO;

@Service
public class StatusService {

    public StatusResponseDTO getStatus() {
        return new StatusResponseDTO(
            "OK",
            "StudyMatch backend is running"
        );
    }
}