package com.example.muzyka;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {
    private int textColor;
    private int backgroundColor;

    public CustomAdapter(Context context, int resource, List<String> items, int textColor, int backgroundColor) {
        super(context, resource, items);
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setTextColor(textColor);
        view.setBackgroundColor(backgroundColor);
        return view;
    }
}
