package com.example.android.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.MobileContract.MobileEntry;

/**
 * Created by Sudha on 18-Jul-17.
 */

public class MobileDbHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "inventory.db";
    public static int DATABASE_VERSION = 1;

    public MobileDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_MOBILE_TABLE = "CREATE TABLE "+ MobileEntry.TABLE_NAME + "("+ MobileEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +MobileEntry.COLUMN_NAME+ " TEXT NOT NULL , "
                +MobileEntry.COLUMN_PRICE+ " INTEGER NOT NULL , "
                +MobileEntry.COLUMN_STOCK+ " INTEGER NOT NULL , "
                +MobileEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL , "
                +MobileEntry.COLUMN_SUPPLIER_PHONE+ " TEXT NOT NULL , "
                +MobileEntry.COLUMN_IMAGE+ " TEXT );";

        db.execSQL(SQL_CREATE_MOBILE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
