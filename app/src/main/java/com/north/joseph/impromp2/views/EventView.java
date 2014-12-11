package com.north.joseph.impromp2.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.items.Event;

import org.json.JSONException;

import java.text.ParseException;

/**
 * Created by Joe on 11/23/2014.
 */
public class EventView extends RelativeLayout {
    private ImageView mImageView;
    private TextView mNameTextView;
    private TextView mTimeTextView;
    private TextView mLocationTextView;

    public EventView(Context context) {
        this(context, null);
    }

    public EventView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.event_view_children, this, true);
        setupChildren();
    }

    public void setEvent(Event event) {
        final String eventName = event.getName();

        try {
            final String startTime = event.getFormattedStartTime();
            mTimeTextView.setText(startTime);
        } catch (ParseException e) {
            Log.d("ParseException", e.getMessage());
            mTimeTextView.setText(event.getStartTime());
        }

        try {
            final String venueName = event.getVenueName();
            final String city = event.getCity();
            mLocationTextView.setText(venueName + ", " + city);
        } catch (JSONException e) {
            mLocationTextView.setText("Unknown");
        }

        mNameTextView.setText(eventName);
    }

    private void setupChildren() {
        mImageView = (ImageView) findViewById(R.id.event_imageView);
        mNameTextView = (TextView) findViewById(R.id.event_nameTextView);
        mTimeTextView = (TextView) findViewById(R.id.event_timeTextView);
        mLocationTextView = (TextView) findViewById(R.id.event_locationTextView);
    }

    public static EventView inflate(ViewGroup parent) {
        EventView eventView = (EventView) LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view, parent, false);
        return eventView;
    }
}
