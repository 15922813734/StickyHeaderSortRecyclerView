package com.example.stickheadersortrecyclerview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.stickheadersortrecyclerview.R;
import com.luck.library.utils.LogUtils;
import com.luck.library.utils.UIUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 字母快速定位侧边栏
 */
public class LetterSlideBar extends View {

    private final String TAG = "LetterSlideBar";

    //默认字母顺序表
    private final String[] DEFAULT_INDEX_ITEMS = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    //字母中线距右间距
    private final int mLettersRightSpace = 8;

    private OnTouchLetterChangeListener mListener;

    // 渲染字母表
    private List<String> mLettersList;

    // 当前触摸字母的位置
    private int mChoosePosition;

    private int mOldPosition;
    private int mNewPosition;

    // 字母列表画笔
    private Paint mLettersPaint = new Paint();

    // 提示字母画笔
    private Paint mHintTextPaint = new Paint();

    //View宽高
    private int mViewWidth;
    private int mViewHeight;
    //自定义属性
    private int mTextSize;
    private int mHintTextSize;
    private int mTextColor;
    private int mTextColorChoose;
    //字母高度
    private int mItemViewHeight;
    //字母上下间距
    private int mVerticalPadding;
    // 手指滑动的坐标，用于画提示文字框
    private int mTouchY;
    private int mTouchX;

    // 选中字母的坐标，用于画字母列表
    private float mPointX, mPointY;

    //判断是否需要画选中字母
    private int operatorType = 0;//1:Action_down  2:Action_move;  0:Action_up

    public LetterSlideBar(Context context) {
        this(context, null);
    }

    public LetterSlideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterSlideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mLettersList = Arrays.asList(DEFAULT_INDEX_ITEMS);
        mTextColor = Color.parseColor("#1FB895");
        mTextColorChoose = ContextCompat.getColor(context, android.R.color.white);
        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.textSize);
        mHintTextSize = context.getResources().getDimensionPixelSize(R.dimen.hintTextSize);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.letterSlideBar);
            mTextColor = a.getColor(R.styleable.letterSlideBar_textColor, mTextColor);
            mTextColorChoose = a.getColor(R.styleable.letterSlideBar_chooseTextColor, mTextColorChoose);
            mTextSize = a.getDimensionPixelSize(R.styleable.letterSlideBar_textSize, mTextSize);
            mHintTextSize = a.getDimensionPixelSize(R.styleable.letterSlideBar_hintTextSize, mHintTextSize);
            a.recycle();
        }
        mHintTextPaint.setAntiAlias(true);
        mHintTextPaint.setColor(mTextColorChoose);
        mHintTextPaint.setStyle(Paint.Style.FILL);
        mHintTextPaint.setTextSize(mHintTextSize);
        mHintTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 通过当前触摸Y坐标及每个字母高度获取当前点击的字母位置
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float y = event.getY();
        final float x = event.getX();
        if(x < mViewWidth / 2) return false;
        mOldPosition = mChoosePosition;
        mNewPosition = (int) ((y - mVerticalPadding)) / mItemViewHeight;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = (int) x;
                mTouchY = (int) y;
                operatorType = 1;
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchY = (int) y;
                mTouchX = (int) x;
                if (mOldPosition != mNewPosition) {
                    if (mNewPosition >= 0 && mNewPosition < mLettersList.size()) {
                        mChoosePosition = mNewPosition;
                        if (mListener != null) {
                            mListener.onLetterChange(mLettersList.get(mNewPosition));
                        }
                    }
                }
                operatorType = 2;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                operatorType = 0;
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 获取组件宽高、上下间距、字母绘制横坐标
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mItemViewHeight = (int) (1.6 * mTextSize);
        mPointX = mViewWidth - UIUtils.dp2px(mLettersRightSpace);
        mVerticalPadding = (mViewHeight - mItemViewHeight * mLettersList.size()) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制字母列表
        drawLetters(canvas);
        //绘制选中的字体
        if (mTouchX < 210) {
            mTouchX = 210;
        }
        drawChooseText(canvas);
    }

    /**
     * 绘制字母列表，并将选中的字母背景画个圆
     *
     * @param canvas
     */
    private void drawLetters(Canvas canvas) {
        for (int i = 0; i < mLettersList.size(); i++) {
            mLettersPaint.reset();
            mLettersPaint.setColor(mTextColor);
            mLettersPaint.setAntiAlias(true);
            mLettersPaint.setTextSize(mTextSize);
            mLettersPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fontMetrics = mLettersPaint.getFontMetrics();
            float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);
            float pointY = mItemViewHeight * i + mVerticalPadding;

            if (i == mChoosePosition) {
                mPointY = pointY;
                canvas.drawCircle(mPointX, mPointY - baseline / 2, UIUtils.dp2px(mLettersRightSpace - 1), mLettersPaint);
                mLettersPaint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawText(mLettersList.get(i), mPointX, pointY, mLettersPaint);
            } else {
                canvas.drawText(mLettersList.get(i), mPointX, pointY, mLettersPaint);
            }
        }
    }


    /**
     * 绘制选中的字母时显示的绿色方框
     *
     * @param canvas
     */
    private void drawChooseText(Canvas canvas) {
        if (operatorType == 0 || mChoosePosition >= mLettersList.size() || mChoosePosition < 0 ||
                mTouchY < mVerticalPadding || mTouchY > mViewHeight - mVerticalPadding) {
            return;
        }
        int popTextSize = 100;
        float x = mTouchX - 160;
        float y = mTouchY;
        mHintTextPaint.reset();
        mHintTextPaint.setStyle(Paint.Style.FILL);
        mHintTextPaint.setColor(Color.parseColor("#1BB771"));
        Path path = new Path();
        RectF roundRectT = new RectF(x - 50, y - 160, x + 100, y - 10);
        // 左上角、右上角、右下角、左下角的x,y
        path.addRoundRect(roundRectT, new float[]{50, 50, 50, 50, 0, 0, 50, 50}, Path.Direction.CCW);
        canvas.drawPath(path, mHintTextPaint);
        mHintTextPaint.reset();
        mHintTextPaint.setColor(Color.WHITE);
        mHintTextPaint.setTextSize(popTextSize);
        // 绘制提示字符
        String target = mLettersList.get(mChoosePosition);
        canvas.drawText(target, x, y - 50, mHintTextPaint);
    }

    /**
     * 设置显示选中状态的字母
     * @param letter
     */
    public void setShowLetter(String letter) {
        if (mLettersList != null && mLettersList.contains(letter)) {
            mChoosePosition = mLettersList.indexOf(letter);
        }
        invalidate();
    }

    public void setOnTouchLetterChangeListener(OnTouchLetterChangeListener listener) {
        this.mListener = listener;
    }

    public List<String> getLetters() {
        return mLettersList;
    }

    public void setLetters(List<String> letters) {
        this.mLettersList = letters;
        invalidate();
    }

    public interface OnTouchLetterChangeListener {
        void onLetterChange(String letter);
    }
}