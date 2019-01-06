package com.example.android.media_template;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private LinearLayout seekBar_layout;
    private LinearLayout mediaController_layout;
    private LinearLayout videoView_layout;

    private Handler delayHandler;

    private ImageButton mCheck_list_button;
    private ImageButton mPlayerOrPause_button;
    private ImageButton mNext_button;
    private ImageButton mPrevious_button;
    private ImageButton mAdd_List_button;
    private ImageButton mRotate_Button;

    private static MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();

    private VideoView mVideoScreen;
    private SeekBar mSeekBar;
    private TextView elapseTime;
    private TextView remainTime;
    private long totalDuration;
    private int mCurrentPosition;
    private String mLengthOfFile;
    private String mSelectedFile;

    private static SurfaceView videoView;
    private static SurfaceHolder holder;

    private int state;
    private final static int start_state = 0;
    private final static int pause_state = 1;
    private final static int resume_state = 2;
    private final static int next_file= 3;
    private final static int back_file=4;

    private boolean mMediaPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove Title Bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove Notification Bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set content view to avoid crash
        setContentView(R.layout.activity_main);

        state = start_state;

        mediaController_layout = findViewById(R.id.mediaController);
        seekBar_layout = findViewById(R.id.seekBar_Controller);
        videoView_layout = findViewById(R.id.screen_Layout);

        delayHandler = new Handler();

        mVideoScreen = (VideoView) findViewById(R.id.videoScreen);
        mSeekBar = (SeekBar)findViewById(R.id.position_seek_bar);

        //Assign references of  ImageButton View in the layout
        mPlayerOrPause_button = (ImageButton) findViewById(R.id.play_or_pause_button);
        mAdd_List_button = (ImageButton) findViewById(R.id.add_list_button);
        mNext_button = (ImageButton) findViewById(R.id.next_button);
        mPrevious_button=(ImageButton) findViewById(R.id.previous_button);

        videoView = (SurfaceView)findViewById(R.id.videoScreen);
        holder = videoView.getHolder();

        mediaController();
    }

    private void mediaController(){

        if(mMediaPlaying ){
            try {
                differentTypeOfFileHandler(mSelectedFile);
            } catch (InvocationTargetException ex) {
                ex.getStackTrace();
            }
            mMediaPlayer.seekTo(mCurrentPosition);
            mMediaPlayer.start();

            state++;
        }
        else {
            mPlayerOrPause_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (state) {
                        case start_state:
                            //if (!mMediaPlaying) {                        }
                            try {
                                differentTypeOfFileHandler(mSelectedFile);
                            } catch (InvocationTargetException ex) {
                                ex.getStackTrace();
                            }

                            try {
                                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        MainActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mMediaPlayer != null) {
                                                    //mCurrentPosition = mMediaPlayer.getCurrentPosition();
                                                    updateSeekBar();
                                                }
                                                mHandler.postDelayed(this, 0);
                                            }
                                        });
                                        Toast.makeText(MainActivity.this, "Playing!", Toast.LENGTH_SHORT).show();
                                        mMediaPlaying = true;
                                        mediaPlayer.start();
                                    }
                                });
                            } catch (NullPointerException ex) {
                                ex.getStackTrace();
                                mMediaPlaying = false;
                            }

                            //mMediaPlayer.start();
                            state++;
                            break;
                        case pause_state:
                            mPlayerOrPause_button.setBackgroundResource(R.drawable.ic_play_button_image);
                            Toast.makeText(MainActivity.this, "Pause!", Toast.LENGTH_SHORT).show();
                            if (mMediaPlayer != null) {
                                mMediaPlayer.pause();
                            }
                            mMediaPlaying = false;
                            state++;
                            break;
                        case resume_state:
                            mPlayerOrPause_button.setBackgroundResource(R.drawable.ic_pause_button_image);
                            Toast.makeText(MainActivity.this, "Resume!", Toast.LENGTH_SHORT).show();
                            if (mMediaPlayer != null) {
                                mMediaPlayer.start();
                                mMediaPlaying = true;
                            }
                            state--;
                            break;
                    }
                }
            });

            mAdd_List_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCurrentSource();
                }
            });
        }
    }

    //This method updates current source file.
    private void updateCurrentSource(){
        reset();
        Intent returnSelectedFile_Intent = new Intent(MainActivity.this, SourceListActivity.class);
        startActivityForResult(returnSelectedFile_Intent,0);
    }

    //This method will handle a file differently depends on the type of a media file.
    private void differentTypeOfFileHandler(String selected) throws InvocationTargetException{

        mPlayerOrPause_button.setBackgroundResource(R.drawable.ic_pause_button_image);

        if(mSelectedFile !=null){
            //mMediaPlayer.reset();
            Uri myUri = Uri.parse("file://" + mSelectedFile);
            if(mMediaPlayer !=null){
                    mMediaPlayer.reset();
            }
            mMediaPlayer = new MediaPlayer();
            if(selected.endsWith("mp3")){
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            else{
                mMediaPlayer.setDisplay(holder);
            }

            try {
                mMediaPlayer.setDataSource(getApplicationContext(), myUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.prepareAsync();
        }
        else
            updateCurrentSource();
    }

    //This method will update /track a Seek Bar of Media Player.
    private void updateSeekBar() {
       //if(!mMediaPlaying){ }
       mCurrentPosition = mMediaPlayer.getCurrentPosition();

        // updating seek bar
        totalDuration = mMediaPlayer.getDuration();
        mSeekBar.setMax((int)totalDuration/1000);
        mSeekBar.setProgress(mCurrentPosition/1000);

        //TextView of current Position of Music.
        TextView currentPosition = (TextView)findViewById(R.id.current_position);
        currentPosition.setText(getTimeString(mCurrentPosition));

        //TextView of maximum/total duration of music.
        TextView remain_Time = (TextView)findViewById(R.id.remain_time);
        remain_Time.setText(getMiliSecToTime((int)totalDuration));

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

    private StringBuffer getMiliSecToTime(int millis){
        StringBuffer buffer = new StringBuffer();

        int mSeconds = millis/1000;

        int mHours = mSeconds/(60*60);
        int remainingTime = mSeconds % (60*60);
        int mMinutes = remainingTime /60;
        mSeconds = remainingTime % 60;

        if(mHours == 0)
            return buffer
                    .append(String.format("%02d",mMinutes))
                    .append(":")
                    .append(String.format("%02d",mSeconds));

        else
           return buffer
                    .append(String.format("%02d",mHours))
                    .append(":")
                    .append(String.format("%02d",mMinutes))
                    .append(":")
                    .append(String.format("%02d",mSeconds));
    };

    private StringBuffer getTimeString(int millis) {
        StringBuffer buffer = new StringBuffer();

        int mSeconds = millis/1000;

        int mHours = mSeconds/(60*60);
        int remainingTime = mSeconds % (60*60);
        int mMinutes = remainingTime /60;
        mSeconds = remainingTime % 60;

        if(mHours == 0)
            return buffer
                    .append(String.format("%02d",mMinutes))
                    .append(":")
                    .append(String.format("%02d",mSeconds));

        else
            return buffer
                    .append(String.format("%02d",mHours))
                    .append(":")
                    .append(String.format("%02d",mMinutes))
                    .append(":")
                    .append(String.format("%02d",mSeconds));
    }

    //Result from selecting a file from External SD Driver
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0){
            if(resultCode == Activity.RESULT_OK){
                state = start_state;
                mSelectedFile = data.getStringExtra("result");
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

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);

            // Checks the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();

                videoView_layout.setOnTouchListener(new View.OnTouchListener(){
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        //button pressed
                        if (event.getAction() == MotionEvent.ACTION_DOWN){
                            /*
                            TimerTask task  = new TimerTask(){
                              public void run(){
                                  seekBar_layout.setVisibility(View.VISIBLE);
                                  mediaController_layout.setVisibility(View.VISIBLE);
                              }
                            };*/
                            controllerVisibility_handler();
                        }
                        //button release
                        else if (event.getAction() == MotionEvent.ACTION_UP){

                        }

                        // TODO Auto-generated method stub
                        return false;
                    }
                });
                if(!mMediaPlaying){
                    setControllerVisible();
                }
                else
                    setControllerInvisible();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
                setControllerVisible();
            }
    }


    private void controllerVisibility_handler(){
        setControllerVisible();

        new CountDownTimer(3000, 1000){

            @Override
            public void onTick(long l) {
                setControllerVisible();
            }

            @Override
            public void onFinish() {
                setControllerInvisible();
            }
        }.start();
        /*
        delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable(){
            @Override
            public void run() {
                setControllerInvisible();
            }
        }, 500);

        */
    }
    private void setControllerInvisible(){
        seekBar_layout.setVisibility(View.INVISIBLE);
        mediaController_layout.setVisibility(View.INVISIBLE);
    }

    private void setControllerVisible(){
        seekBar_layout.setVisibility(View.VISIBLE);
        mediaController_layout.setVisibility(View.VISIBLE);
    }



/*
    //Saving data to prevent complete restart which occurs during orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentState", state);
        outState.putInt("mCurrentPosition", mCurrentPosition);
        outState.putBoolean("isMediaPlaying", mMediaPlaying);
        outState.putString ("selectedFile", mSelectedFile);

    }

    //Restoring data to from a restart which occurs during orientation change
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        state = savedInstanceState.getInt("currentState");
        mCurrentPosition = savedInstanceState.getInt("mCurrentPosition");
        mMediaPlaying = savedInstanceState.getBoolean("isMediaPlaying");
        mSelectedFile = savedInstanceState.getString("selectedFile");
        if(mMediaPlayer != null){
            mMediaPlayer.release();;
        }
        mediaController();

//        updateSeekBar();
//        Log.v("MainActivity.java", "mMediaPlaying?1" + mMediaPlaying);
        if (mMediaPlaying) {
            state = start_state;
            try {
                differentTypeOfFileHandler(mSelectedFile);
            } catch (InvocationTargetException e) {
                e.printStackTrace();

                mMediaPlayer.seekTo(mCurrentPosition);
                Log.v("MainActivity.java", "mCurrentPosition?2" + mCurrentPosition);
                mMediaPlayer.start();
            }
        }
        else
            mediaController();

        }
        /*
    private class myView extends SurfaceView implements Runnable{

        public myView(Context context) {
            super(context);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder){

        }

        @Override
        public void run() {

        }

        public void pause(){

        }

        public void stop(){

        }

    }
    */
}


