package de.hsos.swa.jonas.theater.userdata.boundary.dto;

public class UserParametersDTO {
    public long pageNumber;
    public long pageSize;

    public String username;

    public UserParametersDTO() {
    }

    public UserParametersDTO(long pageNumber, long pageSize, String username) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.username = username;
    }
}
