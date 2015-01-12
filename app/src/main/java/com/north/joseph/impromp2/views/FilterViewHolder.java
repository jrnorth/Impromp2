package com.north.joseph.impromp2.views;

import android.graphics.Color;
import android.view.View;
import android.widget.CheckedTextView;

/**
 * Created by Joe on 1/8/2015.
 */
public class FilterViewHolder {
    public CheckedTextView mCheckedTextView;

    public FilterViewHolder(View row) {
        mCheckedTextView = (CheckedTextView) row.findViewById(android.R.id.text1);
        mCheckedTextView.setTextColor(Color.BLACK);
    }
}
