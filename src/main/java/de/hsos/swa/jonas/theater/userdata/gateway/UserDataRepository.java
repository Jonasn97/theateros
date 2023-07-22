package de.hsos.swa.jonas.theater.userdata.gateway;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUpdateUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.entity.PerformanceState;
import de.hsos.swa.jonas.theater.userdata.entity.UserDataCatalog;
import de.hsos.swa.jonas.theater.userdata.entity.UserEvent;
import de.hsos.swa.jonas.theater.userdata.entity.Userdata;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(Transactional.TxType.MANDATORY)
@ApplicationScoped
public class UserDataRepository implements UserDataCatalog {
    @Override
    public Map<Long, EventState> getEventState(String username, Set<Long> eventIds) {

        HashMap<Long, EventState> eventStates = new HashMap<>();
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return eventStates;
        user.get().userEvents.stream().filter(userEvent -> eventIds.contains(userEvent.eventId)).forEach(userEvent -> eventStates.put(userEvent.eventId, userEvent.eventState));
        return eventStates;
    }

    @Override
    public Optional<EventState> getEventState(String username, long eventId) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        return user.get().userEvents.stream().filter(userEvent -> userEvent.eventId == eventId).findFirst().map(userEvent -> userEvent.eventState);
    }

    @Override
    public Map<Long, PerformanceState> getPerformanceState(String username, Set<Long> performanceIds) {
        return null;
    }

    @Override
    public Optional<PerformanceState> getPerformanceState(String username, Long performanceId) {
        Userdata user = Userdata.find("username", username).firstResult();
        return user.userPerformances.stream().filter(userPerformance -> userPerformance.performanceId == performanceId).findFirst().map(userPerformance -> userPerformance.performanceState);
    }

    @Override
    public Collection<UserEvent> getUserEventsForUser(UserParametersDTO userParametersDTO) {
        Optional<Userdata> user = Userdata.find("username", userParametersDTO.username).firstResultOptional();
        if(user.isEmpty()) return Collections.emptyList();
        return user.get().userEvents
                .stream()
                .skip(userParametersDTO.pageNumber * userParametersDTO.pageSize)
                .limit(userParametersDTO.pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public long getUserEventsForUserCount(UserParametersDTO userParametersDTO) {
        Optional<Userdata> user = Userdata.find("username", userParametersDTO.username).firstResultOptional();
        if(user.isEmpty()) return 0;
        return user.get().userEvents
                .size();
    }

    @Override
    public Optional<UserEvent> getUserEventByIdForUser(long id, String username) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        return user.get().userEvents.stream().filter(userEvent -> userEvent.id == id).findFirst();
    }

    @Override
    public Optional<UserEvent> createUserEvent(String username, IncomingUserEventDTO incomingUserEventDTO) {
Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        UserEvent userEvent = new UserEvent();
        userEvent.eventId = incomingUserEventDTO.eventId;
        userEvent.eventState = incomingUserEventDTO.eventState;
        userEvent.isFavorite = incomingUserEventDTO.isFavorite;
        user.get().userEvents.add(userEvent);
        userEvent.persist();
        user.get().persist();
        return Optional.of(userEvent);
    }

    @Override
    public Optional<UserEvent> updateUserEvent(String username, long userEventId, IncomingUpdateUserEventDTO incomingUserEventDTO) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.id == userEventId).findFirst();
        if(userEvent.isEmpty()) return Optional.empty();
        userEvent.get().eventState = incomingUserEventDTO.eventState;
        userEvent.get().isFavorite = incomingUserEventDTO.isFavorite;
        userEvent.get().persist();
        user.get().persist();
        return Optional.of(userEvent.get());
    }

    @Override
    public boolean deleteUserEvent(String username, long userEventId) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return false;
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.id == userEventId).findFirst();
        if(userEvent.isEmpty()) return false;
        user.get().userEvents.remove(userEvent.get());
        userEvent.get().persist();
        user.get().persist();
        return true;
    }

    @Override
    public Optional<UserEvent> patchUserEvent(String username, long userEventId, EventState eventState) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.id == userEventId).findFirst();
        if(userEvent.isEmpty()) return Optional.empty();
        userEvent.get().eventState = eventState;
        userEvent.get().persist();
        user.get().persist();
        return Optional.of(userEvent.get());
    }

    @Override
    public Optional<UserEvent> patchUserEvent(String username, long userEventId, Boolean isFavorite) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.id == userEventId).findFirst();
        if(userEvent.isEmpty()) return Optional.empty();
        userEvent.get().isFavorite = isFavorite;
        userEvent.get().persist();
        user.get().persist();
        return Optional.of(userEvent.get());
    }

    @Override
    public EventState updateEventStatebyEventIdOfUser(long eventId, EventState eventState, String username) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return null;
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.eventId == eventId).findFirst();
        if(userEvent.isEmpty()) {
            UserEvent newUserEvent = new UserEvent();
            newUserEvent.eventId = eventId;
            newUserEvent.eventState = eventState;
            newUserEvent.isFavorite = false;
            user.get().userEvents.add(newUserEvent);
            newUserEvent.persist();
            user.get().persist();
            return eventState;
        }
        userEvent.get().eventState = eventState;
        userEvent.get().persist();
        user.get().persist();
        return eventState;
    }

    @Override
    public void updateIsFavoritebyEventIdOfUser(long eventId, boolean isFavorite, String username) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return;
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.eventId == eventId).findFirst();
        if(userEvent.isEmpty()) {
            UserEvent newUserEvent = new UserEvent();
            newUserEvent.eventId = eventId;
            newUserEvent.eventState = EventState.NONE;
            newUserEvent.isFavorite = isFavorite;
            user.get().userEvents.add(newUserEvent);
            newUserEvent.persist();
            user.get().persist();
            return;
        }
        userEvent.get().isFavorite = isFavorite;
        userEvent.get().persist();
        user.get().persist();
    }

    @Override
    public boolean isFavorite(String username, Long id) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return false;
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.eventId == id).findFirst();
        if(userEvent.isEmpty()) return false;
        return userEvent.get().isFavorite;
    }
}
