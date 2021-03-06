package com.north.joseph.impromp2.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.adapters.FilterAdapter;
import com.north.joseph.impromp2.fragments.EventSearchFragment;
import com.north.joseph.impromp2.fragments.SavedEventSearchFragment;
import com.north.joseph.impromp2.fragments.SortDialogFragment;
import com.north.joseph.impromp2.interfaces.Locatable;
import com.north.joseph.impromp2.interfaces.PersistableChoice;
import com.north.joseph.impromp2.interfaces.Queryable;
import com.north.joseph.impromp2.items.Event;

public class MainActivity extends FragmentActivity implements EventSearchFragment.OnFragmentInteractionListener,
        Queryable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        Locatable {
    private ViewPager mViewPager;

    private MenuItem mSearchMenuItem;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    public static final int BROWSE_EVENTS_FRAGMENT = 0;
    public static final int SAVED_EVENTS_FRAGMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EventFragmentPagerAdapter mEventFragmentAdapter = new EventFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mEventFragmentAdapter);

        if (savedInstanceState == null) {
            mLocation = null;
        } else {
            mLocation = savedInstanceState.getParcelable(Locatable.LAST_LOCATION_KEY);
        }

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(Locatable.LAST_LOCATION_KEY, mLocation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchMenuItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + mViewPager.getCurrentItem());
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final Intent searchIntent = new Intent(this, SearchResultActivity.class);
            final EventSearchFragment currentFragment = (EventSearchFragment) getCurrentFragment();
            searchIntent.putExtra(SearchManager.QUERY, intent.getStringExtra(SearchManager.QUERY));
            searchIntent.putExtra(FilterAdapter.CHECKED_FILTERS, currentFragment.getFilterOptions());
            searchIntent.putExtra(PersistableChoice.SORTING_KEY, currentFragment.getLastSortingChoice());
            searchIntent.putExtra(EventSearchFragment.FRAGMENT_KEY, mViewPager.getCurrentItem());
            startActivityForResult(searchIntent, 2);
        } else {
            super.onNewIntent(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort) {
            mSearchMenuItem.collapseActionView();
            SortDialogFragment sortDialogFragment = new SortDialogFragment();
            sortDialogFragment.show(getSupportFragmentManager(), getString(R.string.sort_dialog_title));
            return true;
        } else if (id == R.id.filter) {
            mSearchMenuItem.collapseActionView();
            Intent intent = new Intent(this, FilterActivity.class);
            intent.putExtra(FilterAdapter.CHECKED_FILTERS, ((EventSearchFragment) getCurrentFragment()).getFilterOptions());
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                final EventSearchFragment currentFragment = (EventSearchFragment) getCurrentFragment();
                currentFragment.setFilterOptions(data.getBooleanArrayExtra(FilterAdapter.CHECKED_FILTERS));
                currentFragment.loadObjects();
            }
        } else if (requestCode == 1 || requestCode == 2) {
            if (resultCode == RESULT_OK) {
                ((EventSearchFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":1")).loadObjects();
            }
        }

        mSearchMenuItem.collapseActionView();
    }

    public void onFragmentInteraction(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event", event);
        startActivityForResult(intent, 1);
    }

    public String getQuery() {
        return null;
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

    public class EventFragmentPagerAdapter extends FragmentPagerAdapter {
        public EventFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == BROWSE_EVENTS_FRAGMENT)
                return new EventSearchFragment();

            return new SavedEventSearchFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == BROWSE_EVENTS_FRAGMENT)
                return "Event Search";

            return "Saved Events";
        }
    }
}
