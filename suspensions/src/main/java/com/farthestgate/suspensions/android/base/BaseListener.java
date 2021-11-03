package com.farthestgate.suspensions.android.base;


/**
 *
 * Created by Suraj on 16/09/16.
 */
public interface BaseListener {
    /**
     * Method to return the activities layout
     */
    int getLayout();

    /**
     * Method to initialize ui parameters and will be called just after setContentView Method in any activity
     */
    void initializeUi();

}
