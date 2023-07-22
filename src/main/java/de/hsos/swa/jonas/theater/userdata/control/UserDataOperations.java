package de.hsos.swa.jonas.theater.userdata.control;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUpdateUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.entity.UserEvent;

import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserDataOperations {
    Map<Long, EventState> getEventStatesByEventIdsForUser(String username, Set<Long> eventIds);
    Collection<UserEvent> getUserEventsForUser(UserParametersDTO userParametersDTO);


    long getUserEventsForUserCount(UserParametersDTO userParametersDTO);

    Optional<UserEvent> getUserEventByIdForUser(long id, String username);

    Optional<UserEvent> createUserEvent(String username, IncomingUserEventDTO incomingUserEventDTO);

    Optional<UserEvent> updateUserEvent(String username, long userEventId, IncomingUpdateUserEventDTO incomingUserEventDTO);

    boolean deleteUserEvent(String username, long userEventId);

    Optional<UserEvent> patchUserEvent(String username, @Positive long userEventId, Boolean isFavorite);

    Optional<UserEvent> patchUserEvent(String username, @Positive long userEventId, EventState eventState);

    EventState updateEventStatebyEventIdOfUser(long eventId, EventState eventState, String username);

    void updateIsFavoritebyEventIdOfUser(long eventId, boolean isFavorite, String username);
}
