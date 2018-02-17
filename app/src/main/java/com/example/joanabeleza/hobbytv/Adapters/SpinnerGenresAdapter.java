package com.example.joanabeleza.hobbytv.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.joanabeleza.hobbytv.Models.SpinnerGenreItem;
import com.example.joanabeleza.hobbytv.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Project HobbyTV refactored by joanabeleza on 14/02/2018.
 */

public class SpinnerGenresAdapter extends ArrayAdapter<SpinnerGenreItem> {

    private Context mContext;
    private ArrayList<SpinnerGenreItem> genreItems;
    private SpinnerGenresAdapter adapter;
    private boolean isFromView = false;

    public SpinnerGenresAdapter(Context context, int resource, List<SpinnerGenreItem> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.genreItems = (ArrayList<SpinnerGenreItem>) objects;
        this.adapter = this;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = convertView
                    .findViewById(R.id.text);
            holder.mCheckBox = convertView
                    .findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(genreItems.get(position).getName());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(genreItems.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                genreItems.get(position).setSelected(holder.mCheckBox.isChecked());
            }
        });
        return convertView;
    }

    public ArrayList<SpinnerGenreItem> getItems() {
        return genreItems;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}
