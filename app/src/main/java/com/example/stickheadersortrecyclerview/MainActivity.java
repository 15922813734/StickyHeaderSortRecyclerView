package com.example.stickheadersortrecyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.stickheadersortrecyclerview.bean.ItemBean;
import com.luck.library.base.BaseActivity;
import com.luck.library.utils.ApplicationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    private int index = 0;
    @BindView(R.id.rv)
    RecyclerView mRv;
    private MyAdapter mAdapter;

    private LinearLayoutManager mManager;
    private TitleItemDecoration mDecoration;

    private List<String> nameList = Arrays.asList(ApplicationUtils.getApp().getResources().getStringArray(R.array.personname));

    @Override
    protected void initPage() {
        mAdapter = new MyAdapter();
        mManager = new LinearLayoutManager(this);
        mManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(mManager);
        mRv.setAdapter(mAdapter);
        mDecoration = new TitleItemDecoration(this);
        //如果add两个，那么按照先后顺序，依次渲染。
        mRv.addItemDecoration(mDecoration);
        mRv.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        setData();
    }

    @Override
    protected int getPageLayoutId() {
        return R.layout.activity_main;
    }

    private void setData() {
        List<ItemBean> datas = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            int drawableId = mActivity.getResources().getIdentifier("pic_" + (i < 10 ? "0" : "") + (i+1), "drawable", mActivity.getPackageName());
            ItemBean itemBean = new ItemBean(drawableId, nameList.get(i));
            String sortString = nameList.get(i).substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                itemBean.setLetters(sortString.toUpperCase());
            } else {
                itemBean.setLetters("#");
            }
            datas.add(itemBean);
        }
        Collections.sort(datas, new PinyinComparator());
        mDecoration.addData(datas);
        mAdapter.addData(datas);
    }

    public class PinyinComparator implements Comparator<ItemBean> {

        public int compare(ItemBean o1, ItemBean o2) {
            if (o1.getLetters().equals("@")
                    || o2.getLetters().equals("#")) {
                return 1;
            } else if (o1.getLetters().equals("#")
                    || o2.getLetters().equals("@")) {
                return -1;
            } else {
                return o1.getLetters().compareTo(o2.getLetters());
            }
        }

    }
}
