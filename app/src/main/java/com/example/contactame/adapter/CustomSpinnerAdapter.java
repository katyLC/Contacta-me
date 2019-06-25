package com.example.contactame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.contactame.R;

public class CustomSpinnerAdapter extends BaseAdapter {

    private int icons[];
    private String[] horas;
    private LayoutInflater layoutInflater;

    public CustomSpinnerAdapter(Context context, int[] icons, String[] horas) {
        this.icons = icons;
        this.horas = horas;
        layoutInflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return horas.length;
    }

    @Override
    public Object getItem(int position) {
        return horas[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.custom_spinner_item, viewGroup, false);

        ImageView icon = view.findViewById(R.id.imageView);
        TextView names = view.findViewById(R.id.textView);

        icon.setImageResource(icons[0]);
        names.setText(horas[i]);

        return view;
    }
}
