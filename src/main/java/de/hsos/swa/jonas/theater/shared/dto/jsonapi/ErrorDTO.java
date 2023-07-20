package de.hsos.swa.jonas.theater.shared.dto.jsonapi;

public class ErrorDTO {
    public String status;
    public String code;
    public String title;
    public String detail;

    public ErrorDTO(String status, String code, String title, String detail) {
        this.status = status;
        this.code = code;
        this.title = title;
        this.detail = detail;
    }
}
