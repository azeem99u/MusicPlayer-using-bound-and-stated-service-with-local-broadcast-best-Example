package com.example.android.musicplayerboundservice;

import static com.example.android.musicplayerboundservice.App.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MediaPlayerService extends Service {
    Intent mTextChangeIntent;
    private static final String TAG = "azeem";
    public static final String ACTION_COMPLETED = "completed";
    public static final String WORK_COMPLETED = "WORK_COMPLETED";
    public MediaPlayer mPlayer;
    public final MyBinder mBinder = new MyBinder();

    public MediaPlayerService() {}


    @Override
    public void onCreate() {
        super.onCreate();
        mTextChangeIntent = new Intent(ACTION_COMPLETED);
        Log.d(TAG, "onCreateService: ");
        mPlayer = MediaPlayer.create(this,R.raw.ho_karam_sarkar);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mTextChangeIntent.putExtra(WORK_COMPLETED,"done");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(mTextChangeIntent);
                stopForeground(true);
                stopSelf();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction()){
            case Constants.MUSIC_PLAY_KEY:{
                play();
                mTextChangeIntent.putExtra(WORK_COMPLETED,"play");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(mTextChangeIntent);
                break;
            }

            case Constants.MUSIC_PAUSE_KEY:{
                pause();
                mTextChangeIntent.putExtra(WORK_COMPLETED,"pause");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(mTextChangeIntent);
                break;
            }

            case Constants.MUSIC_STOP_KEY:{
                pause();
                stopForeground(true);
                stopSelf();
                mTextChangeIntent.putExtra(WORK_COMPLETED,"pause");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(mTextChangeIntent);
                break;
            }

            case Constants.MUSIC_START_KEY:{
                showNotification();
                break;
            }

            default:{
                stopSelf();
            }
        }

        return START_NOT_STICKY;
    }

    private void showNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setContentTitle("My Music");
        builder.setContentText("service");
        builder.setSmallIcon(R.drawable.ic_baseline_library_music_24);




        Intent playIntent = new Intent(this,MediaPlayerService.class);
        playIntent.setAction(Constants.MUSIC_PLAY_KEY);
        PendingIntent playPendingIntent = PendingIntent.getService(this,100,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this,MediaPlayerService.class);
        pauseIntent.setAction(Constants.MUSIC_PAUSE_KEY);
        PendingIntent pausePendingIntent = PendingIntent.getService(this,100,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this,MediaPlayerService.class);
        stopIntent.setAction(Constants.MUSIC_STOP_KEY);
        PendingIntent stopPendingIntent = PendingIntent.getService(this,100,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Intent targetIntent = new Intent(this,MainActivity.class);
        PendingIntent targetedPIntent = PendingIntent.getActivity(this,100,targetIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(targetedPIntent);

        //action
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_baseline_play_arrow_24,"Play",playPendingIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_baseline_pause_24,"Pause",pausePendingIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_baseline_stop_24,"Stop",stopPendingIntent));

        Notification notification = builder.build();


        startForeground(1234,notification);
    }

    public class MyBinder extends Binder{
        public MediaPlayerService getMediaPlayerService(){
            return MediaPlayerService.this;
        }
    }



    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }
    public void play(){
        mPlayer.start();
    }
    public void pause(){
        mPlayer.pause();
    }





    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        mPlayer.release();

    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind:");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind:");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind: ");
        super.onRebind(intent);

    }
}