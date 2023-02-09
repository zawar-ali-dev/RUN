package com.example.coursework3;

import android.net.Uri;

public class Contract {
    public static final String AUTHORITY = "com.example.coursework3.Provider";

    public static final Uri WORKOUT_URI = Uri.parse("content://"+AUTHORITY+"/workouts");

    public static final String _ID = "_id";
    public static final String DISTANCE = "distance";
    public static final String DURATION = "duration";
    public static final String PACE = "pace";
    public static final String ELEVATION = "elevationGain";
    public static final String DATE = "date";
    public static final String RATING = "rating";
    public static final String COMMENTS = "comments";



    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/Provider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/Provider.data.text";
}
