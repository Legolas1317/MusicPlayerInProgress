package com.example.muzyka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> arrayListSong;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapterSong;
    private ArrayList<String> fullSongList; // Nowa zmienna do przechowywania pełnej listy piosenek
    private ArrayList<Song> songArrayList;
    private EditText searchTextView;
    public static final String EXTRA_SONG_LIST = "com.example.muzyka.EXTRA_SONG_LIST";
    public static final String EXTRA_SELECTED_SONG_INDEX = "com.example.muzyka.EXTRA_SELECTED_SONG_INDEX";
    private HashMap<String, String> songMap;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        arrayListSong = new ArrayList<>();
        fullSongList = new ArrayList<>();
        songArrayList = new ArrayList<>();
        searchTextView = findViewById(R.id.searchTextView);

        arrayAdapterSong = new ArrayAdapter<>(getApplicationContext(), R.layout.text_color, arrayListSong);
        listView.setAdapter(arrayAdapterSong);

        searchTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String searchText = searchTextView.getText().toString().trim();
                    if (searchText.isEmpty()) {
                        filterSongs("");
                    }
                }
            }
        });

        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nie jest potrzebne
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = charSequence.toString().trim();
                filterSongs(searchText);
                Toast.makeText(MainActivity.this,"Wykryto zmianę tekstu",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();
                filterSongs(searchText);
            }
        });

        searchTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL && searchTextView.getText().toString().isEmpty()) {
                    filterSongs("");
                    return true;
                }
                return false;
            }
        });


// ...


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, SongPlayerActivity.class);
                currentIndex = i;
                intent.putExtra("index", currentIndex);
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                loadSong();
            }
        } else {
            loadSong();
        }
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

                arrayListSong.add(currentTitle);
                fullSongList.add(currentTitle); // Dodaj piosenkę do pełnej listy
                songMap.put(currentTitle, currentPath);

                Song song = new Song(currentTitle,currentPath,liczba);
                songArrayList.add(song);

                liczba++;
            } while (songCursor.moveToNext());
        }

        Collections.sort(arrayListSong);

        arrayAdapterSong.notifyDataSetChanged();
        Toast.makeText(this, "Liczba znalezionych utworów: " + liczba, Toast.LENGTH_SHORT).show();
    }


    private void filterSongs(String searchText) {
        if (searchText.equals("")) {
            arrayAdapterSong.clear();
            arrayAdapterSong.addAll(fullSongList); // Użyj pełnej listy do wyświetlenia wszystkich piosenek
            arrayAdapterSong.notifyDataSetChanged();
        } else {
            ArrayList<String> filteredSongs = new ArrayList<>();
            String searchTextLowerCase = searchText.toLowerCase();
            for (String song : fullSongList) { // Użyj pełnej listy do filtrowania
                if (song.toLowerCase().contains(searchTextLowerCase)) {
                    filteredSongs.add(song);
                }
            }
            arrayAdapterSong.clear();
            arrayAdapterSong.addAll(filteredSongs);
            arrayAdapterSong.notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PlayerFragment playerFragment = new PlayerFragment();
        fragmentTransaction.replace(R.id.fragmentContainer, playerFragment);
        fragmentTransaction.commit();
    }

}
