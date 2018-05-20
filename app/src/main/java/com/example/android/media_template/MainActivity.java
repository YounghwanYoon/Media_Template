package com.example.android.media_template;

import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageButton mCheck_list_button;
    ImageButton mPlayerOrPause_button;
    ImageButton mNext_button;
    ImageButton mAdd_List_button;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connecting button
        mPlayerOrPause_button = findViewById(R.id.play_or_pause_button);




         mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.not_there_jeongyup);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //mMediaPlayer.setDataSource(getApplicationContext(), myUri);
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();

    }
}
