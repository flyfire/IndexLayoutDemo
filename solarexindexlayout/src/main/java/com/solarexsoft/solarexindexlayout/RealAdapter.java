package com.solarexsoft.solarexindexlayout;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by houruhou on 21/01/2018.
 */

public class RealAdapter<T extends IndexableEntity> extends RecyclerView.Adapter<RecyclerView
        .ViewHolder> {
    private ArrayList<EntityWrapper<T>> mDatasList = new ArrayList<>();
    private ArrayList<EntityWrapper<T>> mDatas;
    private IndexableAdapter<T> mAdapter;

    private IndexableAdapter.OnItemTitleClickListener mTitleClickListener;
    private IndexableAdapter.OnItemContentClickListener mContentClickListener;

    void setIndexableAdapter(IndexableAdapter<T> adapter) {
        this.mAdapter = adapter;
    }

    void setDatas(ArrayList<EntityWrapper<T>> datas) {
        if (mDatas != null) {
            mDatasList.removeAll(mDatas);
        }
        this.mDatas = datas;
        mDatasList.addAll(datas);
        notifyDataSetChanged();
    }

    ArrayList<EntityWrapper<T>> getItems() {
        return mDatasList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == EntityWrapper.TYPE_TITLE) {
            holder = mAdapter.onCreateTitleViewHolder(parent);
        } else if (viewType == EntityWrapper.TYPE_CONTENT) {
            holder = mAdapter.onCreateContentViewHolder(parent);
        }
        final RecyclerView.ViewHolder finalHolder = holder;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = finalHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                EntityWrapper<T> wrapper = mDatasList.get(position);
                if (viewType == EntityWrapper.TYPE_TITLE) {
                    if (mTitleClickListener != null) {
                        mTitleClickListener.onItemClick(view, position, wrapper.getIndexTitle());
                    }
                } else if (viewType == EntityWrapper.TYPE_CONTENT) {
                    if (mContentClickListener != null) {
                        mContentClickListener.onItemClick(view, wrapper.getOriginalPosition(),
                                position, wrapper.getData());
                    }
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EntityWrapper<T> item = mDatasList.get(position);
        int viewType = getItemViewType(position);
        if (viewType == EntityWrapper.TYPE_TITLE) {
            if (View.INVISIBLE == holder.itemView.getVisibility()) {
                holder.itemView.setVisibility(View.VISIBLE);
            }
            mAdapter.onBindTitleViewHolder(holder, item.getIndexTitle());
        } else if (viewType == EntityWrapper.TYPE_CONTENT) {
            mAdapter.onBindContentViewHolder(holder, item);
        }
    }

    @Override
    public int getItemCount() {
        return mDatasList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDatasList.get(position).getItemType();
    }

    void setOnItemTitleClickListener(IndexableAdapter.OnItemTitleClickListener listener){
        this.mTitleClickListener = listener;
    }

    void setOnItemContentClickListener(IndexableAdapter.OnItemContentClickListener<T> listener){
        this.mContentClickListener = listener;
    }





























}
