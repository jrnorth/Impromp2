package com.north.joseph.impromp2.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.adapters.FilterAdapter;
import com.north.joseph.impromp2.fragments.EventSearchFragment;
import com.north.joseph.impromp2.fragments.SavedEventSearchFragment;
import com.north.joseph.impromp2.fragments.SortDialogFragment;
import com.north.joseph.impromp2.interfaces.Filterable;
import com.north.joseph.impromp2.interfaces.Locatable;
import com.north.joseph.impromp2.interfaces.PersistableChoice;
import com.north.joseph.impromp2.interfaces.Queryable;
import com.north.joseph.impromp2.items.Event;

public class SearchResultActivity extends FragmentActivity implements EventSearchFragment.OnFragmentInteractionListener,
        Filterable, Queryable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Locatable {
    private static EventSearchFragment mEventSearchFragment;
    private static String mQuery;
    private boolean[] mCheckedFilters;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mLocation = null;

            Intent intent = getIntent();
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                mQuery = intent.getStringExtra(SearchManager.QUERY);
            }

            if (intent.getIntExtra(EventSearchFragment.FRAGMENT_KEY, MainActivity.BROWSE_EVENTS_FRAGMENT) == MainActivity.BROWSE_EVENTS_FRAGMENT)
                mEventSearchFragment = new EventSearchFragment();
            else
                mEventSearchFragment = new SavedEventSearchFragment();

            mCheckedFilters = intent.getBooleanArrayExtra(FilterAdapter.CHECKED_FILTERS);

            mEventSearchFragment.setSortingChoice(intent.getIntExtra(PersistableChoice.SORTING_KEY, 0));

            getSupportFragmentManager().beginTransaction().add(android.R.id.content, mEventSearchFragment).commit();
        } else {
            mCheckedFilters = savedInstanceState.getBooleanArray(FilterAdapter.CHECKED_FILTERS);
            mLocation = savedInstanceState.getParcelable(Locatable.LAST_LOCATION_KEY);
        }

        setTitle("results for \"" + mQuery + "\"");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
                mEventSearchFragment.loadObjects();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBooleanArray(FilterAdapter.CHECKED_FILTERS, mCheckedFilters);
        outState.putParcelable(Locatable.LAST_LOCATION_KEY, mLocation);
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

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public Location getLocation() {
        if (mGoogleApiClient.isConnected())
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        return mLocation;
    }
}
