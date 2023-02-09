package com.example.coursework3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class WorkoutDetails extends AppCompatActivity {
    int currentWorkoutId;
    RatingBar ratingBar;
    Editable comment;
    EditText commentSection;
    ArrayList<Double> listOfDistances = new ArrayList<Double>();
    ArrayList<Double> listOfSpeeds = new ArrayList<Double>();
    double fastestSpeed, greatestDistance, thisRunDistance, thisRunSpeed;
    DecimalFormat df = new DecimalFormat("#.###");
    double rating;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        /**
         * retrieve ID of current workout/run
         */
        currentWorkoutId = getIntent().getExtras().getInt("id");
        System.out.println(currentWorkoutId);

        /**
         * retrieve all the appropriate text views
         */
        TextView summaryDistance = findViewById(R.id.summaryDistance);
        TextView summaryDuration = findViewById(R.id.summaryDuration);
        TextView summaryElevation = findViewById(R.id.summaryElevation);
        TextView summaryPace = findViewById(R.id.summaryPace);
        TextView summaryDate = findViewById(R.id.summaryDate);
        TextView distanceComparisonText = findViewById(R.id.distCompText);
        TextView speedComparisonText = findViewById(R.id.speedCompText);
        ratingBar = findViewById(R.id.ratingBar);
        commentSection = findViewById(R.id.comments);


        /**
         * populate the textViews, rating bar and comment section with the data retrieved from the database
         */
        Cursor cursor = getDataBaseRow();
        if (cursor.moveToFirst()){
            /**
             * retrieving this run's speed and distance whilst also setting all the values of the current run in the corresponding textViews;
             */
            thisRunDistance = cursor.getDouble(cursor.getColumnIndex(Contract.DISTANCE));
            thisRunSpeed = cursor.getDouble(cursor.getColumnIndex(Contract.PACE));
            summaryDistance.setText(cursor.getString(cursor.getColumnIndex(Contract.DISTANCE)));
            summaryDuration.setText(cursor.getString(cursor.getColumnIndex(Contract.DURATION)));
            summaryElevation.setText(cursor.getString(cursor.getColumnIndex(Contract.ELEVATION)));
            summaryPace.setText(cursor.getString(cursor.getColumnIndex(Contract.PACE)));
            summaryDate.setText(cursor.getString(cursor.getColumnIndex(Contract.DATE)));
            ratingBar.setRating(cursor.getFloat(cursor.getColumnIndex(Contract.RATING)));
            commentSection.setText(cursor.getString(cursor.getColumnIndex(Contract.COMMENTS)));
        }

        /**
         * retrieve greatest distance recorded in all workouts, and set the appropriate comparison in the activity
         */
        Cursor distanceCursor = getDataBaseColumn(Contract.DISTANCE);
        distanceCursor.moveToFirst();
        while(!distanceCursor.isAfterLast()) {
            listOfDistances.add(distanceCursor.getDouble(distanceCursor.getColumnIndex(Contract.DISTANCE)));
            distanceCursor.moveToNext();
        }
        greatestDistance = Collections.max(listOfDistances);
        if(thisRunDistance >= greatestDistance){
            distanceComparisonText.setText("THIS IS YOUR LONGEST DISTANCE RECORDED!");
        } else {
            double diffValue = greatestDistance - thisRunDistance;
            distanceComparisonText.setText("YOU WERE " + df.format(diffValue) + " KM AWAY FROM REACHING YOUR LONGEST RECORDED DISTANCE!");
        }

        /**
         * retrieve fastest speed recorded in all workouts, and set the appropriate comparison in the activity
         */
        Cursor speedCursor = getDataBaseColumn(Contract.PACE);
        speedCursor.moveToFirst();
        while(!speedCursor.isAfterLast()) {
            listOfSpeeds.add(speedCursor.getDouble(speedCursor.getColumnIndex(Contract.PACE)));
            speedCursor.moveToNext();
        }
        fastestSpeed = Collections.max(listOfSpeeds);
         if(thisRunSpeed >= fastestSpeed){
             speedComparisonText.setText("THIS IS YOUR FASTEST RUN!");
         } else {
             double diffValue = fastestSpeed - thisRunSpeed;
             speedComparisonText.setText("YOU WERE " + df.format(diffValue) + " KM/H FROM REACHING YOUR FASTEST RECORDED PACE");
         }

    }

    /**
     * As the 'save changes' button is pressed, the changes made to comments and/or run rating
     * will be posted to the Database and the activity will end
     * @param view
     */
    public void onFinishReviewClick(View view){

        commentSection = findViewById(R.id.comments);
        comment = commentSection.getText();
        ratingBar = findViewById(R.id.ratingBar);
        rating = ratingBar.getRating();
        ContentValues dbVals = new ContentValues();
        dbVals.put(Contract.RATING, rating);
        dbVals.put(Contract.COMMENTS, String.valueOf(comment));
        getContentResolver().update(Contract.WORKOUT_URI, dbVals, Contract._ID+"=?", new String[] {String.valueOf(currentWorkoutId)});
        finish();
    }

    /**
     * returns a cursor object that points to the row of the database where the ID corresponds
     * with the ID of this current workout
     * @return
     */
    private Cursor getDataBaseRow(){
        return getContentResolver().query(Contract.WORKOUT_URI, null, Contract._ID +"=?", new String[] {String.valueOf(currentWorkoutId)}, null);
    }

    /**
     * returns a cursor object that points to the column of the database specified by the 'column'
     * parameter
     * @param column
     * @return
     */
    private Cursor getDataBaseColumn(String column){
        return getContentResolver().query(Contract.WORKOUT_URI, new String[] { column }, null, null, null);
    }
}