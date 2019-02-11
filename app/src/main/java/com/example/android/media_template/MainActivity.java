package com.example.android.media_template;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static android.media.MediaPlayer.*;

public class MainActivity extends AppCompatActivity  implements OnTimedTextListener, SurfaceHolder.Callback {

    private String Tag;

    private LinearLayout seekBar_layout;
    private LinearLayout mediaController_layout;
    private LinearLayout videoView_layout;

    private static CountDownTimer countDown;
    static int  currentOrientation;

    private ImageButton mCheck_list_button;
    private ImageButton mPlayOrPauseButton;
    private ImageButton mNext_button;
    private ImageButton mAdd_Subtitle_button;
    private ImageButton mAdd_List_button;
    private ImageButton mRotate_Button;

    private static MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();

    private TextView mSubTitleView;

    //private static VideoView mVideoView;
    private SeekBar mSeekBar;

    private static int mCurrentPosition;
    private static int mCurrentPositionBackUp;
    private String mSelectedFile;
    private String mSelectedSub;
    private static String mPreviousSelectedFile;

    private SurfaceHolder mSurfaceHolder;
    private ViewGroup.LayoutParams params;

    private int state;
    private final int start_state = 0;
    private final int pause_state = 1;
    private final int resume_state = 2;

    private final int mFileType = 0;
    private final int mSubtitle_type = 1;

    private static final int MEDIA_FILE_REQUEST = 0;
    private static final int SUBTITLE_FILE_REQUEST = 1;

    private boolean mMediaPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tag = MainActivity.class.getName();
        Toast.makeText(MainActivity.this, "Hello I am Called ONCREATE()!", Toast.LENGTH_SHORT).show();

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

        countDown =  new CountDownTimer(3000, 1000){
            @Override
            public void onTick(long l) {
                setControllerVisible();
            }

            @Override
            public void onFinish() {
                setControllerInvisible();
            }
        };

