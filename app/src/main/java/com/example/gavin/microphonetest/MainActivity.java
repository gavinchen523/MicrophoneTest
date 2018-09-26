package com.example.gavin.microphonetest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MicTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private Playbutton mPlayButton = null;
    private MediaPlayer mPlayer = null;


    // Requesting permission to Record_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted) {
            Log.e(LOG_TAG, "not permission");
            finish();
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);

        }
    }

    class Playbutton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public Playbutton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            AudioDeviceInfo[] adj = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
        }

        // Record to the external cache directory for visibility
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gpp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        mPlayButton = new Playbutton(this);
        ll.addView(mPlayButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        setContentView(ll);
//        setContentView(R.layout.activity_main);

        /*

        mRecord = (Button) findViewById(R.id.record_button);
        mPlay = (Button) findViewById(R.id.play_button);
        mMicSelect = (ListView) findViewById(R.id.mic_list);
        mSpkSelect = (ListView) findViewById(R.id.speak_list);

        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
*/

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }


    }
}
