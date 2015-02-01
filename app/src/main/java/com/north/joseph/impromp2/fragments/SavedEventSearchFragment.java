package com.north.joseph.impromp2.fragments;

import com.north.joseph.impromp2.items.Event;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Created by Joe on 1/27/2015.
 */
public class SavedEventSearchFragment extends EventSearchFragment {
    @Override
    public ParseQuery<Event> getParseQuery() {
        ParseUser user = ParseUser.getCurrentUser();

        ParseQuery<Event> eventQuery;

        if (user != null) {
            eventQuery = super.getParseQuery();

            ParseRelation<Event> eventRelation = user.getRelation("events");

            ParseQuery<Event> relationQuery = eventRelation.getQuery();

            eventQuery.whereMatchesKeyInQuery("objectId", "objectId", relationQuery);
        } else {
            eventQuery = ParseQuery.getQuery(Event.class);
            eventQuery.whereEqualTo("objectId", null);
        }

        return eventQuery;
    }
}
