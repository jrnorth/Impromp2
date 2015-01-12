package com.north.joseph.impromp2.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.adapters.FilterAdapter;

public class FilterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.filter_expandableListView);
        expandableListView.setAdapter(new FilterAdapter(getApplicationContext()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
