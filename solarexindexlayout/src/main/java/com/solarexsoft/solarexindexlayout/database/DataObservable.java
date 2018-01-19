package com.solarexsoft.solarexindexlayout.database;

import android.database.Observable;

/**
 * Created by houruhou on 19/01/2018.
 */

public class DataObservable extends Observable<DataObserver> {
    public void notifyInited() {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onInited();
            }
        }
    }

    public void notifyChanged() {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

    public void notifySetListener(int type) {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onSetListener(type);
            }
        }
    }
}
