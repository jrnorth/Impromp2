package com.north.joseph.impromp2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.items.Event;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.CountCallback;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONException;

import java.text.ParseException;

public class EventDetailActivity extends Activity {
    private boolean mEventSaved = false;
    private boolean mDataRetrieved = false;
    private Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent intent = getIntent();
        mEvent = intent.getParcelableExtra("event");

        if (savedInstanceState == null) {
            retrieveEventSaved();
        } else {
            mEventSaved = savedInstanceState.getBoolean("eventsaved");
            mDataRetrieved = savedInstanceState.getBoolean("dataretrieved");
            if (!mDataRetrieved)
                retrieveEventSaved();
        }

        ImageView image = (ImageView) findViewById(R.id.eventdetail_picture);
        ImageLoader.getInstance().displayImage(mEvent.getImageURL(), image);

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
                    LatLng location = new LatLng(mEvent.getLatitude(), mEvent.getLongitude());
                    googleMap.setMyLocationEnabled(true);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
                    googleMap.addMarker(new MarkerOptions().position(location));
                } catch (JSONException e) {
                    // Nothing.
                }
            }
        });

        TextView eventName = (TextView) findViewById(R.id.eventdetail_title);
        eventName.setText(mEvent.getName());

        TextView eventDetails = (TextView) findViewById(R.id.eventdetail_details);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Start: ");
        try {
            stringBuilder.append(mEvent.getFormattedStartTime());
        } catch (ParseException ex) {
            stringBuilder.append(mEvent.getStartTime());
        }
        stringBuilder.append("\n");

        stringBuilder.append("End: ");
        try {
            stringBuilder.append(mEvent.getFormattedEndTime());
        } catch (ParseException ex) {
            stringBuilder.append(mEvent.getEndTime());
        }
        stringBuilder.append("\n");

        stringBuilder.append("Address: ");
        try {
            stringBuilder.append(mEvent.getAddress1());
        } catch (JSONException ex) {
            stringBuilder.append("address 1");
        }
        stringBuilder.append("\n");

        try {
            if (!mEvent.getAddress2().equals("null")) {
                stringBuilder.append("         ");
                try {
                    stringBuilder.append(mEvent.getAddress2());
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
            stringBuilder.append(mEvent.getVenueName());
        } catch (JSONException ex) {
            stringBuilder.append("venue");
        }
        stringBuilder.append("\n");

        stringBuilder.append("Free: ");
        stringBuilder.append(mEvent.isFree() ? "Yes" : "No");
        stringBuilder.append("\n");

        eventDetails.setText(stringBuilder.toString());

        WebView eventDescription = (WebView) findViewById(R.id.eventdetail_description);
        eventDescription.loadData(mEvent.getHTML(), "text/html; charset=UTF-8", null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("eventsaved", mEventSaved);
        outState.putBoolean("dataretrieved", mDataRetrieved);
    }

    private void retrieveEventSaved() {
        if (ParseUser.getCurrentUser() != null) {
            ParseRelation<Event> eventRelation = ParseUser.getCurrentUser().getRelation("events");
            ParseQuery<Event> relationQuery = eventRelation.getQuery();
            relationQuery.whereEqualTo("objectId", mEvent.getObjectId());
            relationQuery.countInBackground(new CountCallback() {
                @Override
                public void done(int i, com.parse.ParseException e) {
                    mDataRetrieved = true;
                    if (e == null) {
                        mEventSaved = i > 0;
                    }
                    invalidateOptionsMenu();
                }
            });
        } else {
            mDataRetrieved = true;
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (!mDataRetrieved) {
            menu.findItem(R.id.favorite).setEnabled(false);
        } else {
            menu.findItem(R.id.favorite).setEnabled(true);
        }

        if (mEventSaved) {
            menu.findItem(R.id.favorite).setIcon(R.drawable.ic_action_important);
        } else {
            menu.findItem(R.id.favorite).setIcon(R.drawable.ic_action_not_important);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        return true;
    }

    private void saveEvent() {
        ParseRelation<Event> parseRelation = ParseUser.getCurrentUser().getRelation("events");
        parseRelation.add(mEvent);
        mDataRetrieved = false;
        invalidateOptionsMenu();
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    mEventSaved = true;
                    Toast toast = Toast.makeText(EventDetailActivity.this, "Event saved!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                mDataRetrieved = true;
                invalidateOptionsMenu();
            }
        });
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
            } else {
                if (mEventSaved) {
                    ParseRelation<Event> parseRelation = ParseUser.getCurrentUser().getRelation("events");
                    parseRelation.remove(mEvent);
                    mDataRetrieved = false;
                    invalidateOptionsMenu();
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                mEventSaved = false;
                                Toast toast = Toast.makeText(EventDetailActivity.this, "Event removed from saved.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            mDataRetrieved = true;
                            invalidateOptionsMenu();
                        }
                    });
                } else {
                    saveEvent();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                saveEvent();
            }
        }
    }
}
