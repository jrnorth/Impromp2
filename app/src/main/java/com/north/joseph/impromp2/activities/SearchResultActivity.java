package com.north.joseph.impromp2.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.adapters.FilterAdapter;
import com.north.joseph.impromp2.fragments.EventSearchFragment;
import com.north.joseph.impromp2.fragments.SortDialogFragment;
import com.north.joseph.impromp2.interfaces.Filterable;
import com.north.joseph.impromp2.interfaces.Queryable;
import com.north.joseph.impromp2.items.Event;

public class SearchResultActivity extends FragmentActivity implements EventSearchFragment.OnFragmentInteractionListener,
        Filterable, Queryable {
    private static EventSearchFragment mEventSearchFragment;
    private static String mQuery;
    private boolean[] mCheckedFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mEventSearchFragment = new EventSearchFragment();

            Intent intent = getIntent();
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                mQuery = intent.getStringExtra(SearchManager.QUERY);
                Bundle args = new Bundle();
                args.putString("query", mQuery);
                Log.d("ACTION_SEARCH", "search");
                mEventSearchFragment.setArguments(args);
            }

            mCheckedFilters = intent.getBooleanArrayExtra(FilterAdapter.CHECKED_FILTERS);

            getSupportFragmentManager().beginTransaction().add(android.R.id.content, mEventSearchFragment).commit();
        } else {
            mCheckedFilters = savedInstanceState.getBooleanArray(FilterAdapter.CHECKED_FILTERS);
        }

        setTitle("results for \"" + mQuery + "\"");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sort) {
            SortDialogFragment sortDialogFragment = new SortDialogFragment();
            sortDialogFragment.show(getSupportFragmentManager(), getString(R.string.sort_dialog_title));
            return true;
        } else if (id == R.id.filter) {
            Intent intent = new Intent(this, FilterActivity.class);
            intent.putExtra(FilterAdapter.CHECKED_FILTERS, mCheckedFilters);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                mCheckedFilters = data.getBooleanArrayExtra(FilterAdapter.CHECKED_FILTERS);
                mEventSearchFragment.fetchEvents(mQuery, true, "");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBooleanArray(FilterAdapter.CHECKED_FILTERS, mCheckedFilters);
    }

    public void onFragmentInteraction(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    public String getQuery() {
        return mQuery;
    }

    @Override
    public boolean[] getFilterOptions() {
        return mCheckedFilters;
    }
}
