package com.solarexsoft.solarexindexlayout.utils;

import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.regex.Pattern;

/**
 * Created by houruhou on 21/01/2018.
 */

public class PinyinUtil {
    public static final String TAG = PinyinUtil.class.getSimpleName();

    private static final String PATTERN_LETTER = "^[a-zA-Z].*+";

    public static String getPinYin(String input) {
        if (input == null) {
            return "";
        }
        String pinyin = Pinyin.toPinyin(input, "").toLowerCase();
        Log.d(TAG, "getPinYin input = " + input + ",pinyin = " + pinyin);
        return pinyin;
    }

    public static boolean matchingLetter(String input) {
        boolean isMatching = Pattern.matches(PATTERN_LETTER, input);
        Log.d(TAG, "mattching input = " + input + ", " + isMatching);
        return isMatching;
    }


}
