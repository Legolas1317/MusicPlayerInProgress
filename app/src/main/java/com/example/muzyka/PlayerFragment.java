package com.example.muzyka;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Button previousButton;
    private Button playButton;
    private Button nextButton;
    private TextView titleTextView;
    private ArrayList<Song> arrayListSong;
    private int currentIndex = 0;
    private boolean isPlaying = false;
    private double currentTime = 0;
    private Handler handler;
    private boolean isSeeking = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_fragment, container, false);

        arrayListSong = new ArrayList<>();

        handler = new Handler();

        seekBar = view.findViewById(R.id.seekBarMainActivity);
        previousButton = view.findViewById(R.id.PREVIOUSMainActivity);
        playButton = view.findViewById(R.id.PLAYMainActivity);
        nextButton = view.findViewById(R.id.NEXTMainActivity);
        titleTextView = view.findViewById(R.id.tytultextView);

        titleTextView.setSelected(true);
        titleTextView.setSingleLine(true);
        titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        titleTextView.setMarqueeRepeatLimit(-1);
        titleTextView.setFocusable(true);
        titleTextView.setFocusableInTouchMode(true);

        mediaPlayer = new MediaPlayer();

        loadSongs();
        if (!arrayListSong.isEmpty()) {
            playSong();
        } else {
            Toast.makeText(getActivity(), "No songs available", Toast.LENGTH_SHORT).show();
        }

        mediaPlayer.setOnPreparedListener(mp -> {
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.seekTo((int) currentTime);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> playNextSong());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo((int) currentTime);
                mediaPlayer.start();
                isSeeking = false;
            }
        });

        previousButton.setOnClickListener(v -> playPreviousSong());
        playButton.setOnClickListener(v -> playPauseSong());
        nextButton.setOnClickListener(v -> playNextSong());

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying() && !isSeeking) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });

        return view;
    }

    private void loadSongs() {
        Context context = getActivity();
        if (context != null) {
            LoadSong loadSong = new LoadSong(context);
            arrayListSong = loadSong.loadSong();
        }
    }

    private void playSong() {
        if (mediaPlayer != null && !arrayListSong.isEmpty()) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(arrayListSong.get(currentIndex).getPath());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error playing song", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No songs available", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPreviousSong() {
        if (currentIndex > 0) {
            currentIndex--;
            playSong();
        } else {
            Toast.makeText(getActivity(), "No previous song", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPauseSong() {
        if (mediaPlayer.isPlaying()) {
            playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
            mediaPlayer.pause();
            isPlaying = false;
        } else {
            playButton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    private void playNextSong() {
        if (currentIndex < arrayListSong.size() - 1) {
            currentIndex++;
            playSong();
        } else {
            Toast.makeText(getActivity(), "No next song", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && !arrayListSong.isEmpty()) {
            playSong();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSong();
    }

    private void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
