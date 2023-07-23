package de.hsos.swa.jonas.theater.userdata.gateway;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUpdateUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserPerformanceDTO;
import de.hsos.swa.jonas.theater.userdata.entity.*;
import io.quarkus.logging.Log;

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
        user.get().userEvents.stream().filter(userEvent -> eventIds.contains(userEvent.getEventId())).forEach(userEvent -> eventStates.put(userEvent.getEventId(), userEvent.getEventState()));
        return eventStates;
    }

    @Override
    public Optional<EventState> getEventState(String username, long eventId) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        return user.get().userEvents.stream().filter(userEvent -> userEvent.getEventId() == eventId).findFirst().map(UserEvent::getEventState);
    }

    @Override
    public Map<Long, PerformanceState> getPerformanceState(String username, Set<Long> performanceIds) {
        return null;
    }

    @Override
    public Optional<PerformanceState> getPerformanceState(String username, long performanceId) {
        Userdata user = Userdata.find("username", username).firstResult();
        return user.userPerformances.stream().filter(userPerformance -> userPerformance.getPerformanceId() == performanceId).findFirst().map(UserPerformance::getPerformanceState);
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
        userEvent.setEventId(incomingUserEventDTO.eventId);
        userEvent.setEventState(incomingUserEventDTO.eventState);
        userEvent.setFavorite(incomingUserEventDTO.isFavorite);
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
        userEvent.get().setEventState(incomingUserEventDTO.eventState);
        userEvent.get().setFavorite(incomingUserEventDTO.isFavorite);
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
        userEvent.get().setEventState(eventState);
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
        userEvent.get().setFavorite(isFavorite);
        userEvent.get().persist();
        user.get().persist();
        return Optional.of(userEvent.get());
    }

    @Override
    public EventState updateEventStatebyEventIdOfUser(long eventId, EventState eventState, String username) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return null;
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.getEventId() == eventId).findFirst();
        if(userEvent.isEmpty()) {
            UserEvent newUserEvent = new UserEvent();
            newUserEvent.setEventId(eventId);
            newUserEvent.setEventState(eventState);
            newUserEvent.setFavorite(false);
            user.get().userEvents.add(newUserEvent);
            newUserEvent.persist();
            user.get().persist();
            return eventState;
        }
        userEvent.get().setEventState(eventState);
        userEvent.get().persist();
        user.get().persist();
        return eventState;
    }

    @Override
    public void updateIsFavoritebyEventIdOfUser(long eventId, boolean isFavorite, String username) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return;
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.getEventId() == eventId).findFirst();
        if(userEvent.isEmpty()) {
            UserEvent newUserEvent = new UserEvent();
            newUserEvent.setEventId(eventId);
            newUserEvent.setEventState(EventState.NONE);
            newUserEvent.setFavorite(isFavorite);
            user.get().userEvents.add(newUserEvent);
            newUserEvent.persist();
            user.get().persist();
            return;
        }
        userEvent.get().setFavorite(isFavorite);
        userEvent.get().persist();
        user.get().persist();
    }

    @Override
    public boolean isFavorite(String username, long id) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return false;
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.getEventId() == id).findFirst();
        if(userEvent.isEmpty()) return false;
        return userEvent.get().isFavorite();
    }

    @Override
    public Collection<UserPerformance> getUserPerformancesForUser(UserParametersDTO userParametersDTO) {
        Optional<Userdata> user = Userdata.find("username", userParametersDTO.username).firstResultOptional();
        if(user.isEmpty()) return Collections.emptyList();
        return user.get().userPerformances
                .stream()
                .skip(userParametersDTO.pageNumber * userParametersDTO.pageSize)
                .limit(userParametersDTO.pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public long getUserPerformancesForUserCount(UserParametersDTO userParametersDTO) {
        Optional<Userdata> user = Userdata.find("username", userParametersDTO.username).firstResultOptional();
        if(user.isEmpty()) return 0;
        return user.get().userPerformances.size();
    }

    @Override
    public Optional<UserPerformance> createUserPerformance(String username, IncomingUserPerformanceDTO incomingUserPerformanceDTO) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        UserPerformance userPerformance = new UserPerformance();
        userPerformance.setPerformanceId(incomingUserPerformanceDTO.performanceId);
        userPerformance.setPerformanceState(incomingUserPerformanceDTO.performanceState);
        user.get().userPerformances.add(userPerformance);
        userPerformance.persist();
        user.get().persist();
        return Optional.of(userPerformance);
    }

    @Override
    public Optional<UserPerformance> getUserPerformanceByIdForUser(long userPerformanceId, String username) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        return user.get().userPerformances.stream().filter(userPerformance -> userPerformance.id == userPerformanceId).findFirst();
    }

    @Override
    public boolean deleteUserPerformance(String username, long userEventId) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return false;
        Optional<UserPerformance> userPerformance = user.get().userPerformances.stream().filter(userPerformance1 -> userPerformance1.id == userEventId).findFirst();
        if(userPerformance.isEmpty()) return false;
        user.get().userPerformances.remove(userPerformance.get());
        userPerformance.get().persist();
        user.get().persist();
        return true;


    }

    @Override
    public Optional<UserPerformance> updateUserPerformance(String username, long userPerformanceId, IncomingUserPerformanceDTO incomingUserPerformanceDTO) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        Optional<UserPerformance> userPerformance = user.get().userPerformances.stream().filter(userPerformance1 -> userPerformance1.id == userPerformanceId).findFirst();
        if(userPerformance.isEmpty()) return Optional.empty();
        userPerformance.get().setPerformanceState(incomingUserPerformanceDTO.performanceState);
        userPerformance.get().persist();
        user.get().persist();
        return Optional.of(userPerformance.get());
    }
}
