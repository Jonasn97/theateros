package de.hsos.swa.jonas.theater.userdata.gateway;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.shared.PerformanceState;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.IncomingUpdateUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.IncomingUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.IncomingUserPerformanceDTO;
import de.hsos.swa.jonas.theater.userdata.entity.*;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements all the necessary methods for userdata
 */
@Transactional(Transactional.TxType.MANDATORY)
@ApplicationScoped
public class UserDataRepository implements UserDataCatalog {
    /** Returns a map of all event states for a given user for the mobile/event endpoint
     * @param username username of the user
     * @param eventIds ids of the events
     * @return Map<Long, EventState>
     */
    @Override
    public Map<Long, EventState> getEventState(String username, Set<Long> eventIds) {

        HashMap<Long, EventState> eventStates = new HashMap<>();
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return eventStates;
        user.get().userEvents.stream().filter(userEvent -> eventIds.contains(userEvent.getEventId())).forEach(userEvent -> eventStates.put(userEvent.getEventId(), userEvent.getEventState()));
        return eventStates;
    }

    /** Return the event state for a given user for the mobile/event/{id} endpoint
     * @param username username of the user
     * @param eventId id of the event
     * @return Optional<EventState>
     */
    @Override
    public Optional<EventState> getEventState(String username, long eventId) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        return user.get().userEvents.stream().filter(userEvent -> userEvent.getEventId() == eventId).findFirst().map(UserEvent::getEventState);
    }

    /** Returns a map of all performance states for a given user for the mobile/performance endpoint
     * @param username username of the user
     * @param performanceIds ids of the performances
     * @return Map<Long, PerformanceState>
     */
    @Override
    public Map<Long, PerformanceState> getPerformanceState(String username, Set<Long> performanceIds) {
        HashMap<Long, PerformanceState> performanceStates = new HashMap<>();
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return performanceStates;
        user.get().userPerformances.stream().filter(userPerformance -> performanceIds.contains(userPerformance.getPerformanceId())).forEach(userPerformance -> performanceStates.put(userPerformance.getPerformanceId(), userPerformance.getPerformanceState()));
        return performanceStates;
    }

    /** Returns the performance state for a given user
     * @param username username of the user
     * @param performanceId id of the performance
     * @return Optional<PerformanceState>
     */
    @Override
    public Optional<PerformanceState> getPerformanceState(String username, long performanceId) {
        Userdata user = Userdata.find("username", username).firstResult();
        return user.userPerformances.stream().filter(userPerformance -> userPerformance.getPerformanceId() == performanceId).findFirst().map(UserPerformance::getPerformanceState);
    }

    /** Returns a collection of UserEvents for a given user For UserEventsResourceAPI
     * @param userParametersDTO userParametersDTO
     * @return Collection<UserEvent>
     */
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

    /**
     * @param userParametersDTO userParametersDTO
     * @return long size of UserEvents for pagination
     */
    @Override
    public long getUserEventsForUserCount(UserParametersDTO userParametersDTO) {
        Optional<Userdata> user = Userdata.find("username", userParametersDTO.username).firstResultOptional();
        if(user.isEmpty()) return 0;
        return user.get().userEvents
                .size();
    }

    /**
     * @param id id of the UserEvent
     * @param username username of the user
     * @return Optional<UserEvent> for a given user
     */
    @Override
    public Optional<UserEvent> getUserEventByIdForUser(long id, String username) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        return user.get().userEvents.stream().filter(userEvent -> userEvent.id == id).findFirst();
    }

    /** Creates userEvent for a given user
     * @param username username of the user
     * @param incomingUserEventDTO incomingUserEventDTO
     * @return Optional<UserEvent> returns the created UserEvent
     */
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

    /**
     * @param username username of the user
     * @param userEventId id of the UserEvent
     * @param incomingUserEventDTO incomingUserEventDTO
     * @return Optional<UserEvent> for a given user after updating it
     */
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

    /**
     * @param username username of the user
     * @param userEventId id of the UserEvent
     * @return boolean if the deletion was successful
     */
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

    /**
     * @param username username of the user
     * @param userEventId id of the UserEvent
     * @param eventState eventState
     * @return Optional<UserEvent> for a given user after patching it with eventState
     */
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

    /**
     * @param username username of the user
     * @param userEventId id of the UserEvent
     * @param isFavorite isFavorite
     * @return Optional<UserEvent> for a given user after patching it with isFavorite
     */
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

    /** updates EventState for a given user
     * @param eventId id of the event
     * @param eventState eventState
     * @param username username of the user
     * @return EventState for a given user after updating it
     */
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

    /** updates isFavorite for a given user
     * @param eventId id of the event
     * @param isFavorite isFavorite
     * @param username username of the user
     */
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

    /**
     * @param username username of the user
     * @param id id of the event
     * @return boolean if the event is favorite for a given user
     */
    @Override
    public boolean isFavorite(String username, long id) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return false;
        Optional<UserEvent> userEvent = user.get().userEvents.stream().filter(userEvent1 -> userEvent1.getEventId() == id).findFirst();
        if(userEvent.isEmpty()) return false;
        return userEvent.get().isFavorite();
    }

    /**
     * @param userParametersDTO userParametersDTO
     * @return Collection<UserPerformance> for a given user with pagination
     */
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

    /**
     * @param userParametersDTO userParametersDTO
     * @return long count of UserPerformances for a given user for pagination
     */
    @Override
    public long getUserPerformancesForUserCount(UserParametersDTO userParametersDTO) {
        Optional<Userdata> user = Userdata.find("username", userParametersDTO.username).firstResultOptional();
        if(user.isEmpty()) return 0;
        return user.get().userPerformances.size();
    }

    /** creates UserPerformance for a given user
     * @param username username of the user
     * @param incomingUserPerformanceDTO incomingUserPerformanceDTO
     * @return Optional<UserPerformance> for a given user after creating it
     */
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

    /**
     * @param userPerformanceId id of the UserPerformance
     * @param username  username of the user
     * @return Optional<UserPerformance> for a given user
     */
    @Override
    public Optional<UserPerformance> getUserPerformanceByIdForUser(long userPerformanceId, String username) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Optional.empty();
        return user.get().userPerformances.stream().filter(userPerformance -> userPerformance.id == userPerformanceId).findFirst();
    }

    /**
     * @param username  username of the user
     * @param userEventId id of the UserEvent
     * @return boolean if the UserPerformance was deleted for a given user
     */
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

    /** updates UserPerformance for a given user
     * @param username username of the user
     * @param userPerformanceId id of the UserPerformance
     * @param incomingUserPerformanceDTO incomingUserPerformanceDTO
     * @return Optional<UserPerformance> for a given user after updating it
     */
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

    /**
     * @param username username of the user
     * @param eventIds set of eventIds
     * @return Map<Long, EventState> for a given user and a set of eventIds
     */
    @Override
    public Map<Long, EventState> getEventStatesByEventIdsForUser(String username, Set<Long> eventIds) {
        Optional<Userdata> user = Userdata.find("username", username).firstResultOptional();
        if(user.isEmpty()) return Collections.emptyMap();
        return user.get().userEvents.stream().filter(userEvent -> eventIds.contains(userEvent.getEventId())).collect(Collectors.toMap(UserEvent::getEventId, UserEvent::getEventState));
    }
}
