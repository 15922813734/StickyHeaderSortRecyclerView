package com.example.stickheadersortrecyclerview;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stickheadersortrecyclerview.bean.ItemBean;
import com.example.stickheadersortrecyclerview.widget.LetterSlideBar;
import com.luck.library.base.BaseActivity;
import com.luck.library.utils.ApplicationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.slideBar)
    LetterSlideBar mSlideBar;

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
        //设置右侧SideBar触摸监听
        mSlideBar.setOnTouchLetterChangeListener(new LetterSlideBar.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(letter.charAt(0));
                if (position != -1) {
                    mManager.scrollToPositionWithOffset(position, 0);
                }
            }
        });
        setData();
    }

    @Override
    protected int getPageLayoutId() {
        return R.layout.activity_main;
    }

    private void setData() {
        List<ItemBean> datas = new ArrayList<>();
        HashSet<String> letterSet = new HashSet<>();
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
            letterSet.add(itemBean.getLetters());
        }
        mSlideBar.setLetters(new ArrayList<>(letterSet));
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
