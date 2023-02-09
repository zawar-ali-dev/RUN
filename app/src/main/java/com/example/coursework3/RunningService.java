package com.example.coursework3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class RunningService extends Service {

    private static final String CHANNEL_ID = "100";
    private NotificationManager notificationManager;
    LocationManager locationManager;
    RunningLocationListener locationListener;
    String durationString, runPace;
    private NotificationCompat.Builder mBuilder;
    private final IBinder binder = new MyBinder();
    private int NOTIFICATION_ID = 001;


    public class MyBinder extends Binder {
        RunningService getService(){
            return RunningService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * instantiating location listener as the service is being created
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(){
        Log.d("comp3018", "Service Created");
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new RunningLocationListener();
        locationListener.init();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5, // minimum time interval between updates
                    5, // minimum distance between updates, in metres
                    locationListener);
        } catch(SecurityException e) {
            Log.d("comp3018", e.toString());
        }
        super.onCreate();
        createNotification();
    }

    /**
     * Returns today's date in the format dd/mm/yy
     * @return
     */
    public static String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        return formatter.format(date);
    }

    /**
     * resets the total distance and elevation values measured by the lcoation listener
     */
    public void resetRunStats(){
        locationListener.distanceTotal = 0;
        locationListener.elevationTotal = 0;
    }

    /**
     * getters and setters for run metrics
     */
    public void setCurrentRunMetrics(String duration, String pace){
        this.durationString = duration;
        this.runPace = pace;
    }

    public double getTotalDistance(){
        return locationListener.distanceTotal;
    }

    public double getElevationTotal(){
        return locationListener.elevationTotal;
    }

    /**
     * Method called when workout is over, places all the current run's metrics into the database
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void stopRun(){
        ContentValues dbVals = new ContentValues();
        dbVals.put(Contract.DISTANCE, Math.round(((double)getTotalDistance()/ 1000) * 100.0) / 100.0);
        dbVals.put(Contract.DURATION, durationString);
        dbVals.put(Contract.PACE, runPace);
        dbVals.put(Contract.ELEVATION, ((double)getElevationTotal()));
        dbVals.put(Contract.DATE, getFormattedDate());
        getContentResolver().insert(Contract.WORKOUT_URI, dbVals);
    }

    /**
     * Stop service when application is closed
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    /**
     * Handles creation of notification as service begins
     */
    public void createNotification(){

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, CurrentWorkout.class);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_IMMUTABLE);
        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.run_icon)
                .setContentTitle("Fitness Progress!")
                .setContentText("Tracking your Run")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        startForeground(NOTIFICATION_ID, mBuilder.build());
    }
}
