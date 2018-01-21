package com.solarexsoft.solarexindexlayout;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.solarexsoft.solarexindexlayout.database.DataObserver;
import com.solarexsoft.solarexindexlayout.database.IndexBarDataObserver;
import com.solarexsoft.solarexindexlayout.utils.InitialComparator;
import com.solarexsoft.solarexindexlayout.utils.PinyinComparator;
import com.solarexsoft.solarexindexlayout.utils.PinyinUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by houruhou on 21/01/2018.
 */

public class IndexLayout extends LinearLayout {
    public static final String TAG = IndexLayout.class.getSimpleName();
    public static final int MODE_FAST = 0;
    public static final int MODE_ALL_LETTERS = 1;
    public static final int MODE_NONE = 2;

    private Context mContext;
    private boolean mShowAllLeters = true;

    private ExecutorService mExecutorService;
    private Future mFuture;

    private RecyclerView mRecyclerView;
    private IndexBar mIndexBar;

    private View mLastInvisibleRecyclerViewItemView;

    private RealAdapter mRealAdapter;
    private LinearLayoutManager mLayoutManager;

    private IndexableAdapter mIndexableAdapter;

    private DataObserver mDataObserver;

    private int mCompareMode = MODE_FAST;
    private Comparator mComparator;
    private Handler mHandler;

    private IndexBarDataObserver mIndexBarDataObserver = new IndexBarDataObserver() {
        @Override
        public void onChanged() {
            mIndexBar.setDatas(mShowAllLeters, mRealAdapter.getItems());
        }
    };

    public IndexLayout(Context context) {
        this(context, null);
    }

    public IndexLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.mExecutorService = Executors.newSingleThreadExecutor();
        LayoutInflater.from(context).inflate(R.layout.indexlayout, this, true);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.rv_datas);
        mIndexBar = (IndexBar) this.findViewById(R.id.indexbar);
        mRealAdapter = new RealAdapter();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRealAdapter);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        initListeners();
    }

    private void initListeners() {
        mIndexBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int touchPos = mIndexBar.getPositionForPointY(motionEvent.getY());
                Log.d(TAG, "touchpos = " + touchPos);
                if (touchPos < 0) {
                    return true;
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if (touchPos != mIndexBar.getSelectionPosition()) {
                            mIndexBar.setSelectionPosition(touchPos);
                            if (touchPos == 0) {
                                mLayoutManager.scrollToPositionWithOffset(0, 0);
                            } else {
                                int firstRecyclerViewPositionBySelection = mIndexBar
                                        .getFirstRecyclerViewPositionBySelection();
                                Log.d(TAG, "firstRecyclerViewPositionBySelection = " +
                                        firstRecyclerViewPositionBySelection);
                                mLayoutManager.scrollToPositionWithOffset
                                        (firstRecyclerViewPositionBySelection, 0);
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    public <T extends IndexableEntity> void setAdapter(final IndexableAdapter adapter) {
        this.mIndexableAdapter = adapter;
        if (mDataObserver != null) {
            adapter.unregisterDataSetObserver(mDataObserver);
        }

        mDataObserver = new DataObserver() {
            @Override
            public void onInited() {
                onSetListener(IndexableAdapter.TYPE_ALL);
                onDataChanged();
            }

            @Override
            public void onChanged() {
                if (mRealAdapter != null) {
                    mRealAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onSetListener(int type) {
                if ((type == IndexableAdapter.TYPE_CLICK_TITLE || type == IndexableAdapter
                        .TYPE_ALL) && adapter.getOnItemTitleClickListener() != null) {
                    mRealAdapter.setOnItemTitleClickListener(adapter.getOnItemTitleClickListener());
                }
                if ((type == IndexableAdapter.TYPE_CLICK_CONTENT || type == IndexableAdapter
                        .TYPE_ALL) && adapter.getOnItemContentClickListener() != null) {
                    mRealAdapter.setOnItemContentClickListener(adapter
                            .getOnItemContentClickListener());
                }
            }
        };
        adapter.registerDataSetObserver(mDataObserver);
        mRealAdapter.setIndexableAdapter(adapter);
    }

    @IntDef({MODE_FAST, MODE_ALL_LETTERS, MODE_NONE})
    @Retention(RetentionPolicy.SOURCE)
    @interface CompareMode {
    }

    public void setCompareMode(@CompareMode int mode) {
        this.mCompareMode = mode;
    }

    public void setFastCompare(boolean fastCompare) {
        setCompareMode(fastCompare ? MODE_FAST : MODE_ALL_LETTERS);
    }

    public <T extends IndexableEntity> void setComparator(Comparator<EntityWrapper<T>> comparator) {
        this.mComparator = comparator;
    }

    public void setShowAllLeters(boolean showAllLeters) {
        this.mShowAllLeters = showAllLeters;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void onDataChanged() {
        if (mFuture != null) {
            mFuture.cancel(true);
        }
        mFuture = mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                final ArrayList<EntityWrapper> datas = transform(mIndexableAdapter.getItems());
                if (datas == null) {
                    return;
                }
                getSafeHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mRealAdapter.setDatas(datas);
                        mIndexBar.setDatas(mShowAllLeters, mRealAdapter.getItems());
                        if (mIndexableAdapter.getIndexCallback() != null) {
                            mIndexableAdapter.getIndexCallback().onFinished(datas);
                        }
                    }
                });
            }
        });
    }

    private <T extends IndexableEntity> ArrayList<EntityWrapper<T>> transform(final List<T> datas) {
        try {
            TreeMap<String, List<EntityWrapper<T>>> map = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });
            for (int i = 0; i < datas.size(); i++) {
                EntityWrapper<T> entity = new EntityWrapper<>();
                T item = datas.get(i);
                String indexName = item.getFieldIndexBy();
                String pinyin = PinyinUtil.getPinYin(indexName);
                entity.setPinyin(pinyin);
                if (PinyinUtil.matchingLetter(pinyin)) {
                    entity.setIndex(pinyin.substring(0, 1).toUpperCase());
                    entity.setIndexByField(item.getFieldIndexBy());
                }
                entity.setIndexTitle(entity.getIndex());
                entity.setData(item);
                entity.setOriginalPosition(i);
                item.setFieldPinyinIndexBy(entity.getPinyin());

                String inital = entity.getIndex();
                List<EntityWrapper<T>> list;
                if (!map.containsKey(inital)) {
                    list = new ArrayList<>();
                    list.add(new EntityWrapper<T>(entity.getIndex(), EntityWrapper.TYPE_TITLE));
                    map.put(inital, list);
                } else {
                    list = map.get(inital);
                }

                list.add(entity);
            }

            ArrayList<EntityWrapper<T>> list = new ArrayList<>();
            for (List<EntityWrapper<T>> entityWrappers : map.values()) {
                if (mComparator != null) {
                    Collections.sort(entityWrappers, mComparator);
                } else {
                    Comparator comparator;
                    if (mCompareMode == MODE_FAST) {
                        comparator = new InitialComparator<T>();
                        Collections.sort(entityWrappers, comparator);
                    } else if (mCompareMode == MODE_ALL_LETTERS) {
                        comparator = new PinyinComparator<T>();
                        Collections.sort(entityWrappers, comparator);
                    }
                }
                int size = entityWrappers.size();
                if (size > 3) {
                    entityWrappers.get(1).setFirst(true);
                    entityWrappers.get(size - 1).setLast(true);
                }
                list.addAll(entityWrappers);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Handler getSafeHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }
}
