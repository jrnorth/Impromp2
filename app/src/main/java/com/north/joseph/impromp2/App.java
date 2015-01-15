package com.north.joseph.impromp2;

import android.app.Application;

import com.north.joseph.impromp2.items.Event;
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
        Parse.initialize(this, getResources().getString(R.string.application_id), getResources().getString(R.string.client_key));
    }
}
