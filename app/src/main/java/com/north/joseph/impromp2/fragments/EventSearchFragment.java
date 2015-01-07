package com.north.joseph.impromp2.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.north.joseph.impromp2.interfaces.PersistableChoice;
import com.north.joseph.impromp2.items.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class EventSearchFragment extends ListFragment
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        PersistableChoice {
    private EventListAdapter mListAdapter;

    private ParseQuery<Event> query;
    private List<Event> mEvents = null;
    private boolean queryInProgress = false;

    private String mQuery = null;

    private OnFragmentInteractionListener mListener;

    private GoogleApiClient mGoogleApiClient;
    private static boolean mGoogleApiConnected = false;

    private int mLastSortingChoice = 0;

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
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mQuery = args.getString("query");
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fetchEvents(mQuery, false, "");
    }

    public void fetchEvents(String queryStr, boolean refetch, String sortBy) {
        Log.d("fetchEvents", "fetching events...");
        if ((mEvents == null && !queryInProgress) || refetch) {
            Log.d("onActivityCreated", "saved state not null");
            queryInProgress = true;
            setEmptyText("No events");

            mListAdapter = new EventListAdapter(getActivity(), new ArrayList<Event>());
            setListAdapter(mListAdapter);

            setListShown(false);

            query = ParseQuery.getQuery(Event.class);

            if (queryStr != null) {
                List<String> queryWords = Arrays.asList(queryStr.split("\\s+"));
                query.whereContainsAll("searchable_words", queryWords);
            }

            if ("distance".equals(sortBy)) {
                if (!mGoogleApiConnected) {
                    Toast toast = Toast.makeText(getActivity(), "GPS not connected", Toast.LENGTH_SHORT);
                    toast.show();
                    setListShown(true);
                    return;
                } else {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    query.whereNear("geo_point", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                }
            } else {
                query.addAscendingOrder(sortBy);
            }

            query.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> eventList, ParseException e) {
                    if (e == null) {
                        mListAdapter.clear();
                        mListAdapter.addAll(eventList);
                        mEvents = eventList;
                        queryInProgress = false;
                    }
                    setListShown(true);
                }
            });
        }
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

    @Override
    public void onConnected(Bundle bundle) {
        mGoogleApiConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mGoogleApiConnected = false;
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
