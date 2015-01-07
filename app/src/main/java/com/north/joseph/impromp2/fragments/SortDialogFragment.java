package com.north.joseph.impromp2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.north.joseph.impromp2.R;
import com.north.joseph.impromp2.interfaces.Queryable;

/**
 * A simple {@link Fragment} subclass.
 */
public class SortDialogFragment extends DialogFragment {
    private EventSearchFragment mEventSearchFragment;
    private Queryable mQueryable;

    public SortDialogFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    public void setUp(EventSearchFragment e, Queryable q) {
        mEventSearchFragment = e;
        mQueryable = q;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.sort_dialog_title)
                .setItems(R.array.sort_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
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
                }).create();
    }
}
