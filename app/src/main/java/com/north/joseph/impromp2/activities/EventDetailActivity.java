package com.north.joseph.impromp2.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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
import com.north.joseph.impromp2.adapters.EventListAdapter;
import com.north.joseph.impromp2.items.Event;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.CountCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONException;

import java.text.ParseException;

public class EventDetailActivity extends Activity {
    private boolean mEventSaved = false;
    private Event mEvent, mEventObjectReference;
    private int mTimesFavoritePressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent intent = getIntent();
        mEvent = intent.getParcelableExtra("event");
        mEventObjectReference = ParseObject.createWithoutData(Event.class, mEvent.getObjectIdForObjectReference());

        if (savedInstanceState == null) {
            retrieveEventSaved();
        } else {
            mTimesFavoritePressed = savedInstanceState.getInt("timespressed");
            mEventSaved = savedInstanceState.getBoolean("eventsaved");
        }

        ImageView image = (ImageView) findViewById(R.id.eventdetail_picture);
        ImageLoader.getInstance().displayImage(mEvent.getImageURLOrNull(), image);

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

        TextView eventTime = (TextView) findViewById(R.id.eventdetail_timeTextView);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(mEvent.getFormattedStartTime());
        } catch (ParseException ex) {
            stringBuilder.append(mEvent.getStartTime());
        }
        stringBuilder.append(" - ");

        try {
            stringBuilder.append(mEvent.getFormattedEndTime());
        } catch (ParseException ex) {
            stringBuilder.append(mEvent.getEndTime());
        }

        eventTime.setText(stringBuilder.toString());

        TextView eventLocation = (TextView) findViewById(R.id.eventdetail_locationTextView);
        stringBuilder.setLength(0);

        try {
            final String address1 = mEvent.getAddress1();
            if (!address1.equals("null"))
                stringBuilder.append(address1);
        } catch (JSONException ex) {
            stringBuilder.append("address 1");
        }

        try {
            final String address2 = mEvent.getAddress2();
            if (!address2.equals("null")) {
                if (stringBuilder.length() > 0) // There was an address1, so we need a newline.
                    stringBuilder.append("\n");
                stringBuilder.append(address2);
            }
        } catch (JSONException ex) {
            // We'll just assume there's no address 2 if this exception is thrown.
        }

        try {
            final String venue = mEvent.getVenueName();
            if (!venue.equals("null")) {
                if (stringBuilder.length() > 0)// There was an address1 or address2, so we need a newline.
                   stringBuilder.append("\n");
                stringBuilder.append(venue);
            }
        } catch (JSONException ex) {
            stringBuilder.append("venue");
        }

        eventLocation.setText(stringBuilder.toString());

        ((TextView) findViewById(R.id.eventdetail_categoryTextView)).setText(mEvent.getCategory());

        ((TextView) findViewById(R.id.eventdetail_freeTextView)).setText(mEvent.isFree() ? "Free: Yes" : "Free: No");

        WebView eventDescription = (WebView) findViewById(R.id.eventdetail_description);
        eventDescription.loadData(mEvent.getHTML(), "text/html; charset=UTF-8", null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("eventsaved", mEventSaved);
        outState.putInt("timespressed", mTimesFavoritePressed);
    }

    private void retrieveEventSaved() {
        if (ParseUser.getCurrentUser() != null) {
            ParseRelation<Event> eventRelation = ParseUser.getCurrentUser().getRelation("events");
            ParseQuery<Event> relationQuery = eventRelation.getQuery();
            relationQuery.whereEqualTo("objectId", mEventObjectReference.getObjectId());
            Toast toast = Toast.makeText(this, mEventObjectReference.getObjectId() == null ? "NULL" : mEventObjectReference.getObjectId(), Toast.LENGTH_LONG);
            toast.show();
            relationQuery.fromLocalDatastore();

            int count = 0;
            try {
                count = relationQuery.count();
            } catch (com.parse.ParseException e) {
                mEventSaved = false;
            }

            if (count > 0)
                mEventSaved = true;

            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

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
        parseRelation.add(mEventObjectReference);

        mEventObjectReference.pinInBackground();

        mEventSaved = true;
        ParseUser.getCurrentUser().saveEventually();

        Toast toast = Toast.makeText(EventDetailActivity.this, "Event saved!", Toast.LENGTH_SHORT);
        toast.show();

        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        if (mTimesFavoritePressed % 2 == 0)
            setResult(RESULT_CANCELED);
        else
            setResult(RESULT_OK);

        super.onBackPressed();
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
                LoginConfirmationFragment loginConfirmationFragment = new LoginConfirmationFragment();
                loginConfirmationFragment.show(getFragmentManager(), "lcf");
            } else {
                ++mTimesFavoritePressed;
                if (mEventSaved) {
                    ParseRelation<Event> parseRelation = ParseUser.getCurrentUser().getRelation("events");
                    parseRelation.remove(mEventObjectReference);

                    mEventObjectReference.unpinInBackground();

                    mEventSaved = false;
                    ParseUser.getCurrentUser().saveEventually();

                    Toast toast = Toast.makeText(EventDetailActivity.this, "Event removed from saved.", Toast.LENGTH_SHORT);
                    toast.show();

                    invalidateOptionsMenu();
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
                ParseUser.getCurrentUser().put(EventListAdapter.USER_SAVED_EVENTS_LOADED, false);

                ++mTimesFavoritePressed;
                saveEvent();
            }
        }
    }

    private void startParseLoginActivity() {
        ParseLoginBuilder builder = new ParseLoginBuilder(EventDetailActivity.this);
        startActivityForResult(builder.build(), 0);
    }

    public static class LoginConfirmationFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("To save events, you need to log in to your account or create an account if you do not have one.")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((EventDetailActivity) getActivity()).startParseLoginActivity();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
