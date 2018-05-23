package com.example.android.media_template;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button mCheck_list_button;
    Button mPlayerOrPause_button;
    Button mNext_button;
    Button mAdd_List_button;

    MediaPlayer mMediaPlayer;

    SeekBar positionBar;
    TextView elapseTime;
    TextView remainTime;
    int totalDuration;
    String selectedFile;

    int state;
    final static int start_state = 0;
    final static int pause_state = 1;
    final static int resume_state = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = start_state;
        mPlayerOrPause_button = (Button) findViewById(R.id.play_or_pause_button);
        mAdd_List_button = (Button) findViewById(R.id.add_list_button);

        mMediaPlayer = new MediaPlayer();
        mPlayerOrPause_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (state){
                    case start_state:
                        selectMusic(selectedFile);
                        Toast.makeText(MainActivity.this, "Playing:" + selectedFile ,Toast.LENGTH_SHORT).show();

                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mediaPlayer.start();
                            }
                        });
                        mMediaPlayer.start();
                        state++;
                        break;
                    case pause_state:
                        mPlayerOrPause_button.setBackgroundResource(R.drawable.ic_play_button_image);
                        Toast.makeText(MainActivity.this, "Pause!" ,Toast.LENGTH_SHORT).show();
                        mMediaPlayer.pause();
                        state ++;
                        break;
                    case resume_state:
                        mPlayerOrPause_button.setBackgroundResource(R.drawable.ic_pause_button_image);
                        Toast.makeText(MainActivity.this, "Resume!" ,Toast.LENGTH_SHORT).show();
                        mMediaPlayer.start();
                        state--;
                        break;
                }
            }
        });

        mAdd_List_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                Intent returnSelectedFile_Intent = new Intent(MainActivity.this, SourceListActivity.class);
                startActivityForResult(returnSelectedFile_Intent,0);
            }
        });

    }

    private void selectMusic(String selected){
        mPlayerOrPause_button.setBackgroundResource(R.drawable.ic_pause_button_image);
        if(selectedFile!=null){
            //mMediaPlayer.reset();
            Uri myUri = Uri.parse("file://" + selectedFile);
            if(mMediaPlayer !=null){
                    mMediaPlayer.reset();
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(getApplicationContext(), myUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("MainActivity.java", "inside selectMusic is called!");
            mMediaPlayer.prepareAsync();
        }
        else
            mMediaPlayer=MediaPlayer.create(MainActivity.this, R.raw.not_there_jeongyup);
    }


    //Result from selecting a file from External SD Driver
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0){
            if(resultCode == Activity.RESULT_OK){
                state = start_state;
                selectedFile = data.getStringExtra("result");
            }
        }
    }
    private void reset() {
        mPlayerOrPause_button.setBackgroundResource(R.drawable.ic_play_button_image);
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mMediaPlayer.reset();
        }
    }
    private void stop(){
        if(mMediaPlayer!=null){
            mMediaPlayer.pause();
            mMediaPlayer.release();
            mMediaPlayer=null;
            mPlayerOrPause_button.setBackgroundResource(R.drawable.ic_play_button_image);
            state = start_state;
        }
    }
    /*
    When an activity goes onStop status, release and nullify MediaPlayer object to restore memory of the device.
     */
    @Override
    protected void onStop() {
        super.onStop();
        stop();
    }

    /*
    When an activity goes onStop status, release and nullify MediaPlayer object to restore memory of the device.
     */
    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }
}