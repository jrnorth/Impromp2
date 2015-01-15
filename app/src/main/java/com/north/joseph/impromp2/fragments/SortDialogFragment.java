package com.north.joseph.impromp2.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.activities.MainActivity;
import com.north.joseph.impromp2.activities.SearchResultActivity;
import com.north.joseph.impromp2.interfaces.PersistableChoice;
import com.north.joseph.impromp2.interfaces.Queryable;

/**
 * A simple {@link Fragment} subclass.
 */
public class SortDialogFragment extends DialogFragment {
    private EventSearchFragment mEventSearchFragment;
    private Queryable mQueryable;
    private PersistableChoice mPersistableChoice;
    private int mSelectedItem;

    public SortDialogFragment() {
        // Required empty public constructor
    }

    private void prepareFragment() {
        Activity activity = getActivity();

        mQueryable = (Queryable) activity;

        if (activity instanceof MainActivity)
            mEventSearchFragment = (EventSearchFragment) ((MainActivity) activity).getCurrentFragment();
        else if (activity instanceof SearchResultActivity)
            mEventSearchFragment = (EventSearchFragment)
                    ((FragmentActivity) activity).getSupportFragmentManager().findFragmentById(android.R.id.content);

        mPersistableChoice = mEventSearchFragment;

        mSelectedItem = mPersistableChoice.getLastSortingChoice();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        prepareFragment();
        // Set the dialog title
        builder.setTitle(R.string.sort_dialog_title)
            // Specify the list array, the items to be selected by default (null for none),
            // and the listener through which to receive callbacks when items are selected
            .setSingleChoiceItems(R.array.sort_options, mPersistableChoice.getLastSortingChoice(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSelectedItem = which;
                        }
                    })
                    // Set the action buttons
            .setPositiveButton("Sort", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if (mSelectedItem == mPersistableChoice.getLastSortingChoice())
                        return;

                    mPersistableChoice.setSortingChoice(mSelectedItem);

                    switch (mSelectedItem) {
                        case 0:
                            mEventSearchFragment.fetchEvents(mQueryable.getQuery(), true, "");
                            break;
                        case 1:
                            mEventSearchFragment.fetchEvents(mQueryable.getQuery(), true, "distance");
                            break;
                        case 2:
                            mEventSearchFragment.fetchEvents(mQueryable.getQuery(), true, "start_time");
                            break;
                    }
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // Empty.
                }
            });

        return builder.create();
    }
}
