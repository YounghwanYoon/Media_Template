package com.example.android.media_template;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.List;

public class MyListAdapter extends ArrayAdapter {
   // private List<String> directoryList;
    private String  singleDirectory;
    private String parentDirectory;
    private List<String>directories;
    private ImageView file_image;
    private TextView file_directory;
    private int itemType;

    private final static int FILE_TYPE_MP3=0;
    private final static int FILE_TYPE_MP4=1;
    private final static int FILE_TYPE_FOLDER = 2;
    private final static int FILE_TYPE_ELSE=3;

    public MyListAdapter(@NonNull Context context, int resource, @NonNull List objects, File currentFolder) {
        super(context, 0, objects);
        parentDirectory = currentFolder.getPath();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       // return super.getView(position, convertView, parent);
        View listItemView = convertView;

        //Check if the existing view is being reused. Otherwise inflate the view.
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.row_each_directory, parent, false);
        }

        //Assign reference views, of R.layout.row_each_directory, with current position
        file_image = (ImageView) listItemView.findViewById(R.id.file_type_image);
        file_directory = (TextView) listItemView.findViewById(R.id.individual_dir);

        //Get the individual list item of current position.
        singleDirectory = (String) getItem(position);

        //Find out the type of Item by calling item
        itemType = findItemType(singleDirectory);
        setImageByType();

        //Set current position directory after removed parent directory parts.
        setTextAfterTrim(singleDirectory, parentDirectory);

        return listItemView;
    }

    private void setTextAfterTrim(String singleDirectory, String parnetDirectory){
        String removedParentDir = singleDirectory.replace(parnetDirectory,"" );
        removedParentDir = removedParentDir.replaceAll("/", "");
        file_directory.setText(removedParentDir);
    }

    private void setImageByType(){
        //Set different image by file type
        switch(itemType){
            case FILE_TYPE_MP3:
                file_image.setImageResource(R.drawable.ic_music);
                break;
            case FILE_TYPE_MP4:
                file_image.setImageResource(R.drawable.ic_video);
                break;
            case FILE_TYPE_FOLDER:
                file_image.setImageResource(R.drawable.ic_folder);
                break;
            case FILE_TYPE_ELSE:
                file_image.setImageResource(R.drawable.ic_file);
                break;
            default:
                file_image.setImageResource(R.drawable.ic_file);
                break;
        }
    }

    private int findItemType(String directory){
        File tempFile = new File(directory);
        int  file_type=FILE_TYPE_ELSE;
        Log.v("MyListAdapter", "directory:"+ directory);
        if(tempFile.isFile())
        {
            if(directory.contains(".mp3")) {
                file_type= FILE_TYPE_MP3;
            }
            else if (directory.contains(".mp4")){
                file_type= FILE_TYPE_MP4;
            }
            else
                file_type=FILE_TYPE_ELSE;
        }
        else if(new File(directory).isDirectory()){
            file_type= FILE_TYPE_FOLDER;
        }
        else
            file_type=FILE_TYPE_ELSE;

        return file_type;
    }
}
