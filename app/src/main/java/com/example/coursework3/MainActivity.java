package com.example.coursework3;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {
    Button startRun;
    ListView listView;
    SimpleCursorAdapter dataAdapter;
    private ActivityResultLauncher<Intent> currentRunActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRun = findViewById(R.id.startRunButton);
        listView = findViewById(R.id.listView);

        /**
         * launcher for the activities
         */
        currentRunActivityLauncher = registerForActivityResult(new
                        ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        updateListView();
                    }
                });

        /**
         * calling this method in order to populate the listview with the workouts in the database, if any
         */
        updateListView();

    }

    /**
     * Starts running service and load new activity for current workout
     * @param view
     */
    public void onStartRunButtonClick(View view){

        Intent intent = new Intent(MainActivity.this, CurrentWorkout.class);
        currentRunActivityLauncher.launch(intent);

    }

    /**
     * Populates the listView with content from the database
     */
    public void updateListView(){

        Cursor cursor = getContentResolver().query(Contract.WORKOUT_URI, null, null, null, null);
        System.out.println(cursor);
        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.listview_item_layout,
                cursor,
                new String[]{Contract.DISTANCE, Contract.DURATION, Contract._ID, Contract.DATE},
                new int[] {R.id.distanceTextView, R.id.durationTextView, R.id.hiddenId, R.id.dateText},
                0
        );

        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3){
                TextView tv = v.findViewById(R.id.hiddenId);
                String tvText = tv.getText().toString();
                int workoutID = new Integer(tvText).intValue();
                Intent myIntent = new Intent(MainActivity.this, WorkoutDetails.class);
                myIntent.putExtra("id", workoutID);
                currentRunActivityLauncher.launch(myIntent);
            }
        });
    }

}