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
    private TextView myPath;
    private File rootFile;
    private File[] files;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        myPath = (TextView)findViewById(R.id.path);
        String secStore = System.getenv("SECONDARY_STORAGE");

        //Environment.getExternalStorageDirectory().getPath()
        rootFile = new File(secStore);
        getDir(rootFile);
    }

    private void getDir(File currentFolder) {

        itemsInCurrentPath = new ArrayList<String>();
        currentPath = new ArrayList<String>();

        myPath.setText("Location: "+ currentFolder);

        //files now has list of files in the current folder(directory)
        files = currentFolder.listFiles();

        if(!currentFolder.equals(rootFile))
        {
            itemsInCurrentPath.add(currentFolder.getParent());
            currentPath.add(rootFile.getParent());
            currentPath.add(currentFolder.getParent());
        }

        //Add all of files in the current Path/Folder to list ////  if(!files[i].isHidden() )|| files[i].canRead()   // if(files[i].isDirectory())
        for(int i=0; i < files.length;i++)
        {
            itemsInCurrentPath.add(files[i].getParent()+"/"+ files[i].getName()+"/");
        }

        //R.layout.row_each_directory R.id.individual_file,itemsInCurrentPath
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, itemsInCurrentPath);
        setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //One of item in the current folder is selected
        File selected_file = new File(itemsInCurrentPath.get(position));

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


        /*        if (file.isDirectory()) {
            if (file.canRead()) {
                getDir(new File (currentPath.get(position)));
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_media_startup_image)
                        .setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK", null)
                        .show();

            }
        }
        else{
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_media_startup_image)
                    .setTitle("[" + file.getName() + "]")
                    .setPositiveButton("OK", null)
                    .show();
        }*/
    }
}