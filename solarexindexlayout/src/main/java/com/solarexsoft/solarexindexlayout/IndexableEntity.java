package com.solarexsoft.solarexindexlayout;

/**
 * Created by houruhou on 19/01/2018.
 */

public interface IndexableEntity {
    String getFieldIndexBy();

    void setFieldIndexBy(String indexField);

    void setFieldPinyinIndexBy(String pinyin);
}
