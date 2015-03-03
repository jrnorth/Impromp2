package com.north.joseph.impromp2.fragments;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.adapters.EventListAdapter;
import com.north.joseph.impromp2.items.Event;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Joe on 1/27/2015.
 */
public class SavedEventSearchFragment extends EventSearchFragment {
    private boolean mWereEventsDeleted = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private HashSet<Integer> selectedPositions;
            private boolean deletePressed;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = getListView().getCheckedItemCount();
                mode.setTitle(checkedCount + " selected");

                if (checked)
                    selectedPositions.add(position);
                else
                    selectedPositions.remove(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                selectedPositions = new HashSet<>();
                mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.delete) {
                    if (selectedPositions.size() > 0) {
                        ParseRelation<Event> eventRelation = ParseUser.getCurrentUser().getRelation("events");
                        List<Event> eventsToUnpin = new LinkedList<>();
                        for (int i : selectedPositions) {
                            final Event event = (Event) getListAdapter().getItem(i);
                            eventRelation.remove(event);
                            eventsToUnpin.add(event);
                        }

                        // Unpin the deleted events from the local datastore.
                        Event.unpinAllInBackground(eventsToUnpin);
                        // Update the relation on the ParseUser in the local datastore and on the server.
                        ParseUser.getCurrentUser().saveEventually();
                    }
                    deletePressed = true;
                    mode.finish();
                    return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if (selectedPositions.size() > 0 && deletePressed) {
                    mWereEventsDeleted = true;
                    ((ParseQueryAdapter<Event>) getListAdapter()).loadObjects();
                }
            }
        });
    }

    public boolean wereEventsDeleted() {
        return mWereEventsDeleted;
    }

    @Override
    public ParseQuery<Event> getParseQuery() {
        final ParseUser user = ParseUser.getCurrentUser();

        ParseQuery<Event> relationQuery;

        if (user != null) {
            ParseRelation<Event> eventRelation = user.getRelation("events");

            relationQuery = eventRelation.getQuery();

            // If the user's saved events have been loaded, query from the local datastore.
            if (user.getBoolean(EventListAdapter.USER_SAVED_EVENTS_LOADED)) {
                relationQuery.fromLocalDatastore();
            }

            buildParseQuery(relationQuery);
        } else {
            relationQuery = ParseQuery.getQuery(Event.class);
            relationQuery.whereEqualTo("objectId", null);
        }

        return relationQuery;
    }

    @Override
    protected void fetchEvents() {
        mListAdapter = new EventListAdapter(getActivity(), this);

        mListAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Event>() {
            @Override
            public void onLoading() {
                if (ParseUser.getCurrentUser() == null)
                    setEmptyText("You are not logged in. To save events, you must log in or sign up for an account. Tap the star on any event page to get started.");
                else
                    setEmptyText("No events found.");
                setListShown(false);
            }

            @Override
            public void onLoaded(final List<Event> events, Exception e) {
                final ParseUser parseUser = ParseUser.getCurrentUser();
                if (parseUser != null && !ParseUser.getCurrentUser().getBoolean(EventListAdapter.USER_SAVED_EVENTS_LOADED) && e == null) {
                    // This is the first time the user's saved events have been loaded from the server since logging in, so we need to pin them to the background.
                    Event.unpinAllInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            Event.pinAllInBackground(events);
                        }
                    });

                    ParseRelation<Event> relation = parseUser.getRelation("events");
                    // Why do I have to add Events back to the ParseRelation manually?
                    for (Event ev : events) {
                        relation.add(ev);
                    }

                    // Indicate that the user's data has been loaded from the server.
                    parseUser.put(EventListAdapter.USER_SAVED_EVENTS_LOADED, true);
                }

                if (e == null)
                    // Refresh the events, since they probably have been retrieved from the local datastore and the information on the server may have changed.
                    Event.fetchAllIfNeededInBackground(events);

                setListShown(true);
            }
        });

        setListAdapter(mListAdapter);

        setListShown(false);
    }
}
