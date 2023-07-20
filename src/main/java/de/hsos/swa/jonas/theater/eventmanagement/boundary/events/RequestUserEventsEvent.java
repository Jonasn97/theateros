package de.hsos.swa.jonas.theater.eventmanagement.boundary.events;

import java.util.Set;

public class RequestUserEventsEvent {
    private String username;
    private Set<Long> eventIds;

    public RequestUserEventsEvent(String username, Set<Long> eventIds){
        this.username = username;
        this.eventIds = eventIds;
    }

    public RequestUserEventsEvent(){

    }

    public String getUsername(){
        return username;
    }

    public Set<Long> getEventIds(){
        return eventIds;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setEventIds(Set<Long> eventIds){
        this.eventIds = eventIds;
    }
}
