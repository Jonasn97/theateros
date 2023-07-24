package de.hsos.swa.jonas.theater;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingEventDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api.EventResourceApi;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResponseWrapperDTO;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

@QuarkusTest
public class EventResourceApiTest {


    @Test
    public void testGetEvents() {
        // Act
        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .get("http://localhost:8080/api/events");

        // Assert
        response.then().statusCode(200);

        ResponseWrapperDTO<Object> responseWrapperDTO = response.as(ResponseWrapperDTO.class);
        List<OutgoingEventDTOApi> events = (List<OutgoingEventDTOApi>) responseWrapperDTO.data;
        assertAll(
                () -> assertThat("Response should be 200", response.getStatusCode() == 200),
                () -> assertThat("Response data should not be null", responseWrapperDTO.data, is(notNullValue())),
                () -> assertThat("Response data should be a list of events", events, is(notNullValue())),
                () -> assertThat("Response should contain 10 events", events.size(), is(10))
        );
    }


}
