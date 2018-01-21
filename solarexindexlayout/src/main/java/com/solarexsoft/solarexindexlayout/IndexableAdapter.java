package com.solarexsoft.solarexindexlayout;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.solarexsoft.solarexindexlayout.database.DataObservable;
import com.solarexsoft.solarexindexlayout.database.DataObserver;

import java.util.List;

/**
 * Created by houruhou on 19/01/2018.
 */

public abstract class IndexableAdapter<T extends IndexableEntity> {
    public static final String TAG = IndexableAdapter.class.getSimpleName();
    public static final int TYPE_ALL = 0;
    public static final int TYPE_CLICK_TITLE = 1;
    public static final int TYPE_CLICK_CONTENT = 2;
    private final DataObservable mDataObservable = new DataObservable();

    private List<T> mDatas;

    private IndexCallback<T> mCallback;
    private OnItemTitleClickListener mTitleClickListener;
    private OnItemContentClickListener mContentClickListener;

    public abstract RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent);

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);

    public abstract void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle);

    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity);

    public void setDatas(List<T> datas) {
        setDatas(datas, null);
    }

    public void setDatas(List<T> datas, IndexCallback<T> callback) {
        Log.d(TAG, "setDatas datas = " + datas + ",callback = " + callback);
        this.mCallback = callback;
        mDatas = datas;
        notifyInited();
    }

    public void setOnItemTitleClickListener(OnItemTitleClickListener listener) {
        this.mTitleClickListener = listener;
        notifySetListener(TYPE_CLICK_TITLE);
    }

    public void setOnItemContentClickListener(OnItemContentClickListener<T> listener) {
        this.mContentClickListener = listener;
        notifySetListener(TYPE_CLICK_CONTENT);
    }

    public void notifyDataSetChanged() {
        mDataObservable.notifyInited();
    }

    private void notifyInited() {
        mDataObservable.notifyInited();
    }

    private void notifySetListener(int type) {
        mDataObservable.notifySetListener(type);
    }

    public List<T> getItems() {
        return mDatas;
    }

    IndexCallback<T> getIndexCallback() {
        return mCallback;
    }

    OnItemTitleClickListener getOnItemTitleClickListener() {
        return mTitleClickListener;
    }

    OnItemContentClickListener getOnItemContentClickListener() {
        return mContentClickListener;
    }

    void registerDataSetObserver(DataObserver observer) {
        mDataObservable.registerObserver(observer);
    }

    void unregisterDataSetObserver(DataObserver observer) {
        mDataObservable.unregisterObserver(observer);
    }

    public interface IndexCallback<T> {
        void onFinished(List<EntityWrapper<T>> datas);
    }

    public interface OnItemTitleClickListener {
        void onItemClick(View view, int currentPosition, String indexTitle);
    }

    public interface OnItemContentClickListener<T> {
        void onItemClick(View view, int originalPosition, int currentPosition, T entity);
    }


}
