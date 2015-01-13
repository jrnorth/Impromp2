package com.north.joseph.impromp2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.adapters.FilterAdapter;

import java.util.Arrays;

public class FilterActivity extends Activity {
    private static final String ORIGINAL_CHECKED_FILTERS = "ocf";
    private FilterAdapter mListAdapter;
    private boolean[] mOriginalChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        final ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.filter_expandableListView);

        if (savedInstanceState != null) {
            final boolean[] checked = savedInstanceState.getBooleanArray(FilterAdapter.CHECKED_FILTERS);
            mListAdapter = new FilterAdapter(getApplicationContext(), this, checked);
            mOriginalChecked = savedInstanceState.getBooleanArray(ORIGINAL_CHECKED_FILTERS);
        } else {
            Intent intent = getIntent();
            boolean[] checked = intent.getBooleanArrayExtra(FilterAdapter.CHECKED_FILTERS);
            if (checked != null) {
                mListAdapter = new FilterAdapter(getApplicationContext(), this, checked);
            } else {
                mListAdapter = new FilterAdapter(getApplicationContext(), this);
                checked = mListAdapter.getChecked();
            }
            mOriginalChecked = Arrays.copyOf(checked, checked.length);
        }

        filterToggled();

        expandableListView.setAdapter(mListAdapter);
    }

    private boolean didFiltersChange() {
        return !Arrays.equals(mListAdapter.getChecked(), mOriginalChecked);
    }

    public void filterToggled() {
        if (didFiltersChange())
            setTitle(getResources().getString(R.string.title_activity_filter));
        else
            setTitle("Back to events");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra(FilterAdapter.CHECKED_FILTERS, mListAdapter.getChecked());

            if (didFiltersChange())
                setResult(RESULT_OK, intent);
            else
                setResult(RESULT_CANCELED);

            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBooleanArray(FilterAdapter.CHECKED_FILTERS, mListAdapter.getChecked());
        outState.putBooleanArray(ORIGINAL_CHECKED_FILTERS, mOriginalChecked);
    }
}
