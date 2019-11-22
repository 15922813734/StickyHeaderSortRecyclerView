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

import java.util.Arrays;
import java.util.List;

/**
 * 字母快速定位侧边栏
 */
public class LetterSlideBar extends View {

    private static final String TAG = "LetterSlideBar";

    /**
     * 默认字母顺序表
     */
    private final  String[] DEFAULT_INDEX_ITEMS = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

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
    private Paint mTextPaint = new Paint();
    //View宽高
    private int mViewWidth;
    private int mViewHeight;

    private int mTextSize;
    private int mHintTextSize;
    private int mTextColor;
    private int mTextColorChoose;
    private int mItemViewHeight;
    //字母间距
//    private int mPadding;
    private int mTopPadding;

    // 手指滑动的Y点作为中心点
    private int mCenterY; //中心点Y

    private int mCenterX;//中心点X

    // 选中字体的坐标
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
//        mPadding = context.getResources().getDimensionPixelSize(R.dimen.padding);
        mTopPadding = context.getResources().getDimensionPixelSize(R.dimen.topPadding);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.letterSlideBar);
            mTextColor = a.getColor(R.styleable.letterSlideBar_textColor, mTextColor);
            mTextColorChoose = a.getColor(R.styleable.letterSlideBar_chooseTextColor, mTextColorChoose);
            mTextSize = a.getDimensionPixelSize(R.styleable.letterSlideBar_textSize, mTextSize);
            mHintTextSize = a.getDimensionPixelSize(R.styleable.letterSlideBar_hintTextSize, mHintTextSize);
            mTopPadding = a.getDimensionPixelSize(R.styleable.letterSlideBar_topPadding, mTopPadding);
            a.recycle();
        }

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColorChoose);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mHintTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float y = event.getY();
        final float x = event.getX();
        mOldPosition = mChoosePosition;
        mNewPosition = (int) ((y - mTopPadding)) / mItemViewHeight;//(int) (y / mViewHeight * mLettersList.size());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCenterX = (int) x;
                mCenterY = (int) y;
                operatorType = 1;
                break;
            case MotionEvent.ACTION_MOVE:
                mCenterY = (int) y;
                mCenterX = (int) x;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mItemViewHeight = (mViewHeight - mTopPadding * 2) / mLettersList.size();
        mPointX = mViewWidth - 1.6f * mTextSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制字母列表
        drawLetters(canvas);
        //绘制选中的字体
        if(mCenterX < 210) {
            mCenterX = 210;
        }
        drawChooseText(canvas);

    }

    /**
     * 绘制字母列表
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

            float pointY = mItemViewHeight * i + baseline / 2 + mTopPadding;

            if (i == mChoosePosition) {
                mPointY = pointY;
                canvas.drawCircle(mPointX, mPointY - baseline /2 ,mTextSize * 1.6f, mLettersPaint);
                mLettersPaint.setColor(Color.parseColor("#FFFFFF"));
//                 //绘制A-Z的字母
                canvas.drawText(mLettersList.get(i), mPointX, pointY, mLettersPaint);
            } else {
                canvas.drawText(mLettersList.get(i), mPointX, pointY, mLettersPaint);
            }
        }
    }


    /**
     * 绘制选中的字母
     *
     * @param canvas
     */
    private void drawChooseText(Canvas canvas) {

        if(operatorType == 0 || mChoosePosition >= mLettersList.size() || mChoosePosition < 0){
            return;
        }
        int popTextSize = 100;
        float x = mCenterX - 160;
        float y = mCenterY ;//mCenterY + baseline / 2;

        mTextPaint.reset();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.parseColor("#1BB771"));
        Path path = new Path();
        RectF roundRectT = new RectF(x-50, y - 160, x + 100, y-10);
        // 左上角、右上角、右下角、左下角的x,y
        path.addRoundRect(roundRectT, new float[]{50, 50, 50, 50, 0, 0, 50, 50}, Path.Direction.CCW);
        canvas.drawPath(path, mTextPaint);
        mTextPaint.reset();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(popTextSize);
        // 绘制提示字符
//            if (mRatio >= 0.9f) {
        String target = mLettersList.get(mChoosePosition);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);
        canvas.drawText(target, x, y-50, mTextPaint);// TODO 50需要一个依据
//            }

    }

    public void setShowLetter(String letter) {
        if(mLettersList != null && mLettersList.contains(letter)) {
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