package com.example.coursework3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CurrentWorkout extends AppCompatActivity {
    RunningService myService;
    Intent serviceIntent;
    Long durationInMillis;
    Double elapsedDistanceKm, paceValue;

    private static final DecimalFormat df = new DecimalFormat("0.00");


    /**
     * Celled when this activity is first created, instantiates the runningService, binding it to this activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_workout);

        serviceIntent = new Intent(CurrentWorkout.this, RunningService.class);
        bindService(serviceIntent, serviceConnection, 0);
        startService(serviceIntent);


    }

    /**
     * End run and return to main activity
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onEndRunButtonClick(View view){
        Intent intent = new Intent();
        intent.putExtra("duration", durationInMillis);
        intent.putExtra("distance", elapsedDistanceKm);
        intent.putExtra("pace", paceValue);
        setResult(Activity.RESULT_OK, intent);
        stopService(serviceIntent);
        TextView time = findViewById(R.id.timeText);
        TextView runPace = findViewById(R.id.paceText);
        myService.setCurrentRunMetrics((String)time.getText(), (String)runPace.getText());
        myService.stopRun();
        finish();
    }


    /**
     * Utility method used to convert the milliseconds to a string format
     * @param millis
     * @return
     */
    public static String millisToTimeString(long millis) {
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (millis % (1000 * 60)) / 1000;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Method takes in the current total time and current total distance and returns the speed
     * @param timeMillis
     * @param distance
     * @return
     */
    public static double paceCalculator(long timeMillis, double distance){
        double hours = (double)timeMillis / (1000 * 60 * 60);
        //to avoid dividing by 0
        if(distance == 0 ){
            return 0;
        } else {
            return distance/hours;
        }
    }

    /**
     * Handles the thread that constantly updates the run statistics such as duration and distance.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateThread(){
        final long startMillis = System.currentTimeMillis();
        TextView time = findViewById(R.id.timeText);
        TextView distance = findViewById(R.id.distanceText);
        TextView pace = findViewById(R.id.paceText);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //getting the elapsed time
                durationInMillis = System.currentTimeMillis() - startMillis;
                String elapsedTime = millisToTimeString(durationInMillis);

                //getting the elapsed distance
                double elapsedDistanceMeters = myService.getTotalDistance();
                elapsedDistanceKm = elapsedDistanceMeters/1000;

                //getting the current pace
                paceValue = paceCalculator(durationInMillis, elapsedDistanceKm);

                //setting the values in the appropriate views
                pace.setText(df.format(paceValue)+"KM/H");
                distance.setText(df.format(elapsedDistanceKm)+"KM");
                time.setText(elapsedTime);
                handler.postDelayed(this, 1000);
            }
        },1000);
    }


    /**
     * Method to handle Service connection
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            RunningService.MyBinder binder = (RunningService.MyBinder) iBinder;
            myService = binder.getService();
            System.out.println("SERVICE CONNECTION ESTABLISHED!");

            //resetting statistics of a previous run
            myService.resetRunStats();
            updateThread();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("SERVICE CONNECTION LOST!");
        }
    };



}