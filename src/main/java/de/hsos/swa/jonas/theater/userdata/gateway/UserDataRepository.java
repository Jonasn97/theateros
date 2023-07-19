package de.hsos.swa.jonas.theater.userdata.gateway;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.entity.UserDataCatalog;
import de.hsos.swa.jonas.theater.userdata.entity.Userdata;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Transactional(Transactional.TxType.MANDATORY)
@ApplicationScoped
public class UserDataRepository implements UserDataCatalog {
    @Override
    public Map<Long, EventState> getEventState(String username, Set<Long> eventIds) {
       //Stream durch alle events vom user mit username
        //Stream durch alle events aus eventIds
        //wenn beide gleich sind, dann in hashmap
        HashMap<Long, EventState> eventStates = new HashMap<>();
        Userdata user = Userdata.find("username", username).firstResult();
        //Stream through all events from user, and return the eventStates of the events that are in the eventIds
        user.userEvents.stream().filter(userEvent -> eventIds.contains(userEvent.eventId)).forEach(userEvent -> eventStates.put(userEvent.eventId, userEvent.eventState));
        return eventStates;
    }

    @Override
    public Optional<EventState> getEventState(String username, long eventId) {
        Userdata user = Userdata.find("username", username).firstResult();
        //Stream through all events from user, and return the eventState of the event that has the eventId
        return user.userEvents.stream().filter(userEvent -> userEvent.eventId == eventId).findFirst().map(userEvent -> userEvent.eventState);
    }

}
