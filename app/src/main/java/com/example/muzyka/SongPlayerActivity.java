package com.example.muzyka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SongPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_SONG_LIST = "com.example.muzyka.EXTRA_SONG_LIST";
    public static final String EXTRA_SELECTED_SONG_INDEX = "com.example.muzyka.EXTRA_SELECTED_SONG_INDEX";

    private Button next, play, back;
    private TextView textView;
    private SeekBar seekBar;
    private ArrayList<String> songList;
    private int currentIndex;
    private android.media.MediaPlayer mediaPlayer;
    private ArrayList<String> songPathList;
    private HashMap<String, String> songMap; // Deklaracja mapy
    private boolean isSeeking = false;
    private boolean isPlaying = false; // Dodano flagę odtwarzania
    private double currentTime = 0;
    private SongData songData;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);

        // Ustaw niestandardowy układ paska u góry jako pasek akcji
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.custom_toolbar);

        play = findViewById(R.id.PLAY);
        next = findViewById(R.id.NEXT);
        back = findViewById(R.id.BACK);
        textView = findViewById(R.id.textView);
        seekBar = findViewById(R.id.seekBar);

        //ustawienie na przyciks play icony


        songList = new ArrayList<>();
        songPathList = new ArrayList<>();
        mediaPlayer = new android.media.MediaPlayer();
        songData = (SongData) getApplication();

        Intent intent = getIntent();
        currentIndex = intent.getIntExtra("index", 0);

        Toast.makeText(this, "Index przekazany " + currentIndex, Toast.LENGTH_SHORT).show();

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            loadSong();
        }

        textView.setText(songList.get(currentIndex));


        playSong();

        mediaPlayer.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(android.media.MediaPlayer mp) {
                playNextSong();
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // ...

                // Ustaw maksymalną wartość SeekBar na długość utworu w milisekundach
                seekBar.setMax(mediaPlayer.getDuration());

                // ...

                // Aktualizuj pozycję SeekBar cyklicznie
                updateSeekBar();
                // Piosenka jest gotowa do odtworzenia
                // Ustaw aktualny czas odtwarzania na początku
                mediaPlayer.seekTo((int) currentTime);
                mediaPlayer.start();
                songData.setCurrentSeekBarProgress(seekBar.getProgress());
            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Zaktualizuj wartość currentTime na podstawie postępu seekBara
                    currentTime = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Zatrzymaj odtwarzanie piosenki podczas przesuwania seekBara
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Wznów odtwarzanie piosenki od wybranego momentu
                mediaPlayer.seekTo((int) currentTime);
                mediaPlayer.start();
                songData.setCurrentSeekBarProgress(seekBar.getProgress());
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    play.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
                    isPlaying = false;
                } else {
                    try {
                        if (mediaPlayer.getCurrentPosition() == 0) {
                            mediaPlayer.reset();
                            String songTitle = songList.get(currentIndex);
                            String pathToFile = songMap.get(songTitle);
                            mediaPlayer.setDataSource(pathToFile);
                            mediaPlayer.prepare();
                        }
                        mediaPlayer.start();
                        play.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        isPlaying = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    play.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
                }
                currentIndex++;
                if (currentIndex >= songList.size()) {
                    currentIndex = 0;
                }
                textView.setText(songList.get(currentIndex));
                seekBar.setProgress(0); // Resetuj seekBar do początkowej wartości
                currentTime = 0; // Resetuj currentTime do 0
                playSong();
                resetSeekBar();
                isPlaying = true; // Ustaw flagę odtwarzania na true
                play.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
                songData.setCurrentIndex(currentIndex);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    play.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
                }
                currentIndex--;
                if (currentIndex < 0) {
                    currentIndex = songList.size() - 1;
                }
                textView.setText(songList.get(currentIndex));
                seekBar.setProgress(0); // Resetuj seekBar do początkowej wartości
                currentTime = 0; // Resetuj currentTime do 0
                playSong();
                resetSeekBar();
                isPlaying = true; // Ustaw flagę odtwarzania na true
                play.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
                songData.setCurrentIndex(currentIndex);
            }
        });


        songData.setCurrentSeekBarProgress(seekBar.getProgress());
        songData.setCurrentIndex(currentIndex);

    }

    private void loadSong() {
        int liczba = 0;
        songMap = new HashMap<>();
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentPath = songCursor.getString(songPath);
                songList.add(currentTitle);
                songMap.put(currentTitle, currentPath); // Dodanie wpisu do mapy
                liczba++;
            } while (songCursor.moveToNext());
        }

        // Sortuj listę utworów
        Collections.sort(songList);

        //arrayAdapterSong.notifyDataSetChanged(); // zaktualizuj listę
        //Toast.makeText(this, "liczba znalezionych utworów "+liczba, Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSong();
            } else {
                Toast.makeText(this, "Brak uprawnień do odczytu plików multimedialnych.", Toast.LENGTH_SHORT).show();
                finish(); // Zakończ aktywność, jeśli brak uprawnień
            }
        }
    }

    private void playNextSong() {
        currentIndex++;
        if (currentIndex >= songList.size()) {
            currentIndex = 0;
        }
        textView.setText(songList.get(currentIndex));
        playSong();
    }

    private void playSong() {
        try {
            mediaPlayer.reset();
            String songTitle = songList.get(currentIndex);
            String pathToFile = songMap.get(songTitle);
            mediaPlayer.setDataSource(pathToFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentTime = 0; // Ustaw currentTime na 0 po zresetowaniu odtwarzacza
            isPlaying = true; // Ustaw flagę odtwarzania na true
            play.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateSeekBar() {
        if (mediaPlayer != null && !isSeeking) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(currentPosition);

            // Wywołaj metodę rekurencyjnie po pewnym czasie (np. co 100ms)
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            }, 100);
        }
    }
    private void resetSeekBar() {
        seekBar.setProgress(0);
    }


    /*
        private void saveCurrentSongInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("songIndex", currentIndex);
        editor.putBoolean("isPlaying", mediaPlayer.isPlaying());
        editor.putInt("currentTime", mediaPlayer.getCurrentPosition());
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCurrentSongInfo();
    }
     */
}