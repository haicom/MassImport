package com.example.importcvs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
 
public class DBHelper extends AbstractDbAdapter{
 
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CONFIG_NAME = "config_name";
    public static final String KEY_CONFIG_VALUE = "config_value";
    public static final String KEY_POSTED_DATE = "createdAt";
    public static final String KEY_EDITED_DATE = "updatedAt";
    public int maxLevelOnCurrentMenu = 1;
 
    public int getMaxLevelOnCurrentMenu() {
        return maxLevelOnCurrentMenu;
    }
 
    public void setMaxLevelOnCurrentMenu(int maxLevelOnCurrentMenu) {
        this.maxLevelOnCurrentMenu = maxLevelOnCurrentMenu;
    }
 
    public static final String DATABASE_TABLE = "appconfigtbl";
     
    public DBHelper(Context ctx) {
        super(ctx);
    }
     
    public long insertDB(String config_name,String config_value, String createdAt, String updatedAt) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CONFIG_NAME, config_name);
        initialValues.put(KEY_CONFIG_VALUE, config_value);
        initialValues.put(KEY_POSTED_DATE,createdAt);
        initialValues.put(KEY_EDITED_DATE,updatedAt);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
     
    public long insertDB(String[] RowData)
    {
        long result = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_CONFIG_NAME, RowData[0]);
        values.put(KEY_CONFIG_VALUE, RowData[1]);
        values.put(KEY_POSTED_DATE, "");
        values.put(KEY_EDITED_DATE, "");
        result = mDb.insert(DATABASE_TABLE, null, values);
        return result;
    }
     
    public boolean deleteList(long rowId) {     
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
     
    public boolean deleteCongigTableOldRecord() {     
        return mDb.delete(DATABASE_TABLE,  null, null) > 0;
    }
 
}