package com.solarexsoft.solarexindexlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by houruhou on 20/01/2018.
 */

public class IndexBar extends View {
    public static final String TAG = IndexBar.class.getSimpleName();
    private int mTotalHeight;

    private List<String> mIndexList = new ArrayList<>();
    private HashMap<String, Integer> mMapping = new HashMap<>();
    private ArrayList<EntityWrapper> mDatas;

    private int mSelectionPosition;
    private float mIndexHeight;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Context mContext;

    private int mTextColor, mSelectedTextColor;
    private float mTextSize, mTextSpace;
    private Drawable mBackground;
    private float mWidth, mHeight;


    public IndexBar(Context context) {
        this(context, null);
    }

    public IndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public IndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(R.styleable.IndexBar);
            mTextColor = array.getColor(R.styleable.IndexBar_textColor, ContextCompat.getColor
                    (context, R.color.default_indexBar_textColor));
            mSelectedTextColor = array.getColor(R.styleable.IndexBar_selectedTextColor,
                    ContextCompat.getColor(context, R.color.default_indexBar_selectedTextColor));
            mTextSize = array.getDimension(R.styleable.IndexBar_textSize, getResources()
                    .getDimension(R.dimen.default_indexBar_textSize));
            mTextSpace = array.getDimension(R.styleable.IndexBar_textSpace, getResources()
                    .getDimension(R.dimen.default_indexBar_textSpace));
            mBackground = array.getDrawable(R.styleable.IndexBar_background);
            mWidth = array.getDimension(R.styleable.IndexBar_layout_width, getResources()
                    .getDimension(R.dimen.default_indexBar_layout_width));
            mHeight = array.getDimension(R.styleable.IndexBar_layout_height, getResources()
                    .getDimension(R.dimen.default_indexBar_layout_height));
            array.recycle();
        }
        if (mBackground != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(mBackground);
            } else {
                setBackgroundDrawable(mBackground);
            }
        }

        mPaint.setColor(mTextColor);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTextSize);

        mSelectedPaint.setTextAlign(Paint.Align.CENTER);
        mSelectedPaint.setTextSize(mTextSize + (int) TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        mSelectedPaint.setColor(mSelectedTextColor);

        for (char i = 'A'; i <= 'Z'; i++) {
            mIndexList.add(String.valueOf(i));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mIndexList.size() > 0) {
            mTotalHeight = (int) ((mIndexList.size() - 1) * mPaint.getTextSize() + mSelectedPaint
                    .getTextSize() + (mIndexList.size() + 1) * mTextSpace);
        }
        if (mTotalHeight > heightSize) {
            mTotalHeight = heightSize;
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (mWidth > widthSize) {
            mWidth = widthSize;
        }
        Log.d(TAG, "onMeasure width = " + mWidth + ",height = " + mTotalHeight);
        super.onMeasure(MeasureSpec.makeMeasureSpec((int) mWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mTotalHeight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mIndexHeight = ((float) getHeight()) / mIndexList.size();
        Log.d(TAG, "onDraw mIndexHeight = " + mIndexHeight);
        Log.d(TAG, "onDraw mSelectionPosition = " + mSelectionPosition);

        for (int i = 0; i < mIndexList.size(); i++) {
            if (mSelectionPosition == i) {
                canvas.drawText(mIndexList.get(i), getWidth() / 2, mIndexHeight * 0.85f +
                        mIndexHeight * i, mSelectedPaint);
            } else {
                canvas.drawText(mIndexList.get(i), getWidth() / 2, mIndexHeight * 0.85f +
                        mIndexHeight * i, mPaint);
            }
        }
    }

    int getPositionForPointY(float y) {
        int position = (int) (y / mIndexHeight);
        Log.d(TAG, "getPositionForPointY position = " + position + ",y = " + y);
        if (position < 0) {
            position = 0;
        } else if (position > mIndexList.size() - 1) {
            position = mIndexList.size() - 1;
        }
        Log.d(TAG, "getPositionForPointY position = " + position);
        return position;
    }

    int getSelectionPosition() {
        return mSelectionPosition;
    }

    void setSelectionPosition(int position) {
        this.mSelectionPosition = position;
        invalidate();
    }

    int getFirstRecyclerViewPositionBySelection() {
        String index = mIndexList.get(mSelectionPosition);
        Log.d(TAG, "getFirstRecyclerViewPositionBySelection index = " + index);
        if (mMapping.containsKey(index)) {
            return mMapping.get(index);
        }
        return -1;
    }

    List<String> getIndexList() {
        return mIndexList;
    }

    void setDatas(boolean showAllLeter, ArrayList<EntityWrapper> datas) {
        this.mDatas = datas;
        for (int i = 0; i < datas.size(); i++) {
            EntityWrapper wrapper = datas.get(i);
            if (wrapper.getItemType() == EntityWrapper.TYPE_TITLE || wrapper.getIndexTitle() ==
                    null) {
                String index = wrapper.getIndex();
                if (!TextUtils.isEmpty(index)) {
                    if (!mMapping.containsKey(index)) {
                        mMapping.put(index, i);
                    }
                }
            }
        }
        Log.d(TAG, "setDatas mMapping = " + mMapping);
        requestLayout();
    }

    void setSelection(int firstVisibleItemPosition) {
        if (mDatas == null || mDatas.size() <= firstVisibleItemPosition ||
                firstVisibleItemPosition < 0) {
            return;
        }
        EntityWrapper wrapper = mDatas.get(firstVisibleItemPosition);
        int position = mIndexList.indexOf(wrapper.getIndex());
        Log.d(TAG, "setSelection firstVisibleItemPosition = " + firstVisibleItemPosition + "," +
                "position = " + position);
        if (mSelectionPosition != position && position >= 0) {
            mSelectionPosition = position;
            invalidate();
        }
    }

}
