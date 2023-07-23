package de.hsos.swa.jonas.theater.userdata.control;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUpdateUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserPerformanceDTO;
import de.hsos.swa.jonas.theater.userdata.entity.UserDataCatalog;
import de.hsos.swa.jonas.theater.userdata.entity.UserEvent;
import de.hsos.swa.jonas.theater.userdata.entity.UserPerformance;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
@ApplicationScoped
public class UserDataService implements UserDataOperations {
    @Inject
    UserDataCatalog userDataCatalog;

    @Override
    public Map<Long, EventState> getEventStatesByEventIdsForUser(String username, Set<Long> eventIds) {
        return null;
    }

    @Override
    public Collection<UserEvent> getUserEventsForUser(UserParametersDTO userParametersDTO) {
        return userDataCatalog.getUserEventsForUser(userParametersDTO);
    }

    @Override
    public long getUserEventsForUserCount(UserParametersDTO userParametersDTO) {
        return userDataCatalog.getUserEventsForUserCount(userParametersDTO);
    }

    @Override
    public Optional<UserEvent> getUserEventByIdForUser(long id, String username) {
        return userDataCatalog.getUserEventByIdForUser(id, username);
    }

    @Override
    public Optional<UserEvent> createUserEvent(String username, IncomingUserEventDTO incomingUserEventDTO) {
        return userDataCatalog.createUserEvent(username, incomingUserEventDTO);
    }

    @Override
    public Optional<UserEvent> updateUserEvent(String username, long userEventId, IncomingUpdateUserEventDTO incomingUserEventDTO) {
        return userDataCatalog.updateUserEvent(username, userEventId, incomingUserEventDTO);
    }

    @Override
    public boolean deleteUserEvent(String username, long userEventId) {
        return userDataCatalog.deleteUserEvent(username, userEventId);
    }

    @Override
    public Optional<UserEvent> patchUserEvent(String username, long userEventId, Boolean isFavorite) {
        return userDataCatalog.patchUserEvent(username, userEventId, isFavorite);
    }

    @Override
    public Optional<UserEvent> patchUserEvent(String username, long userEventId, EventState eventState) {
        return userDataCatalog.patchUserEvent(username, userEventId, eventState);
    }

    @Override
    public EventState updateEventStatebyEventIdOfUser(long eventId, EventState eventState, String username) {
        return userDataCatalog.updateEventStatebyEventIdOfUser(eventId, eventState, username);
    }

    @Override
    public void updateIsFavoritebyEventIdOfUser(long eventId, boolean isFavorite, String username) {
        userDataCatalog.updateIsFavoritebyEventIdOfUser(eventId, isFavorite, username);
    }

    @Override
    public Collection<UserPerformance> getUserPerformancesForUser(UserParametersDTO userParametersDTO) {
        return userDataCatalog.getUserPerformancesForUser(userParametersDTO);
    }

    @Override
    public long getUserPerformancesForUserCount(UserParametersDTO userParametersDTO) {
        return userDataCatalog.getUserPerformancesForUserCount(userParametersDTO);
    }

    @Override
    public Optional<UserPerformance> createUserPerformance(String username, IncomingUserPerformanceDTO incomingUserPerformanceDTO) {
        return userDataCatalog.createUserPerformance(username, incomingUserPerformanceDTO);
    }
}
