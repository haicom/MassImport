package com.example.importcvs;
/***
 * 有时有些数据无法通过与服务器连接获取并导入到本地数据库，就需要把数据放置在assets目录下，并且读取此数据并把数据导入到本地数据库中，
 * 用处还是比较多的，比如一些配置信息，还有一些资源等等
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
 
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private boolean isCsvFileNeedToInitialize = true;
    private static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private DBHelper mDbHelper;
    private ProgressDialog mProgressDialog;
    private static int totalRowsUpdate = 0;
     
    public static final String external_sd = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final File sdCard = Environment.getExternalStorageDirectory();
    public static final String sdcardBaseDir = sdCard.getAbsolutePath();
    public static final String externalPath = "/Android/data/com.example/";
    public static final String csvFileName = "appconfig.csv";
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         
        mDbHelper = new DBHelper(this);
        mDbHelper.open();
        totalRowsUpdate = 0;
        
        Log.d(TAG, " onCreate sdcardBaseDir = " + sdcardBaseDir + " externalPath = " + externalPath);
        new InitializeCSVFileAsync(this).execute("");
    }
     
    public static void setTotalRecord(int ctr) {
        totalRowsUpdate = ctr;
    }
     
    private void PopIt( String title, String message ){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setTitle(title);
        alertbox.setMessage(message);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface arg0, int arg1) {
            finish();
        }
        });
        alertbox.show();
    }
     
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
     
    @Override
    protected Dialog onCreateDialog(int id) {
        Resources res = getResources();
        String reader = "";
        int ctr = 0;
        try {
        File f = new File(sdcardBaseDir + externalPath + csvFileName);
        BufferedReader in = new BufferedReader(new FileReader(f));
        while ((reader = in.readLine()) != null) { ctr++; }
            setTotalRecord(ctr);
        }catch(Exception e) {    e.getMessage();  }
     
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
            mProgressDialog = new ProgressDialog(this);
           // mProgressDialog.setProgressDrawable(res.getDrawable(R.drawable.initialize_progress_bar_states));
            mProgressDialog.setMessage("Initializing...");
            mProgressDialog.setMax(ctr);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            return mProgressDialog;
            default:
            return null;
        }
        
    }
     
    // Display Initialize progress bar for uploading CSVFiles to database
    class InitializeCSVFileAsync extends AsyncTask<String, String, String>
    {
        private Context mContext = null;
        
        public InitializeCSVFileAsync(Context context) {
            mContext = context;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDbHelper.deleteCongigTableOldRecord();
            if(isCsvFileNeedToInitialize)
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
        
        @Override
        protected String doInBackground(String... aurl) {
            Log.d(TAG, "doInBackground");
           
                float total = 0F;
                float fctr = 1F;
                String reader = "";
                int ctr = 0;
                boolean skipheader = true;
//                File f = new File(sdcardBaseDir + externalPath + csvFileName);
//                BufferedReader in = new BufferedReader(new FileReader(f));
                 
               try {
                   InputStream inputStream = mContext.getResources().getAssets().open("appconfig.txt");
                   Log.d(TAG, " InitializeCSVFileAsync doInBackground inputStream = " + inputStream);
                   int lenght = inputStream.available();
                   Log.d(TAG, " InitializeCSVFileAsync doInBackground lenght = " + lenght);
                   InputStreamReader inputreader = new InputStreamReader(inputStream);
                   BufferedReader buffreader = new BufferedReader(inputreader);              
                   while ((reader = buffreader.readLine()) != null) {
                   // skip header column name from csv
                       Log.d(TAG, " doInBackground reader = " + reader);
//                       if ( skipheader ) {
//                           skipheader = false;
//                           continue;
//                       }
//                       
                       String[] RowData = reader.split(",");
                       mDbHelper.insertDB(RowData);
                       total += fctr;
                       publishProgress(""+(int)total);
                       //publishProgress((int)(total*100/lenghtOfFile));
                   }
                   buffreader.close();
               } catch (Exception e) {  
                   e.printStackTrace();
               }
               
          
            
            return null;
        }
        
        protected void onProgressUpdate(String... progress) {
            Log.d(TAG, "onProgressUpdate progress[0] = " + progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }
    
        @Override
        protected void onPostExecute(String unused) {     
            File f = new File(sdcardBaseDir + externalPath + csvFileName);
            boolean result = f.delete();
            if(isCsvFileNeedToInitialize)
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            mDbHelper.close();
            //fillAllList();
        }
    
        protected void onDestroy() {
            if (mDbHelper != null) {
                mDbHelper.close();
            }
        }
    }
}