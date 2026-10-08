package pt.upt.studymatch.studymatchbackend.dto;

public class StatusResponseDTO {

    private String status;
    private String message;

    public StatusResponseDTO() {
    }

    public StatusResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}