package com.example.muzyka;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SongData extends Application {
    private SharedPreferences sharedPreferences;
    private int currentSeekBarProgress;
    private double currentTime;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public int getCurrentIndex() {
        return sharedPreferences.getInt("currentIndex", 0);
    }

    public void setCurrentIndex(int currentIndex) {
        sharedPreferences.edit().putInt("currentIndex", currentIndex).apply();
    }

    public boolean isPlaying() {
        return sharedPreferences.getBoolean("isPlaying", false);
    }

    public void setPlaying(boolean isPlaying) {
        sharedPreferences.edit().putBoolean("isPlaying", isPlaying).apply();
    }

    public int getCurrentSeekBarProgress() {
        return sharedPreferences.getInt("currentSeekBarProgress", 0);
    }

    public void setCurrentSeekBarProgress(int progress) {
        sharedPreferences.edit().putInt("currentSeekBarProgress", progress).apply();
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double time) {
        currentTime = time;
    }
}
