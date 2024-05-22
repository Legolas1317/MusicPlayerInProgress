package com.example.muzyka;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LoadSong {
    private Context context;

    public LoadSong(Context context) {
        this.context = context;
    }

    public ArrayList<Song> loadSong() {
        ArrayList<Song> songList = new ArrayList<>();
        int liczba = 0;
        HashMap<String, String> songMap = new HashMap<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentPath = songCursor.getString(songPath);

                songMap.put(currentTitle, currentPath);

                Song song = new Song(currentTitle, currentPath, liczba);
                songList.add(song);

                liczba++;
            } while (songCursor.moveToNext());
        }

        if (songCursor != null) {
            songCursor.close();
        }

        Toast.makeText(context, "Liczba znalezionych utworów: " + liczba, Toast.LENGTH_SHORT).show();

        return songList;
    }
}
