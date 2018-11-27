package com.example.android.media_template;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button mCheck_list_button;
    private Button mPlayerOrPause_button;
    private Button mNext_button;
    private Button mAdd_List_button;

    private MediaPlayer mMediaPlayer;
    double mCurrentPosition;
    private Handler mHandler = new Handler();

    private SeekBar mSeekBar;
    private TextView elapseTime;
    private TextView remainTime;
    private int totalDuration;
    private String selectedFile;

    private int state;
    private final static int start_state = 0;
    private final static int pause_state = 1;
    private final static int resume_state = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = start_state;
        //Assign references of  Button View in the layout
        mPlayerOrPause_button = (Button) findViewById(R.id.play_or_pause_button);
        mAdd_List_button = (Button) findViewById(R.id.add_list_button);

        mSeekBar = (SeekBar)findViewById(R.id.position_seek_bar);

        //Instantiate Media Player
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

                                MainActivity.this.runOnUiThread(new Runnable(){

                                    @Override
                                    public void run() {
                                        if (mMediaPlayer != null){
                                            int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                                            updateSeekBar();
                                        }
                                        mHandler.postDelayed(this,1000);
                                    }
                                });

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

    private void updateSeekBar() {
        // updating seek bar
        totalDuration = mMediaPlayer.getDuration()/1000;
        mSeekBar.setMax(totalDuration);

        String mMinutes = String.format("%02d", totalDuration/60);
        String mSeconds = String.format("%02d", totalDuration %60);
        String mLengthOfFile = mMinutes + ":" + mSeconds;

        mCurrentPosition = mMediaPlayer.getCurrentPosition()/1000;
        mSeekBar.setProgress((int)mCurrentPosition);

        //ToDo: fix causing crashing error
        //String mCurrentPositionInMin= String.format("%02d%02d", (int)mCurrentPosition);
        //String mCurrentPositionInSec = String.format("%02d", mCurrentPosition %60);
        //String mLengthOfCP = mCurrentPositionInMin + ":" + mCurrentPositionInSec;

        //TextView of current Position of Music.
        TextView elapse_Time = (TextView)findViewById(R.id.elapse_time);
        elapse_Time.setText(String.valueOf(mCurrentPosition));

        if(mMediaPlayer!=null){
            /*
            Runnable mUpdateCurrentPositionTextView = new Runnable(){

                @Override
                public void run() {

                }
            };
*/

            //TextView of total length of current file.
            TextView remain_Time = (TextView)findViewById(R.id.remain_time);
            remain_Time.setText(String.valueOf(mLengthOfFile));
        }
        //To Do : Need to update the changing in both elapse and remain with runnable method(?) or class.

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mMediaPlayer.seekTo(progress*1000);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
    //Call reset() whenever Media Player Object will be reused
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