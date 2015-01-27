package com.north.joseph.impromp2.fragments;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.items.Event;
import com.north.joseph.impromp2.views.ViewHolder;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import org.json.JSONException;

import java.text.ParseException;

/**
 * Created by Joe on 11/23/2014.
 */
public class EventListAdapter extends ParseQueryAdapter<Event> {
    private int mHeight;

    public EventListAdapter(Context context, final EventSearchFragment eventSearchFragment) {
        super(context, new ParseQueryAdapter.QueryFactory<Event>() {
            public ParseQuery<Event> create() {
                return eventSearchFragment.getParseQuery();
            }
        });

        setObjectsPerPage(20);
    }

    @Override
    public View getItemView(Event event, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.event_view_children, null);
        }

        super.getItemView(event, convertView, parent);

        ViewHolder holder = (ViewHolder) convertView.getTag();

        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

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

        mHeight = convertView.getHeight() >> 1;

        return convertView;
    }

    @Override
    public View getNextPageView(View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.get_next_page, null);
            v.setMinimumHeight(mHeight);
        }

        super.getNextPageView(v, parent);

        return v;
    }
}
