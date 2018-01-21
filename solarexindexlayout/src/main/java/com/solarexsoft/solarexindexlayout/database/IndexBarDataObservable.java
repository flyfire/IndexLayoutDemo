package com.solarexsoft.solarexindexlayout.database;

import android.database.Observable;

/**
 * Created by houruhou on 21/01/2018.
 */

public class IndexBarDataObservable extends Observable<IndexBarDataObserver> {
    public void notifyChanged() {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i>=0; i-- ){
                mObservers.get(i).onChanged();;
            }
        }
    }
}
