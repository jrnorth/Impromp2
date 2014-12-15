package com.north.joseph.impromp2.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.north.joseph.impromp2.R;

/**
 * Created by Joe on 12/15/2014.
 */
public class ViewHolder {
    public ImageView mImageView;
    public TextView mNameTextView;
    public TextView mTimeTextView;
    public TextView mLocationTextView;

    public ViewHolder(View row) {
        this.mImageView = (ImageView) row.findViewById(R.id.event_imageView);
        this.mNameTextView = (TextView) row.findViewById(R.id.event_nameTextView);
        this.mTimeTextView = (TextView) row.findViewById(R.id.event_timeTextView);
        this.mLocationTextView = (TextView) row.findViewById(R.id.event_locationTextView);
    }
}
