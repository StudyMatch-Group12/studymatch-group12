package pt.upt.studymatch.studymatchbackend.dto;

public class StatusResponseDTO {

    private String status;
    private String message;

    public StatusResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
