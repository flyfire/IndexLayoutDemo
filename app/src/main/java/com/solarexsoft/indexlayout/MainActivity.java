package com.solarexsoft.indexlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict;
import com.solarexsoft.solarexindexlayout.EntityWrapper;
import com.solarexsoft.solarexindexlayout.IndexLayout;
import com.solarexsoft.solarexindexlayout.IndexableAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private List<CityEntity> mDatas;

    IndexLayout mIndexLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIndexLayout = (IndexLayout) findViewById(R.id.indexlayout);
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(this)));
        mIndexLayout.setComparator(new Comparator<EntityWrapper<CityEntity>>() {
            @Override
            public int compare(EntityWrapper<CityEntity> o1, EntityWrapper<CityEntity> o2) {
                return o1.getPinyin().compareTo(o2.getPinyin());
            }
        });
        CityAdapter adapter = new CityAdapter(this);
        mIndexLayout.setAdapter(adapter);
        List<CityEntity> list = new ArrayList<>();
        List<String> cityStrings = Arrays.asList(getResources().getStringArray(R.array.city_array));
        for (String cityString : cityStrings) {
            CityEntity entity = new CityEntity();
            entity.setName(cityString);
            list.add(entity);
        }

        adapter.setDatas(list, null);
        adapter.setOnItemContentClickListener(new IndexableAdapter
                .OnItemContentClickListener<CityEntity>() {


            @Override
            public void onItemClick(View view, int originalPosition, int currentPosition,
                                    CityEntity entity) {
                Log.d(TAG, "click city = " + entity.getName());
            }
        });
        adapter.setOnItemTitleClickListener(new IndexableAdapter.OnItemTitleClickListener() {
            @Override
            public void onItemClick(View view, int currentPosition, String indexTitle) {
                Log.e(TAG, "click index = " + indexTitle);
            }
        });

    }
}
