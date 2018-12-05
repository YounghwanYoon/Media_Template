package com.example.android.media_template;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SourceListActivity extends ListActivity {

    private List<String> itemsInCurrentPath = null;
    private List<String> currentPath = null;
    private String root;
    private String previousSelectedPath;
    private TextView myPath;
    private File rootFile;
    private File[] files;

    //this will store a path of folder that contains music(s) that most recently played.
    private static String lastSavePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        start();
    }

    private void start(){
        //If there was previously selected path, it will start from the selected path;
        if(previousSelectedPath!=null){
            rootFile = new File(previousSelectedPath);
            getDir(rootFile);
        }
        //If this is first time of selecting file.
        else {
            myPath = (TextView) findViewById(R.id.path);

            //"/storage/" path will open directory in between Internal and External SD Cards within the device.
            //failed with s5
            //String secStore = System.getenv("SECONDARY_STORAGE");

            //not with s5
            //String secStore = getBaseContext().getFilesDir().getAbsolutePath();

            //"/storage/" works with note2 not with s5
            //String secStore = "/storage/";
            String secStore = "/storage/";
            //String secStore = "/root/";
            //TODO: Need to do device control. Depends on the device, rootFile could cause error due to having different folder/path names

            Log.i("SourceListActivity.java", "currentPath: " + secStore);
            //Environment.getExternalStorageDirectory().getPath()
            rootFile = new File(secStore);

            getDir(rootFile);
        }
    }

    private void getDir(File currentFolder) {

        itemsInCurrentPath = new ArrayList<String>();
        currentPath = new ArrayList<String>();

        //Display current directory location
        myPath.setText("Current Location: "+ currentFolder);

        //files now has list of files in the current folder(directory)
        files = currentFolder.listFiles();

        if(!currentFolder.equals(rootFile)) {
            itemsInCurrentPath.add(currentFolder.getParent());
            currentPath.add(rootFile.getParent());
            currentPath.add(currentFolder.getParent());
        }
        Log.v("SourceListActivty.java", "Length of files:" +  files.length);

        if(files.length ==0) {
            Log.v("SourceListActivty.java", "Length of files is empty");
        } else {
            //Add all of files in the current Path/Folder to list
            for(int i=0; i < files.length;i++) {
                if(!files[i].isHidden() || files[i].canRead()){
                    itemsInCurrentPath.add(files[i].getParent()+"/"+ files[i].getName()+"/");
                }
            }
        }


        //R.layout.row_each_directory R.id.individual_file,itemsInCurrentPath
        //ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, itemsInCurrentPath);
        MyListAdapter fileList = new MyListAdapter(this,android.R.layout.simple_list_item_1, itemsInCurrentPath, currentFolder);
        setListAdapter(fileList);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //One of item in the current folder is selected
        File selected_file = new File(itemsInCurrentPath.get(position));
        //ToDo: Create static last played folder path.
        lastSavePath = selected_file.toString();
        Log.v("SourceListActivity.java", "Last saved music fold was:" + lastSavePath);
        if(selected_file.isDirectory())
        {
            if(selected_file.canRead())
                getDir(selected_file);
            else //Double caution for selecting non-readable file (which was sorted in getDir();
                Toast.makeText(SourceListActivity.this,"It cannot be read", Toast.LENGTH_SHORT);

        }
        //Once selected file is mp3 file, then it return to parent activity.
        else if(selected_file.getPath().endsWith(".mp3") ){
            Intent returnIntent = getIntent();//new Intent();
            returnIntent.putExtra("result",selected_file.getPath() );
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else
            Toast.makeText(SourceListActivity.this,"It is not a directory", Toast.LENGTH_SHORT);
    }
}