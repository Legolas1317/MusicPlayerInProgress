package com.example.muzyka;

import java.util.ArrayList;

public class Song {
    private String _title;
    private String _path;
    private int _index;
    private boolean _ulubiona;
    private ArrayList<Song> songArrayList;


    public Song(String title, String path, int index) {
        this._title = title;
        this._path = path;
        this._index = index;
        this._ulubiona = false;
    }


    public String getTitle() {
        return _title;
    }

    public String getPath() {
        return _path;
    }

    public int getIndex() { return  _index; }

    public boolean getUlubiona() { return _ulubiona; }

    public ArrayList loadSong(){

        int index;




        return songArrayList;
    }


}

