package com.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class StatusProvider extends ContentProvider {
    private static final String TAG = "com.yamba.StatusProvider";
    private DbHelper dbHelper;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE, StatusContract.STATUS_DIR);
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE + "/#", StatusContract.STATUS_ITEM);
    }

    public StatusProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                Log.d(TAG, "gotType: " + StatusContract.STATUS_TYPE_DIR);
                return StatusContract.STATUS_TYPE_DIR;

            case StatusContract.STATUS_ITEM:
                Log.d(TAG, "gotType: " + StatusContract.STATUS_TYPE_ITEM);
                return StatusContract.STATUS_TYPE_ITEM;

            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnValue = null;

        // Assert correct uri
        if (sURIMatcher.match(uri) != StatusContract.STATUS_DIR)
            throw new IllegalArgumentException("Illegal uri: " + uri);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        // Was insert successful?
        if (rowId != -1) {
            long id = values.getAsLong(StatusContract.Column.ID);
            returnValue = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "Insert uri: " + returnValue);

            // Notify that data for this URI has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnValue;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        Log.d(TAG, "Status Provider - onCreate");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
