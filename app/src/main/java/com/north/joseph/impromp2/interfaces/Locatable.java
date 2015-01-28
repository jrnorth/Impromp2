package com.north.joseph.impromp2.interfaces;

import android.location.Location;

/**
 * Created by Joe on 1/26/2015.
 */
public interface Locatable {
    public static final String LAST_LOCATION_KEY = "ll";

    public Location getLocation();
}
