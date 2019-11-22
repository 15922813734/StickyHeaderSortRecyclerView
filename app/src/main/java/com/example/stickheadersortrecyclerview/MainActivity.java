package com.example.stickheadersortrecyclerview;

import android.text.TextUtils;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stickheadersortrecyclerview.bean.ItemBean;
import com.example.stickheadersortrecyclerview.widget.LetterSlideBar;
import com.luck.library.base.BaseActivity;
import com.luck.library.utils.ApplicationUtils;
import com.luck.library.utils.LogUtils;
import com.luck.library.utils.toasty.Toasty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.slideBar)
    LetterSlideBar mSlideBar;

    private MyAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;
    private TitleItemDecoration mDecoration;

    private List<String> nameList = Arrays.asList(ApplicationUtils.getApp().getResources().getStringArray(R.array.personname));

    @Override
    protected void initPage() {
        mAdapter = new MyAdapter();
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(mLayoutManager);
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
                    mLayoutManager.scrollToPositionWithOffset(position, 0);
                }
            }
        });
        setData();
//        mDecoration.setOnTitleLettersChangedListener(new TitleItemDecoration.onTitleLettersChangedListener() {
//            @Override
//            public void onTitleLettersChanged(String s) {
//                mSlideBar.setShowLetter(s);
//            }
//        });
        mAdapter.setOnItemViewClickListener(new MyAdapter.onItemViewClickListener() {
            @Override
            public void onClick(int position, String s) {
                Toasty.success(mActivity, String.format(Locale.CHINA,"click:%d  %s", position, s)).show();
            }
        });
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                final int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                final int lastVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                String temp = mAdapter.getCurrentLetter(firstVisibleItemPosition, lastVisibleItemPosition, dy > 0);
                if(!TextUtils.isEmpty(temp)) {
                    mSlideBar.setShowLetter(temp);
                }
            }
        });
    }

    @Override
    protected int getPageLayoutId() {
        return R.layout.activity_main;
    }

    private void setData() {
        List<ItemBean> datas = new ArrayList<>();
        TreeSet<String> letterSet = new TreeSet<>((o1, o2) -> {
             return comparatorRule(o1, o2);
        });
        for (int i = 0; i < nameList.size(); i++) {
            int picIndex = i % 60;
            int drawableId = mActivity.getResources().getIdentifier("pic_" + (picIndex < 10 ? "0" : "") + (picIndex+1), "drawable", mActivity.getPackageName());
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
        for (int i = 0; i < datas.size(); i++) {
            letterSet.add(datas.get(i).getLetters());
            datas.get(i).setName(datas.get(i).getName()+ i);
        }
        mSlideBar.setLetters(new ArrayList<>(letterSet));
        mDecoration.addData(datas);
        mAdapter.addData(datas);
    }

    /**
     * 保证字母升序排列，并将#放到最后
     * @param o1
     * @param o2
     * @return
     */
    private int comparatorRule(String o1, String o2) {
        if (o1.equals("#")) {
            if(o2.equals("#")) {
                return 0;
            }
            return 1;
        } else if(o2.equals("#")) {
            return -1;
        }else {
            return o1.compareTo(o2);
        }
    }
    public class PinyinComparator implements Comparator<ItemBean> {
        public int compare(ItemBean o1, ItemBean o2) {
            return comparatorRule(o1.getLetters(), o2.getLetters());
        }
    }
}
