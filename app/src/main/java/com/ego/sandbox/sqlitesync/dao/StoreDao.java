package com.ego.sandbox.sqlitesync.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ego.sandbox.sqlitesync.entities.Store;

import java.util.ArrayList;

public class StoreDao {
    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public StoreDao(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public boolean create(Store store){
        try{
            // Gets the data repository in write mode
            db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(DBContract.StoreEntry.COLUMN_NAME_STORE_NAME, store.getName());

            System.out.println(values);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(DBContract.StoreEntry.TABLE_NAME, null, values);

            if (newRowId > 0) {
                return true;
            }

        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }

        return false;
    }

    public ArrayList<Store> readAll(){
        ArrayList<Store> stores = new ArrayList<>();

        try {
            db = dbHelper.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    DBContract.StoreEntry.COLUMN_NAME_ID,
                    DBContract.StoreEntry.COLUMN_NAME_STORE_NAME,
            };

            Cursor cursor = db.query(
                    DBContract.StoreEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    null,              // The columns for the WHERE clause
                    null,          // The values for the WHERE clause
                    null,          // don't group the rows
                    null,           // don't filter by row groups
                    null  // The sort order
            );

            if(cursor!=null && cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    Store store = new Store(
                            cursor.getInt(0),
                            cursor.getString(1));
//                    System.out.println(transaction.toString());
                    stores.add(store);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }

        return stores;
    }

    public boolean update(ArrayList<Store> stores) {
        if(emptyTable()){

            try{
                // Gets the data repository in write mode
                db = dbHelper.getWritableDatabase();

                db.beginTransaction();

                for (Store store: stores) {
                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(DBContract.StoreEntry.COLUMN_NAME_STORE_NAME, store.getId());
                    values.put(DBContract.StoreEntry.COLUMN_NAME_STORE_NAME, store.getName());

                    System.out.println(values);

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert(DBContract.StoreEntry.TABLE_NAME, null, values);
                }

                db.setTransactionSuccessful();
                db.endTransaction();

                return true;
            }catch (SQLException e) {
                e.printStackTrace();
            } finally {
                dbHelper.close();
            }

            return false;
        }

        return false;
    }

    public boolean emptyTable(){
        try {
            db = dbHelper.getWritableDatabase();
            db.execSQL("DELETE FROM "+ DBContract.StoreEntry.TABLE_NAME);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }

        return false;
    }
}
