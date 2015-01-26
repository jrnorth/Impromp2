package com.north.joseph.impromp2.items;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Joe on 1/25/2015.
 */
@ParseClassName("EventCollection")
public class EventCollection extends ParseObject {
    public EventCollection() {}

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public List<Event> getEvents() {
        return getList("events");
    }

    public void setEvents(List<Event> events) {
        put("events", events);
    }
}
