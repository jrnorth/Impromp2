package com.north.joseph.impromp2;

import android.app.Application;

import com.north.joseph.impromp2.items.Event;
import com.north.joseph.impromp2.items.EventCollection;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Joe on 1/15/2015.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Event.class);
        Parse.initialize(this, getResources().getString(R.string.parse_app_id), getResources().getString(R.string.parse_client_key));
    }
}
