package com.north.joseph.impromp2.interfaces;

/**
 * Created by Joe on 1/7/2015.
 */
public interface PersistableChoice {
    public static final String SORTING_KEY = "sk";

    public int getLastSortingChoice();

    public void setSortingChoice(int newChoice);
}
