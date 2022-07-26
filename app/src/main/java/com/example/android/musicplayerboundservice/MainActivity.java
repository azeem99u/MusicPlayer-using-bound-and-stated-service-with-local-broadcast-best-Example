package com.example.android.musicplayerboundservice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "azeem";

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(MediaPlayerService.WORK_COMPLETED);
            if (message == "done") {
                musicButton.setText("Play");
            }else if (message == "pause"){
                musicButton.setText("Play");

            }else if (message == "play"){
                musicButton.setText("Pause");
            }

        }
    };

    MediaPlayerService mMediaPlayerService;
    private boolean mBound = false;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlayerService.MyBinder myBinder = (MediaPlayerService.MyBinder) iBinder;
            mMediaPlayerService = myBinder.getMediaPlayerService();
            mBound = true;
            if (mMediaPlayerService.isPlaying()) {
                musicButton.setText("Pause");
            } else {
                musicButton.setText("Play");
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    Button musicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicButton = findViewById(R.id.button);
    }

    public void musicButton(View view) {
        if (mBound) {
            if (mMediaPlayerService.isPlaying()) {
                mMediaPlayerService.pause();
                musicButton.setText("Play");
            } else {
                startService(new Intent(this, MediaPlayerService.class).setAction(Constants.MUSIC_START_KEY));
                mMediaPlayerService.play();
                musicButton.setText("Pause");
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart:");
        bindService(new Intent(this, MediaPlayerService.class), serviceConnection, BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(broadcastReceiver, new IntentFilter(MediaPlayerService.ACTION_COMPLETED));


    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(serviceConnection);
            mBound = false;
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mMediaPlayerService.isPlaying()){
            mMediaPlayerService.stopSelf();
        }
    }
}