package com.ego.sandbox.sqlitesync.dao;

import android.provider.BaseColumns;

public class DBContract {

    private DBContract() {};

    public static class StoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "stores";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_STORE_NAME = "name";
    }
}