        SurfaceView mSurfaceView = findViewById(R.id.videoView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceView.setKeepScreenOn(true); // keepScreen On While playing

        mSeekBar = findViewById(R.id.position_seek_bar);
        mSubTitleView = findViewById(R.id.subTitle_textView);

        //Assign references of  ImageButton View in the layout
        mPlayOrPauseButton = findViewById(R.id.play_or_pause_button);
        mAdd_List_button = findViewById(R.id.add_list_button);
        mNext_button =  findViewById(R.id.next_button);
        mAdd_Subtitle_button = findViewById(R.id.ic_subtitle_image);

        params = mediaController_layout.getLayoutParams();
        int backupHeight = params.height;

        assignMediaControl();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void assignMediaControl(){
        mPlayOrPauseButton.setOnClickListener(new OnClickListener() {
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
                            mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mMediaPlayer != null && mMediaPlaying) {
                                                try {
                                                    if(mSelectedSub != null){
                                                        Log.i(Tag, "subtitleHandler is called");
                                                        if(isSameFile())
                                                            subtitleHandler(mSelectedSub);
                                                            //remove previously selected subtitle when new media player is inserted.
                                                        else
                                                            mSelectedSub = null;
                                                    }
                                                    updateSeekBar();
                                                }catch (IllegalStateException e){
                                                    e.getStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            mHandler.postDelayed(this, 500);
                                        }
                                    });
                                    Toast.makeText(MainActivity.this, "Playing!", Toast.LENGTH_SHORT).show();
                                    mMediaPlaying = true;
                                    if(isSameFile()){
                                        mMediaPlayer.seekTo(mCurrentPositionBackUp);
                                    }
                                    if(mMediaPlayer != null ) {
                                        state++;
                                        mSubTitleView.setVisibility(View.VISIBLE);

                                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                                            setControllerInvisible();
                                        }
                                        mediaPlayer.start();
                                    }
                                }
                            });
                        } catch (NullPointerException ex) {
                            ex.getStackTrace();
                            mMediaPlaying = false;
                        }

                        //mMediaPlayer.start();
                        break;
                    case pause_state:
                        mPlayOrPauseButton.setBackgroundResource(R.drawable.ic_play_button_image);
                        if (mMediaPlayer != null) {
                            if(mSelectedSub != null)
                                mSubTitleView.setVisibility(View.INVISIBLE);
                            mMediaPlayer.pause();
                        }
                        mMediaPlaying = false;
                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            Toast.makeText(MainActivity.this, "Pause!", Toast.LENGTH_SHORT).show();
                            setControllerVisible();
                        }
                        state++;
                        break;
                    case resume_state:
                        mPlayOrPauseButton.setBackgroundResource(R.drawable.ic_pause_button_image);
                        Toast.makeText(MainActivity.this, "Resume!", Toast.LENGTH_SHORT).show();
                        if (mMediaPlayer != null) {
                            mSubTitleView.setVisibility(View.VISIBLE);
                            mMediaPlayer.start();
                            mMediaPlaying = true;
                            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                                setControllerInvisible();
                            }
                        }
                        state--;
                        break;
                }
            }
        });
        mAdd_List_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSourceFile(mFileType);
            }
        });

        videoView_layout.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                //button pressed
                if (event.getAction() == MotionEvent.ACTION_DOWN && currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
                    controllerVisibility_handler(Configuration.ORIENTATION_LANDSCAPE);
                }
                //button release
               //if (event.getAction() == MotionEvent.ACTION_UP){
                //Do nothing when touching a screen is released
                return false;
            }
        });

        mAdd_Subtitle_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSourceFile(mSubtitle_type);
            }
        });
    }

    //Verify whether currently selected file is same file as previously selected.
    private boolean isSameFile(){
            if(mSelectedFile == null && mSelectedFile != mPreviousSelectedFile) {
                return false;
            }
            else
                return true;
    }

    //This method will handle a file differently depends on the type of a media file.
    private void differentTypeOfFileHandler(String selected) throws InvocationTargetException{

        if(mSelectedFile !=null){
            mPlayOrPauseButton.setBackgroundResource(R.drawable.ic_pause_button_image);

            //mMediaPlayer.reset();
            Uri myUri = Uri.parse("file://" + mSelectedFile);
            if(mMediaPlayer !=null){
                // mMediaPlayer.reset();
            }
            mMediaPlayer = new MediaPlayer();
            if(selected.endsWith("mp3")){
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            else{
                mMediaPlayer.setDisplay(mSurfaceHolder);
                //mMediaPlayer.addTimedTextSource();
            }
            try {
                mMediaPlayer.setDataSource(getApplicationContext(), myUri);
                //mVideoView.requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.prepareAsync();
        }
        else
            getSourceFile(mFileType);
    }

    //This method updates current source file.
    private void getSourceFile(int source_Type){

        //reset();
        if(source_Type == mSubtitle_type){
            Intent returnSelectedSubtitle_Intent = new Intent(MainActivity.this, SubtitleHandler.class);
            startActivityForResult(returnSelectedSubtitle_Intent,SUBTITLE_FILE_REQUEST);

        }
        else if (source_Type == mFileType){
            Intent returnSelectedFile_Intent = new Intent(MainActivity.this, SourceListActivity.class);
            startActivityForResult(returnSelectedFile_Intent,MEDIA_FILE_REQUEST);
        }
    }

    // https://stackoverflow.com/questions/13422673/looking-for-a-working-example-of-addtimedtextsource-for-adding-subtitle-to-a-vid
    private void subtitleHandler(String selected) throws IOException {
        Thread.interrupted();
        if (selected.endsWith("srt") ||selected.endsWith("smi")){
             Log.i(Tag, "SubtitleHandler is called!");
             try {
                 mSubTitleView.setVisibility(View.VISIBLE);
                 mMediaPlayer.addTimedTextSource(selected, MEDIA_MIMETYPE_TEXT_SUBRIP);
             } catch (NullPointerException e){
                 e.getStackTrace();
                 e.printStackTrace();
             }
             try{
                 int textTrackIndex = findTrackIndexFor(TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mMediaPlayer.getTrackInfo());
                 if (textTrackIndex >= 0) {
                     mMediaPlayer.selectTrack(textTrackIndex);
                 } else {
                     Log.w("test", "Cannot find text track!");
                 }
             }catch(RuntimeException e){
                e.getStackTrace();
             }
            //mMediaPlayer.setOnTimedTextListener(this);
        }
    }

    private int findTrackIndexFor(int mediaTrackType, TrackInfo[] trackInfo) {
        int mediaTrackType1 = mediaTrackType;
        TrackInfo[] trackInfo1 = trackInfo;
        int index = -1;
        for (int i = 0; i < trackInfo.length; i++) {
            if (trackInfo[i].getTrackType() == mediaTrackType) {
                return i;
            }
        }
        return index;
    }

    @Override
    public void onTimedText(MediaPlayer mediaPlayer, final TimedText timedText) {
        if(isSameFile())
            mSubTitleView.setText(timedText.getText());
    }

    //This method will update /track a Seek Bar of Media Player.
    private void updateSeekBar() {

         mCurrentPosition = mMediaPlayer.getCurrentPosition();

        // updating seek bar
        long totalDuration = mMediaPlayer.getDuration();
        mSeekBar.setMax((int) totalDuration /1000);
        mSeekBar.setProgress(mCurrentPosition/1000);

        //TextView of current Position of Music.
       final TextView currentPosition = findViewById(R.id.current_position);
        //currentPosition.setText(getTimeString(mCurrentPosition));
        //TextView of maximum/total duration of music.
        TextView remain_Time = findViewById(R.id.remain_time);
        remain_Time.setText(getMiliSecToTime((int) totalDuration));

        mMediaPlayer.setOnTimedTextListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //TextView of current Position of Music.
            //TextView currentPosition = findViewById(R.id.current_position);
            int tempProgress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //if User initiate changing its seekbar, then it updates accordingly.
                if(fromUser){
                    tempProgress = progress*1000;
                }
                //else currentPosition of Text View will update programmatically.
                else{
                    currentPosition.setText(getTimeString(progress*1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(tempProgress);
            }
        });
    }

    @SuppressLint("DefaultLocale")
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
    }

    @SuppressLint("DefaultLocale")
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

    //called whenever intent is returned
    //Result from selecting a media file.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==MEDIA_FILE_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                if(data.getStringExtra("resultMediaFile") != null){
                    state = start_state;
                    mSelectedFile = data.getStringExtra("resultMediaFile");
                    mPreviousSelectedFile = data.getStringExtra("resultMediaFile");
                    if(mSelectedFile != null && mPreviousSelectedFile != null){
                        Log.i(Tag, "mSelectedFile is:" + mSelectedFile);
                        Log.i(Tag, "mPreviousSelectedFile is:" + mPreviousSelectedFile);
                    }
                }
            }
            //In case where user did not select a file.
            else{
                Toast.makeText(this, "mSelectedFile is not selected ", Toast.LENGTH_SHORT).show();
                if(mPreviousSelectedFile!=null)
                    mSelectedFile = mPreviousSelectedFile;
            }
        }
        else if (requestCode == SUBTITLE_FILE_REQUEST){
            if(resultCode == Activity.RESULT_OK) {
                if(data.getStringExtra("resultSubtitleFile") != null){
                    mMediaPlayer.release();
                    mSelectedSub = data.getStringExtra("resultSubtitleFile");
                    Toast.makeText(this, "Subtitle is added", Toast.LENGTH_LONG);
                    try {
                        subtitleHandler(mSelectedSub);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
                Toast.makeText(this, "Subtitle is not added", Toast.LENGTH_LONG);
        }
    }

    /*
        When an activity goes onPause status, backup currentPosition and update mPlayOrPauseButton background image.
     */
    @Override
    protected void onPause() {
        if(mMediaPlayer != null){
            if(mMediaPlaying){
                mMediaPlayer.pause();
                mPlayOrPauseButton.setBackgroundResource(R.drawable.ic_play_button_image);
                state = start_state;
                mCurrentPositionBackUp = mCurrentPosition;
            }
            else{
                mPlayOrPauseButton.setBackgroundResource(R.drawable.ic_play_button_image);
                state = start_state;
                mCurrentPositionBackUp = mCurrentPosition;
            }
        }
        super.onPause();
    }
    /*
    When an activity goes onResume status, pause update the state.
  */
    @Override
    protected void onResume(){
        super.onResume();
        if(mMediaPlayer !=null && mMediaPlaying){
            Toast.makeText(MainActivity.this, "Hello I am Called OnResume()!", Toast.LENGTH_SHORT).show();
            //mMediaPlayer.pause();
            state = start_state;
        }
    }
    /*
    When an activity goes onStop status, pause mMediaPlayer to avoid restarting the file.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }
    /*
    When an activity goes onDestroy status, release and nullify MediaPlayer object to restore memory of the device.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mSelectedSub = null;
        }
    }
    //media control box visibility related
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        currentOrientation = newConfig.orientation;
        params = mediaController_layout.getLayoutParams();
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if(!mMediaPlaying)
                setControllerVisible();
            else
                setControllerInvisible();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

            controllerVisibility_handler(currentOrientation);
        }
    }
    private void controllerVisibility_handler(int currentOrientation){
        setControllerVisible();
        if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE)
            countDown.start();
        else
            countDown.cancel();
    }
    private void setControllerInvisible(){
        seekBar_layout.setVisibility(View.INVISIBLE);
        mediaController_layout.setVisibility(View.INVISIBLE);
    }
    private void setControllerVisible(){
        seekBar_layout.setVisibility(View.VISIBLE);
        mediaController_layout.setVisibility(View.VISIBLE);
    }

    //Saving data to prevent complete restart which occurs during orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("currentState", state);
        outState.putInt("mCurrentPosition", mCurrentPosition);
        outState.putBoolean("isMediaPlaying", mMediaPlaying);
        outState.putString ("mSelectedSub", mSelectedSub);
        outState.putInt("mCurrentPositionBackUp", mCurrentPositionBackUp);
    }

    //Restoring data to from a restart which occurs during orientation change
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        state = savedInstanceState.getInt("currentState");
        //mCurrentPosition = savedInstanceState.getInt("mCurrentPosition");
        mMediaPlaying = savedInstanceState.getBoolean("isMediaPlaying");
        //mSelectedFile = savedInstanceState.getString("selectedFile");
        if(isSameFile() && mCurrentPosition >0){
            mCurrentPositionBackUp = savedInstanceState.getInt("mCurrentPositionBackUp");
        }
        mSelectedSub = savedInstanceState.getString("mSelectedSub");

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.w(Tag, "Surface is created!");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        Log.w(Tag, "Surface is changed!");
        surfaceHolder.setKeepScreenOn(true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        onPause();
        Log.i(Tag, "Surface is destroyed!");
    }
}
