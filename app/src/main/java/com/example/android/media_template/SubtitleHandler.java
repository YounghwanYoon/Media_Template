package com.example.android.media_template;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SubtitleHandler extends SourceListActivity {
    //private List<String> itemsInCurrentPath = null;
    //private static String mPreviousSelectedPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position,id);
        //One of item in the current folder is selected
        File selected_file = new File(super.itemsInCurrentPath.get(position));

        if(selected_file.isDirectory())
        {
            if(selected_file.canRead()){
                //Calling previouslySelectedPath() to store most recently visited folder
                //previouslySelectedPath(selected_file);
                //Log.v("SourceListActivity.java", "Last saved music fold was:" + mPreviousSelectedPath);
                super.getDir(selected_file);
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

    //This method save most recent path that user looked.
    private void previouslySelectedPath(File previousPath){
        if(previousPath.getPath().endsWith(".smi")||previousPath.getPath().endsWith(".srt"))
            mPreviousSelectedPath = previousPath.getParent();
        else
            mPreviousSelectedPath = previousPath.toString();
    }
}
