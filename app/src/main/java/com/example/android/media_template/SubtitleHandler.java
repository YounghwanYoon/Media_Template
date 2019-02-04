package com.example.android.media_template;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class SubtitleHandler extends SourceListActivity {
    //private List<String> itemsInCurrentPath = null;
    //private static String mPreviousSelectedPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.Tag= "SubtitleHandler.java";
    }

    @Override
    protected void start() {
        super.start();
    }

    @Override
    protected void getDir(File currentFolder) {
        super.getDir(currentFolder);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
       // super.onListItemClick(l,v,position,id);
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
                Toast.makeText(SubtitleHandler.this,"It cannot be read", Toast.LENGTH_SHORT);

        }
        //Once selected file is mp3 file, then it return to parent activity.
        else if(selected_file.getPath().endsWith(".smi") || selected_file.getPath().endsWith(".srt")){
            //Calling previouslySelectedPath() to store most recently visited file
            //previouslySelectedPath(selected_file);
            //Log.v("SourceListActivity.java", "Last saved music fold was:" + mPreviousSelectedPath);

            Intent returnIntent = getIntent();//new Intent();
            returnIntent.putExtra("resultSubtitleFile",selected_file.getPath() );
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else
            Toast.makeText(SubtitleHandler.this,"It is not a directory", Toast.LENGTH_SHORT);
    }

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
