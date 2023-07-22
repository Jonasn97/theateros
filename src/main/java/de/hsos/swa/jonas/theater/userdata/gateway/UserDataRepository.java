package de.hsos.swa.jonas.theater.userdata.gateway;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
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

}
