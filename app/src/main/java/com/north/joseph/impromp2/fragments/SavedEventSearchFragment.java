package com.north.joseph.impromp2.fragments;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.items.Event;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.HashSet;

/**
 * Created by Joe on 1/27/2015.
 */
public class SavedEventSearchFragment extends EventSearchFragment {
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
                        for (int i : selectedPositions) {
                            Event event = (Event) getListAdapter().getItem(i);
                            eventRelation.remove(event);
                        }

                        try {
                            ParseUser.getCurrentUser().save();
                        } catch (ParseException e) {
                            // Do nothing.
                        }
                    }
                    deletePressed = true;
                    mode.finish();
                    return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if (selectedPositions.size() > 0 && deletePressed)
                    ((ParseQueryAdapter<Event>) getListAdapter()).loadObjects();
            }
        });
    }

    @Override
    public ParseQuery<Event> getParseQuery() {
        ParseUser user = ParseUser.getCurrentUser();

        ParseQuery<Event> eventQuery;

        if (user != null) {
            eventQuery = super.getParseQuery();

            ParseRelation<Event> eventRelation = user.getRelation("events");

            ParseQuery<Event> relationQuery = eventRelation.getQuery();

            eventQuery.whereMatchesKeyInQuery("objectId", "objectId", relationQuery);
        } else {
            eventQuery = ParseQuery.getQuery(Event.class);
            eventQuery.whereEqualTo("objectId", null);
        }

        return eventQuery;
    }
}
