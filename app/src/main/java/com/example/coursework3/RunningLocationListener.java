package com.example.coursework3;

import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

public class RunningLocationListener implements android.location.LocationListener {

    double elevationTotal;
    double distanceTotal, elapsedDistance;
    Location prevLocation, newLocation;
    boolean newRun;

    /**
     * Initialised values to be used
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init(){
        elevationTotal = 0;
        distanceTotal = 0;
        elapsedDistance = 0;
        prevLocation = new Location("");
        prevLocation.setLatitude(0);
        prevLocation.setLongitude(0);
        newLocation = new Location("");
        newLocation.setLatitude(0);
        newLocation.setLongitude(0);
        newRun = true;
    }

    /**
     * Updates when GPS changes location, calculate current totals and set new locations
     * @param location
     */
    @Override
    public void onLocationChanged(Location location){

        /**
         * first thing we do is set up a new location if this is the first run.
         */
        if (newRun){
            newLocation.setLatitude(location.getLatitude());
            newLocation.setLongitude(location.getLongitude());
            newLocation.setAltitude(location.getAltitude());
            newRun = false;
        }

        /**
         * set prev location to last recorded location
         */
        prevLocation.setLatitude(newLocation.getLatitude());
        prevLocation.setLongitude(newLocation.getLongitude());
        prevLocation.setAltitude(newLocation.getAltitude());

        /**
         * set new location to the current/most recent recorded location
         */
        newLocation.setLatitude(location.getLatitude());
        newLocation.setLongitude(location.getLongitude());
        newLocation.setAltitude(location.getAltitude());

        distanceTotal += prevLocation.distanceTo(newLocation);
        elevationTotal += newLocation.getAltitude() - prevLocation.getAltitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // information about the signal, i.e. number of satellites
        Log.d("comp3018", "onStatusChanged: " + provider + " " + status);
    }
    @Override
    public void onProviderEnabled(String provider) {
        // the user enabled (for example) the GPS
        Log.d("comp3018", "onProviderEnabled: " + provider);
    }
    @Override
    public void onProviderDisabled(String provider) {
        // the user disabled (for example) the GPS
        Log.d("comp3018", "onProviderDisabled: " + provider);
    }
}
