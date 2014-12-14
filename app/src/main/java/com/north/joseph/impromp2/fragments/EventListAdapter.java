package com.north.joseph.impromp2.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.north.joseph.impromp2.items.Event;
import com.north.joseph.impromp2.views.EventView;

import java.util.List;

/**
 * Created by Joe on 11/23/2014.
 */
public class EventListAdapter extends ArrayAdapter<Event> {
    public EventListAdapter(Context c, List<Event> events) {
        super(c, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventView eventView = (EventView) convertView;
        if (eventView == null)
            eventView = EventView.inflate(parent);

        eventView.setEvent(getItem(position));
        return eventView;
    }
}
