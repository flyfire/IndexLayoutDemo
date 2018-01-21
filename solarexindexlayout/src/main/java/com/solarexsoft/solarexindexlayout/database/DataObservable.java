package com.solarexsoft.solarexindexlayout.database;

import android.database.Observable;
import android.util.Log;

/**
 * Created by houruhou on 19/01/2018.
 */

public class DataObservable extends Observable<DataObserver> {
    public static final String TAG = DataObservable.class.getSimpleName();

    public void notifyInited() {
        Log.d(TAG, "notifyInited");
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onInited();
            }
        }
    }

    public void notifyChanged() {
        Log.d(TAG, "notifychanged");
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

    public void notifySetListener(int type) {
        Log.d(TAG, "notifySetListener type = " + type);
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onSetListener(type);
            }
        }
    }
}
