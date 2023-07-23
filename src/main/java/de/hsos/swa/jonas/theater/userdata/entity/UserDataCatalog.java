package de.hsos.swa.jonas.theater.userdata.entity;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUpdateUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserPerformanceDTO;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserDataCatalog {
    Map<Long, EventState> getEventState(String username, Set<Long> eventIds);
    Optional<EventState> getEventState(String username, long eventId);

    Map<Long, PerformanceState> getPerformanceState(String username, Set<Long> performanceIds);

    Optional<PerformanceState> getPerformanceState(String username, long performanceId);

    Collection<UserEvent> getUserEventsForUser(UserParametersDTO userParametersDTO);

    long getUserEventsForUserCount(UserParametersDTO userParametersDTO);

    Optional<UserEvent> getUserEventByIdForUser(long id, String username);

    Optional<UserEvent> createUserEvent(String username, IncomingUserEventDTO incomingUserEventDTO);

    Optional<UserEvent> updateUserEvent(String username, long userEventId, IncomingUpdateUserEventDTO incomingUserEventDTO);

    boolean deleteUserEvent(String username, long userEventId);

    Optional<UserEvent> patchUserEvent(String username, long userEventId, EventState eventState);

    Optional<UserEvent> patchUserEvent(String username, long userEventId, Boolean isFavorite);

    EventState updateEventStatebyEventIdOfUser(long eventId, EventState eventState, String username);

    void updateIsFavoritebyEventIdOfUser(long eventId, boolean isFavorite, String username);

    boolean isFavorite(String username, long id);

    Collection<UserPerformance> getUserPerformancesForUser(UserParametersDTO userParametersDTO);

    long getUserPerformancesForUserCount(UserParametersDTO userParametersDTO);

    Optional<UserPerformance> createUserPerformance(String username, IncomingUserPerformanceDTO incomingUserPerformanceDTO);

    Optional<UserPerformance> getUserPerformanceByIdForUser(long userPerformanceId, String username);

    boolean deleteUserPerformance(String username, long userEventId);

    Optional<UserPerformance> updateUserPerformance(String username, long userPerformanceId, IncomingUserPerformanceDTO incomingUserPerformanceDTO);
}
