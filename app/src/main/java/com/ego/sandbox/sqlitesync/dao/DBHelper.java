package com.ego.sandbox.sqlitesync.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_STORE_ENTRIES = " CREATE TABLE " + DBContract.StoreEntry.TABLE_NAME
            + " ( "
            + DBContract.StoreEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, "
            + DBContract.StoreEntry.COLUMN_NAME_STORE_NAME + " TEXT"
            + " ) ";
    private static final String SQL_DELETE_STORE_ENTRIES = " DROP TABLE IF EXISTS " + DBContract.StoreEntry.TABLE_NAME;

    private static final String DB_NAME = "sync_db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STORE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_STORE_ENTRIES);
        onCreate(db);
    }

}
