package com.example.alarmjava20;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class AlarmMediaPlayer {

    private MediaPlayer mediaPlayer;
    private Context context;
    private static AlarmMediaPlayer instance;

    private AlarmMediaPlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
    }

    public static synchronized AlarmMediaPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new AlarmMediaPlayer(context);
        }
        return instance;
    }

    public void play(Uri musicUri) {
        try {
            if (mediaPlayer.isPlaying()) {
                stop();
            }

            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, musicUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer(); // Создаем новый экземпляр
    }

    public void release() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            instance = null; // обнуляем экземпляр, чтобы при следующем вызове получить новый
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
}
