package com.north.joseph.impromp2.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.fragments.EventSearchFragment;
import com.north.joseph.impromp2.fragments.SortDialogFragment;
import com.north.joseph.impromp2.interfaces.Queryable;
import com.north.joseph.impromp2.items.Event;

public class MainActivity extends FragmentActivity implements EventSearchFragment.OnFragmentInteractionListener,
        Queryable {
    private ViewPager mViewPager;
    private EventFragmentPagerAdapter mEventFragmentAdapter;

    private static EventSearchFragment mEventSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEventFragmentAdapter = new EventFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mEventFragmentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + mViewPager.getCurrentItem());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort) {
            SortDialogFragment sortDialogFragment = new SortDialogFragment();
            sortDialogFragment.show(getSupportFragmentManager(), getString(R.string.sort_dialog_title));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    public String getQuery() {
        return null;
    }

    public class EventFragmentPagerAdapter extends FragmentPagerAdapter {
        public EventFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return new EventSearchFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Event Search";

            return "Saved Events";
        }
    }
}
