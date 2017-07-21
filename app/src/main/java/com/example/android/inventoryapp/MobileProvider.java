package com.example.android.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryapp.MobileContract.MobileEntry;

/**
 * Created by sudha on 19-Jul-17.
 */

public class MobileProvider extends ContentProvider{

    private MobileDbHelper mDbHelper;
    public static final String LOG_TAG = MobileProvider.class.getSimpleName();

    private static final int MOBILE = 100;
    private static final int MOBILE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MobileContract.CONTENT_AUTHORITY, MobileContract.PATH_MOBILE, MOBILE);
        sUriMatcher.addURI(MobileContract.CONTENT_AUTHORITY, MobileContract.PATH_MOBILE+ "/#", MOBILE_ID);
    }

    @Override
    public boolean onCreate() {

        mDbHelper = new MobileDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match){
            case MOBILE:
                cursor = db.query(MobileEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                Log.v(LOG_TAG, cursor.toString());
                break;
            case MOBILE_ID:
                selection = MobileEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(MobileEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                Log.v(LOG_TAG, cursor.toString());
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        int match = sUriMatcher.match(uri);
        switch (match){
            case MOBILE:
                return MobileEntry.CONTENT_LIST_TYPE;
            case MOBILE_ID:
                return MobileEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Log.v("MobileProvider", "testing ");

        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOBILE:
                return insertMobile(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertMobile(Uri uri, ContentValues values){

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(MobileEntry.TABLE_NAME, null, values);



        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int rowsDeleted;

        switch (match){

            case MOBILE:
                rowsDeleted = db.delete(MobileEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOBILE_ID:
                selection = MobileEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(MobileEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int match = sUriMatcher.match(uri);

        switch (match){
            case MOBILE:
                return updateMobile(uri, values, selection, selectionArgs);
            case MOBILE_ID:
                selection = MobileEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMobile(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    private int updateMobile(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(MobileEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
