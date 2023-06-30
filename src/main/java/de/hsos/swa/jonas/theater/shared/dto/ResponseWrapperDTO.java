package de.hsos.swa.jonas.theater.shared.dto;

import java.util.List;

public class ResponseWrapperDTO<T> {
    public T data;
    public List<ErrorDTO> errors;
    public LinksDTO links;
    public List<T> included;

    public ResponseWrapperDTO(T data, List<ErrorDTO> errors, LinksDTO links, List<T> included) {
        this.data = data;
        this.errors = errors;
        this.links = links;
        this.included = included;
    }

    public ResponseWrapperDTO() {
    }
}
