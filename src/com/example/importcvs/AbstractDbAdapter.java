package com.example.importcvs;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public abstract class AbstractDbAdapter {
 
    protected static final String TAG = "ExampleDbAdapter";
    protected DatabaseHelper mDbHelper;
    protected SQLiteDatabase mDb;
 
    protected static final String CONFIG_TABLE_CREATE =
            "create table appconfigtbl (_id integer primary key," + "config_name text not null," + "config_value text," + " createdAt text, " + " updatedAt text);";
 
    protected static final String DATABASE_NAME = "example";
    protected static final int DATABASE_VERSION = 2;
     
    protected final Context mCtx;
     
    protected static class DatabaseHelper extends SQLiteOpenHelper {
     
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CONFIG_TABLE_CREATE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CONFIG_TABLE_CREATE");
        onCreate(db);
    }
}
 
    public AbstractDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
 
    public AbstractDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
 
    public void close() {
    if (mDbHelper != null) {
        mDbHelper.close();
    }
    //mDbHelper.close();
    }
}