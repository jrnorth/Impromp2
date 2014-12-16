package com.north.joseph.impromp2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.items.Event;

import org.json.JSONException;

import java.text.ParseException;

public class EventDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("event");

        TextView eventName = (TextView) findViewById(R.id.eventdetail_title);
        eventName.setText(event.getName());

        TextView eventDetails = (TextView) findViewById(R.id.eventdetail_details);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Start: ");
        try {
            stringBuilder.append(event.getFormattedStartTime());
        } catch (ParseException ex) {
            stringBuilder.append(event.getStartTime());
        }
        stringBuilder.append("\n");

        stringBuilder.append("End: ");
        try {
            stringBuilder.append(event.getFormattedEndTime());
        } catch (ParseException ex) {
            stringBuilder.append(event.getEndTime());
        }
        stringBuilder.append("\n");

        stringBuilder.append("Address: ");
        try {
            stringBuilder.append(event.getAddress1());
        } catch (JSONException ex) {
            stringBuilder.append("address 1");
        }
        stringBuilder.append("\n");

        try {
            if (!event.getAddress2().equals("null")) {
                stringBuilder.append("         ");
                try {
                    stringBuilder.append(event.getAddress2());
                } catch (JSONException ex) {
                    stringBuilder.append("address 2");
                }
                stringBuilder.append("\n");
            }
        } catch (JSONException ex) {
            // We'll just assume there's no address 2 if this exception is thrown.
        }

        stringBuilder.append("Venue: ");
        try {
            stringBuilder.append(event.getVenueName());
        } catch (JSONException ex) {
            stringBuilder.append("venue");
        }
        stringBuilder.append("\n");

        stringBuilder.append("Free: ");
        stringBuilder.append(event.isFree() ? "Yes" : "No");
        stringBuilder.append("\n");

        eventDetails.setText(stringBuilder.toString());

        TextView eventDescription = (TextView) findViewById(R.id.eventdetail_description);
        eventDescription.setText(event.getDescription());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
