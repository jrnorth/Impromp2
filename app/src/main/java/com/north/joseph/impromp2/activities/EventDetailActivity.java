package com.north.joseph.impromp2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.items.Event;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONException;

import java.text.ParseException;

public class EventDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent intent = getIntent();
        final Event event = intent.getParcelableExtra("event");

        final ScrollView scrollView = (ScrollView) findViewById(R.id.eventdetail_scrollView);
        final ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_UP:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;
                    default:
                        return true;
                }
            }
        });

        ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                try {
                    LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
                    googleMap.setMyLocationEnabled(true);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
                    googleMap.addMarker(new MarkerOptions().position(location));
                } catch (JSONException e) {
                    // Nothing.
                }
            }
        });

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
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.favorite) {
            if (ParseUser.getCurrentUser() == null) {
                ParseLoginBuilder builder = new ParseLoginBuilder(EventDetailActivity.this);
                startActivityForResult(builder.build(), 0);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
