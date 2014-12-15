package com.north.joseph.impromp2.fragments;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.items.Event;
import com.north.joseph.impromp2.views.ViewHolder;

import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

/**
 * Created by Joe on 11/23/2014.
 */
public class EventListAdapter extends ArrayAdapter<Event> {
    public EventListAdapter(Context c, List<Event> events) {
        super(c, R.layout.event_view_children, R.id.event_nameTextView, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        ViewHolder holder = (ViewHolder) row.getTag();

        if (holder == null) {
            holder = new ViewHolder(row);
            row.setTag(holder);
        }

        Event event = getItem(position);

        final String eventName = event.getName();

        try {
            final String startTime = event.getFormattedStartTime();
            holder.mTimeTextView.setText(startTime);
        } catch (ParseException e) {
            Log.d("ParseException", e.getMessage());
            holder.mTimeTextView.setText(event.getStartTime());
        }

        try {
            final String venueName = event.getVenueName();
            final String city = event.getCity();
            holder.mLocationTextView.setText(venueName + ", " + city);
        } catch (JSONException e) {
            holder.mLocationTextView.setText("Unknown");
        }

        holder.mNameTextView.setText(eventName);

        return row;
    }
}
