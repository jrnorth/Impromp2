package com.north.joseph.impromp2.items;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Joe on 11/23/2014.
 */
@ParseClassName("Event")
public class Event extends ParseObject implements Parcelable {
    public Event() {}

    public Event(Parcel in) {
        String objectId = in.readString();
        setObjectId(objectId);

        String category = in.readString();
        put("category", category);

        String endTime = in.readString();
        put("end_time", endTime);

        String eventId = in.readString();
        put("event_id", eventId);

        boolean[] free = new boolean[1];
        in.readBooleanArray(free);
        put("free", new Boolean(free[0]));

        String name = in.readString();
        put("name", name);

        String startTime = in.readString();
        put("start_time", startTime);

        String url = in.readString();
        put("url", url);

        JSONObject venueObject = new JSONObject();
        JSONObject addressObject = new JSONObject();

        String venueName = in.readString();
        String address1 = in.readString();
        String address2 = in.readString();
        String city = in.readString();
        String postalCode = in.readString();
        double latitude = in.readDouble();
        double longitude = in.readDouble();

        try {
            venueObject.put("name", venueName);

            addressObject.put("address_1", address1);
            addressObject.put("address_2", address2);
            addressObject.put("city", city);
            addressObject.put("postal_code", postalCode);
            addressObject.put("latitude", latitude);
            addressObject.put("longitude", longitude);

            venueObject.put("address", addressObject);
            put("venue", venueObject);
        } catch(JSONException ex) {
            // Nothing. This will never throw an exception.
        }

        String imageUrl = in.readString();
        put("image_url", imageUrl);

        String html = in.readString();
        put("html", html);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getObjectId());
        dest.writeString(getCategory());
        // Write the raw UTC time rather than the formatted time returned by getFormattedEndTime() since we'll
        // want to be able to reuse the getFormattedEndTime() method to get the local time.
        dest.writeString(getEndTime());
        dest.writeString(getEventId());

        boolean[] free = new boolean[1];
        free[0] = isFree();
        dest.writeBooleanArray(free);

        dest.writeString(getName());
        // Write the raw UTC time rather than the formatted time returned by getFormattedStartTime() since we'll
        // want to be able to reuse the getFormattedStartTime() method to get the local time.
        dest.writeString(getStartTime());
        dest.writeString(getUrl());

        try {
            dest.writeString(getVenueName());
        } catch (JSONException ex) {
            Log.d("JSONException", ex.getMessage());
            dest.writeString("venue name");
        }

        try {
            dest.writeString(getAddress1());
        } catch (JSONException ex) {
            Log.d("JSONException", ex.getMessage());
            dest.writeString("address 1");
        }

        try {
            dest.writeString(getAddress2());
        } catch (JSONException ex) {
            Log.d("JSONException", ex.getMessage());
            dest.writeString("address 2");
        }

        try {
            dest.writeString(getCity());
        } catch (JSONException ex) {
            Log.d("JSONException", ex.getMessage());
            dest.writeString("city");
        }

        try {
            dest.writeString(getPostalCode());
        } catch (JSONException ex) {
            Log.d("JSONException", ex.getMessage());
            dest.writeString("postal code");
        }

        try {
            dest.writeDouble(getLatitude());
        } catch (JSONException ex) {
            Log.d("JSONException", ex.getMessage());
            dest.writeString("latitude");
        }

        try {
            dest.writeDouble(getLongitude());
        } catch (JSONException ex) {
            Log.d("JSONException", ex.getMessage());
            dest.writeString("longitude");
        }

        dest.writeString(getImageURL());

        dest.writeString(getHTML());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getFormattedEndTime() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = df.parse(getString("end_time"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        df.setTimeZone(TimeZone.getDefault());
        df.applyPattern("MMMM d, yyyy h:mm a");
        return df.format(date.getTime());
    }

    public String getEndTime() {
        return getString("end_time");
    }

    public String getEventId() {
        return getString("event_id");
    }

    public boolean isFree() {
        return getBoolean("free");
    }

    public String getName() {
        return getString("name");
    }

    public String getFormattedStartTime() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = df.parse(getString("start_time"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        df.setTimeZone(TimeZone.getDefault());
        df.applyPattern("MMMM d, yyyy h:mm a");
        return df.format(date.getTime());
    }

    public String getStartTime() {
        return getString("start_time");
    }

    public String getUrl() {
        return getString("url");
    }

    public String getCategory() {
        return getString("category");
    }

    public String getAddress1() throws JSONException {
        return getJSONObject("venue").getJSONObject("address").getString("address_1");
    }

    public String getAddress2() throws JSONException {
        return getJSONObject("venue").getJSONObject("address").getString("address_2");
    }

    public String getCity() throws JSONException {
        return getJSONObject("venue").getJSONObject("address").getString("city");
    }

    public String getPostalCode() throws JSONException {
        return getJSONObject("venue").getJSONObject("address").getString("postal_code");
    }

    public double getLatitude() throws JSONException {
        return getJSONObject("venue").getJSONObject("address").getDouble("latitude");
    }

    public double getLongitude() throws JSONException {
        return getJSONObject("venue").getJSONObject("address").getDouble("longitude");
    }

    public String getVenueName() throws JSONException {
        return getJSONObject("venue").getString("name");
    }

    public String getImageURL() {
        return getString("image_url");
    }

    public String getHTML() {
        return getString("html");
    }
}
