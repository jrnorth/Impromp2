package com.north.joseph.impromp2.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.views.FilterViewHolder;

/**
 * Created by Joe on 1/8/2015.
 */
public class FilterAdapter extends BaseExpandableListAdapter {
    public static final String CHECKED_FILTERS = "cf";
    private final Context mContext;
    private final String[] mCategories;
    private boolean[] mChecked;

    public FilterAdapter(Context context) {
        mContext = context;
        mCategories = context.getResources().getStringArray(R.array.filter_options);
        mChecked = new boolean[1 + mCategories.length];
    }

    public FilterAdapter(Context context, boolean[] checked) {
        mContext = context;
        mCategories = context.getResources().getStringArray(R.array.filter_options);
        mChecked = checked;
    }

    public boolean[] getChecked() {
        return mChecked;
    }

    @Override
    public int getGroupCount() {
        return 2;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 0)
            return 1;

        return mCategories.length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (groupPosition == 0)
            return "Cost";

        return "Categories";
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition == 0)
            return "Free";

        return mCategories[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(mContext).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        TextView group = (TextView) row.findViewById(android.R.id.text1);

        group.setText((String) getGroup(groupPosition));
        group.setTextColor(Color.BLACK);

        return row;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_checked, parent, false);
        }

        FilterViewHolder filterViewHolder = (FilterViewHolder) row.getTag();

        if (filterViewHolder == null) {
            filterViewHolder = new FilterViewHolder(row);
            row.setTag(filterViewHolder);
        }

        final CheckedTextView checkedTextView = filterViewHolder.mCheckedTextView;
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChecked[groupPosition + childPosition] = !mChecked[groupPosition + childPosition];
                checkedTextView.toggle();
            }
        });

        filterViewHolder.mCheckedTextView.setText((String) getChild(groupPosition, childPosition));
        filterViewHolder.mCheckedTextView.setChecked(mChecked[groupPosition + childPosition]);

        return row;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
