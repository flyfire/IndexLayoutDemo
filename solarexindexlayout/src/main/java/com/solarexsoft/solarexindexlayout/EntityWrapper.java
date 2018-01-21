package com.solarexsoft.solarexindexlayout;

/**
 * Created by houruhou on 19/01/2018.
 */

public class EntityWrapper<T> {
    public static final int TYPE_TITLE = Integer.MAX_VALUE - 1;
    public static final int TYPE_CONTENT = Integer.MAX_VALUE;

    private String index;
    private String indexTitle;
    private String pinyin;
    private String indexByField;
    private T data;
    private int originalPosition;
    private int itemType = TYPE_CONTENT;
    private boolean isFirst = false;
    private boolean isLast = false;

    EntityWrapper() {
    }

    EntityWrapper(String index, int itemType) {
        this.index = index;
        this.indexTitle = index;
        this.pinyin = index;
        this.itemType = itemType;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndexTitle() {
        return indexTitle;
    }

    public void setIndexTitle(String indexTitle) {
        this.indexTitle = indexTitle;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getIndexByField() {
        return indexByField;
    }

    public void setIndexByField(String indexByField) {
        this.indexByField = indexByField;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    public void setOriginalPosition(int originalPosition) {
        this.originalPosition = originalPosition;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isTitle() {
        return itemType == TYPE_TITLE;
    }

    public boolean isContent() {
        return itemType == TYPE_CONTENT;
    }
}
