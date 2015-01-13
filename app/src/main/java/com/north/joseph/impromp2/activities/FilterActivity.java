package com.north.joseph.impromp2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.adapters.FilterAdapter;

public class FilterActivity extends Activity {
    private FilterAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        final ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.filter_expandableListView);

        if (savedInstanceState != null) {
            final boolean[] checked = savedInstanceState.getBooleanArray(FilterAdapter.CHECKED_FILTERS);
            mListAdapter = new FilterAdapter(getApplicationContext(), checked);
        } else {
            Intent intent = getIntent();
            final boolean[] checked = intent.getBooleanArrayExtra(FilterAdapter.CHECKED_FILTERS);
            if (checked != null)
                mListAdapter = new FilterAdapter(getApplicationContext(), checked);
            else
                mListAdapter = new FilterAdapter(getApplicationContext());
        }

        expandableListView.setAdapter(mListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra(FilterAdapter.CHECKED_FILTERS, mListAdapter.getChecked());
            setResult(RESULT_OK, intent);
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBooleanArray(FilterAdapter.CHECKED_FILTERS, mListAdapter.getChecked());
    }
}
