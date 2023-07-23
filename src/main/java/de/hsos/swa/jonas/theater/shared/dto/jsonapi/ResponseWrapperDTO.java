package de.hsos.swa.jonas.theater.shared.dto.jsonapi;

import java.util.List;

/** The ResponseWrapperDTO is used to wrap the response of the REST-API to accomodate the JSON-API specification.
 * @param <T> Data Type of the Response contains either a single object or a list of objects
 *
 */
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
