package com.north.joseph.impromp2;

import android.app.Application;
import android.util.Log;

import com.north.joseph.impromp2.items.Event;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Joe on 11/23/2014.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Initializing", "initializing...");
        ParseObject.registerSubclass(Event.class);
        Parse.initialize(this, "OlG1RmLjls8tIEmkqvkhxEFF4Vffh7uibaq4Yfpo", "QgiLVuaJJgt4oSDgooL06UArwDZlqYKnZGWY03gb");
    }
}
