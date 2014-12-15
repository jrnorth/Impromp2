package com.north.joseph.impromp2.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.north.joseph.impromp2.items.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
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
public class EventSearchFragment extends ListFragment {
    private EventListAdapter mListAdapter;

    private ParseQuery<Event> query;
    private List<Event> mEvents = null;
    private boolean queryInProgress = false;

    private OnFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventSearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("onActivityCreated", "fragment activity created");

        fetchEvents(null);
    }

    public void fetchEvents(String queryStr) {
        Log.d("fetchEvents", "fetching events...");
        if (queryStr != null || (mEvents == null && !queryInProgress)) {
            Log.d("onActivityCreated", "saved state not null");
            queryInProgress = true;
            setEmptyText("No events");

            setHasOptionsMenu(true);
            setRetainInstance(true);

            mListAdapter = new EventListAdapter(getActivity(), new ArrayList<Event>());
            setListAdapter(mListAdapter);

            setListShown(false);

            query = ParseQuery.getQuery(Event.class);

            if (queryStr != null) {
                List<String> queryWords = Arrays.asList(queryStr.split("\\s+"));
                query.whereContainsAll("searchable_words", queryWords);
            }

            query.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> eventList, ParseException e) {
                    if (e == null) {
                        mListAdapter.clear();
                        mListAdapter.addAll(eventList);
                        mEvents = eventList;
                        queryInProgress = false;
                    } else {
                        mListAdapter.addAll(new ArrayList<Event>());
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
        public void onFragmentInteraction(String id);
    }

}
