package com.example.coursework3;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Provider extends ContentProvider {

    /**
     * Declare dbhelper instance and Urimatcher
     */
    private DBHelper dbHelper = null;
    private static final UriMatcher uriMatcher;


    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Contract.AUTHORITY,"workouts",1);
    }

    /**
     * Instantiate the dbhelper
     * @return
     */
    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(this.getContext(), "workoutDatabase", null, 9);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /**
         * not many tables in database hence simple switch cases
         */
        switch(uriMatcher.match(uri)) {
            case 1:
                return database.query("workouts", projection, selection, selectionArgs, null, null, sortOrder);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        String contentType;

        if(uri.getLastPathSegment() == null){
            contentType = Contract.CONTENT_TYPE_MULTIPLE;
        } else {
            contentType = Contract.CONTENT_TYPE_SINGLE;
        }

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String tableName;

        switch(uriMatcher.match(uri)) {
            case 1:
                tableName = "workouts";
                break;
            default:
                tableName = null;
                break;
        }

        long id = database.insert(tableName, null, values);
        database.close();
        Uri nu = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(nu, null);
        return nu;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(uriMatcher.match(uri)) {
            case 1:
                return db.delete("workouts", selection, selectionArgs);
            default:
                return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch(uriMatcher.match(uri)) {
            case 1:
                return db.update("workouts", values, selection, selectionArgs);
            default:
                return 0;
        }
    }
}
