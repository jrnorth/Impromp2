package com.north.joseph.impromp2.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.adapters.EventListAdapter;
import com.north.joseph.impromp2.interfaces.Filterable;
import com.north.joseph.impromp2.interfaces.Locatable;
import com.north.joseph.impromp2.interfaces.PersistableChoice;
import com.north.joseph.impromp2.interfaces.Queryable;
import com.north.joseph.impromp2.items.Event;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class EventSearchFragment extends ListFragment
        implements PersistableChoice {
    protected EventListAdapter mListAdapter;

    protected OnFragmentInteractionListener mListener;

    protected int mLastSortingChoice = 0;

    protected static List<String> FILTER_OPTIONS = null;

    public static final String FRAGMENT_KEY = "fk";

    private static final List<String> STOPWORDS = Arrays.asList("the", "and", "or");

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventSearchFragment() {
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fetchEvents();
    }

    public void loadObjects() {
        mListAdapter.loadObjects();
    }

    public ParseQuery<Event> getParseQuery() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        String queryStr = ((Queryable) mListener).getQuery();

        if (queryStr != null) {
            LinkedList<String> queryWords = new LinkedList<>(Arrays.asList(queryStr.toLowerCase().split("\\s+")));
            queryWords.removeAll(STOPWORDS);
            if (queryWords.size() > 0)
                query.whereContainsAll("searchable_words", queryWords);
        }

        if (getLastSortingChoice() == 1) { // Distance
            Location location = ((Locatable) mListener).getLocation();
            if (location == null) {
                Toast toast = Toast.makeText(getActivity(), "GPS not connected", Toast.LENGTH_LONG);
                toast.show();
            } else {
                query.whereNear("geo_point", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
            }
        } else if (getLastSortingChoice() == 2) {
            query.addAscendingOrder("start_time");
        }

        final boolean[] filterOptions = ((Filterable) mListener).getFilterOptions();
        if (filterOptions != null) {
            if (filterOptions[0])
                query.whereEqualTo("free", Boolean.TRUE);

            LinkedList<String> queryFilters = new LinkedList<>();
            for (int i = 1; i < filterOptions.length; ++i) {
                if (filterOptions[i])
                    queryFilters.add(FILTER_OPTIONS.get(i - 1));
            }
            if (!queryFilters.isEmpty())
                query.whereContainedIn("category", queryFilters);
        }

        return query;
    }

    protected void fetchEvents() {
        setEmptyText("No events found.");

        mListAdapter = new EventListAdapter(getActivity(), this);

        mListAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Event>() {
            @Override
            public void onLoading() {
                setListShown(false);
            }

            @Override
            public void onLoaded(List<Event> events, Exception e) {
                setListShown(true);
            }
        });

        setListAdapter(mListAdapter);

        setListShown(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if (FILTER_OPTIONS == null) {
            String[] filterOptions = getResources().getStringArray(R.array.filter_options);
            FILTER_OPTIONS = Arrays.asList(filterOptions);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mListener.onFragmentInteraction(mListAdapter.getItem(position));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Event event);
    }

    public int getLastSortingChoice() {
        return mLastSortingChoice;
    }

    public void setSortingChoice(int newChoice) {
        mLastSortingChoice = newChoice;
    }
}
