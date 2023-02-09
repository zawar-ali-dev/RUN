package com.example.coursework3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * creates workouts database
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE workouts (" +
                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "distance DOUBLE NOT NULL, " +
                "duration STRING NOT NULL, " +
                "elevationGain STRING NOT NULL, " +
                "date STRING NOT NULL," +
                "rating DOUBLE, " +
                "comments STRING, " +
                "pace DOUBLE NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
