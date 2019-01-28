package com.example.android.media_template;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SourceListActivity extends ListActivity {

    protected static List<String> itemsInCurrentPath = null;
    protected static List<String> currentPath = null;
    protected static String root;
    protected static String mPreviousSelectedPath;
    private static TextView myPath;
    private static File rootFile;
    private static File[] files;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static String mCurrentTag;

    //this will store a path of folder that contains music(s) that most recently played.
    private static String lastSavePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentTag = "SourceListActivity.java";

      /*  int permissionCheck = ContextCompat.checkSelfPermission(SourceListActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
*/
        //Remove Title Bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove Notification Bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set content view to avoid crash
        setContentView(R.layout.listview);
        verifyStoragePermissions(this);

        start();
    }

    private void start(){
        myPath = (TextView) findViewById(R.id.path);

        //If there was previously selected path, it will start from the selected path;
        if(mPreviousSelectedPath !=null){
            rootFile = new File(mPreviousSelectedPath);
            getDir(rootFile);
        }
        //If this is first time of selecting file.
        else {

            //"/storage/" path will open directory in between Internal and External SD Cards within the device.
            //String secStore = System.getenv("SECONDARY_STORAGE");
            //not with s5
            //String secStore = getBaseContext().getFilesDir().getAbsolutePath();
            //"/storage/" works with note2 not with s5
            //Environment.getExternalStorageDirectory().getPath()
            // rootFile = new File(secStore);

            File testingRootFile = Environment.getRootDirectory().getParentFile();
            File parentFile = Environment.getExternalStorageDirectory();
            String samsungExSDPath= Environment.getExternalStorageDirectory().getPath();
            File samsungFile;// = new File(samsungExSDPath + "/external_sd/");;

            //Log.i(mCurrentTag, " currentRootFile is : " +testingRootFile.toString());
           // Log.i(mCurrentTag, " getExternalStorageDirectory() is : " +Environment.getExternalStorageDirectory().toString());
         //   Log.i(mCurrentTag, " Environment.getRootDirectory().getParentFile() is : " +Environment.getRootDirectory().getParentFile().toString());
          //  Log.i(mCurrentTag, " getExternalFilesDir(null)is : " +getExternalFilesDir(null).toString());

            if(android.os.Build.DEVICE.contains("Samsung") || android.os.Build.MANUFACTURER.contains("Samsung")||android.os.Build.DEVICE.contains("samsung") || android.os.Build.MANUFACTURER.contains("samsung")){
                //Toast.makeText(this, "MANUFACTURER Name: " + Build.MANUFACTURER, Toast.LENGTH_SHORT).show();

                samsungFile = Environment.getRootDirectory().getParentFile();
                rootFile = samsungFile;
                //                rootFile = new File("\"/storage/\"");
            }
            else{
                //Toast.makeText(this, "Non_SamSung_MANUFACTURER Name: " + Build.MANUFACTURER, Toast.LENGTH_SHORT).show();
                rootFile = parentFile;
            }

            //rootFile = samsungFile;

            getDir(rootFile);
        }
    }

    protected void getDir(File startingFilePath) {

        itemsInCurrentPath = new ArrayList<String>();
        currentPath = new ArrayList<String>();

        //Display current directory location
        myPath.setText("Current Location: "+ startingFilePath.getPath());

        //files now has list of files in the current folder(directory)
        files = startingFilePath.listFiles();

        if(!startingFilePath.equals(rootFile)) {
            if(startingFilePath.getPath() != "/storage/") {
                Log.v(mCurrentTag, "What the hell am i Doing here");
                //A folder that will redirect to previous path;
                itemsInCurrentPath.add(startingFilePath.getParent());

                currentPath.add(rootFile.getParent());
                currentPath.add(startingFilePath.getParent());
            }
        }
        //Log.v("SourceListActivty.java", "Length of files:" +  files.length);

        try {
            if(files.length ==0) {
                Log.v(getCallingActivity() + "", "Length of files is empty");
            }
            else {
                //Add all of files in the current Path/Folder to list
                for (int i = 0; i < files.length; i++) {
                    if (!files[i].isHidden() || files[i].canRead()) {
                        itemsInCurrentPath.add(files[i].getParent() + "/" + files[i].getName() + "/");
                    }
                }
            }
        }catch(NullPointerException e){
            e.getStackTrace();
        }
        //R.layout.row_each_directory R.id.individual_file,itemsInCurrentPath
        //ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, itemsInCurrentPath);
        MyListAdapter fileList = new MyListAdapter(this,android.R.layout.simple_list_item_1, itemsInCurrentPath, startingFilePath);
        setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //One of item in the current folder is selected
        File selected_file = new File(itemsInCurrentPath.get(position));

        if(selected_file.isDirectory())
        {
            if(selected_file.canRead()){
                //Calling previouslySelectedPath() to store most recently visited folder
                //previouslySelectedPath(selected_file);
                //Log.v("SourceListActivity.java", "Last saved music fold was:" + mPreviousSelectedPath);
                getDir(selected_file);
            }
            else //Double caution for selecting non-readable file (which was sorted in getDir();
                Toast.makeText(SourceListActivity.this,"It cannot be read", Toast.LENGTH_SHORT);

        }
        //Once selected file is mp3 file, then it return to parent activity.
        else if(selected_file.getPath().endsWith(".mkv") || selected_file.getPath().endsWith(".mp4")|| selected_file.getPath().endsWith(".mkv") || selected_file.getPath().endsWith(".avi")){
            //Calling previouslySelectedPath() to store most recently visited file
            //previouslySelectedPath(selected_file);
            //Log.v("SourceListActivity.java", "Last saved music fold was:" + mPreviousSelectedPath);

            Intent returnIntent = getIntent();//new Intent();
            returnIntent.putExtra("resultMediaFile",selected_file.getPath() );
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else
            Toast.makeText(SourceListActivity.this,"It is not a directory", Toast.LENGTH_SHORT);
    }

    //This method save most recent path that user looked.
    private void previouslySelectedPath(File previousPath){
        if(previousPath.getPath().endsWith(".mp3"))
            mPreviousSelectedPath = previousPath.getParent();
        else
            mPreviousSelectedPath = previousPath.toString();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}