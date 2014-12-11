package com.north.joseph.impromp2.items;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Joe on 11/23/2014.
 */
@ParseClassName("Event")
public class Event extends ParseObject {
    public String getDescription() {
        return getString("description");
    }

    public String getFormattedEndTime() throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(getString("end_time"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new SimpleDateFormat("MMMM d, yyyy hh:mm").format(cal.getTime());
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
}
