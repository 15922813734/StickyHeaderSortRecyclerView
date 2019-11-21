package com.example.stickheadersortrecyclerview.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.stickheadersortrecyclerview.R;
import com.luck.library.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 波浪侧边栏
 */
public class LetterSlideBar extends View {

    private static final String TAG = "WaveSideBar";
    private final static String[] DEFAULT_INDEX_ITEMS = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    private OnTouchLetterChangeListener mListener;

    // 渲染字母表
    private List<String> mLetters;

    // 当前选中的位置
    private int mChoosePosition = -1;

    private int mOldPosition;

    private int mNewPosition;

    // 字母列表画笔
    private Paint mLettersPaint = new Paint();

    // 提示字母画笔
    private Paint mTextPaint = new Paint();

    private int mTextSize;
    private int mHintTextSize;
    private int mTextColor;
    private int mWaveColor;
    private int mTextColorChoose;
    private int mWidth;
    private int mHeight;
    private int mItemHeight;
    private int mPadding;

    // 手指滑动的Y点作为中心点
    private int mCenterY; //中心点Y

    private int mCenterX;//中心点X


    // 选中字体的坐标
    private float mPointX, mPointY;

    // 圆形中心点X
    private float mCircleCenterX;

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
        mLetters = Arrays.asList(DEFAULT_INDEX_ITEMS);
        mTextColor = Color.parseColor("#969696");
        mWaveColor = Color.parseColor("#bef9b81b");
        mTextColorChoose = ContextCompat.getColor(context, android.R.color.white);
        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.textSize);
        mHintTextSize = context.getResources().getDimensionPixelSize(R.dimen.hintTextSize);
        mPadding = context.getResources().getDimensionPixelSize(R.dimen.padding);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.waveSideBar);
            mTextColor = a.getColor(R.styleable.waveSideBar_textColor, mTextColor);
            mTextColorChoose = a.getColor(R.styleable.waveSideBar_chooseTextColor, mTextColorChoose);
            mTextSize = a.getDimensionPixelSize(R.styleable.waveSideBar_textSize, mTextSize);
            mHintTextSize = a.getDimensionPixelSize(R.styleable.waveSideBar_hintTextSize, mHintTextSize);
            mWaveColor = a.getColor(R.styleable.waveSideBar_backgroundColor, mWaveColor);
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
        mNewPosition = (int) (y / mHeight * mLetters.size());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //限定触摸范围
                if (x < mWidth - 200) {
                    return false;
                }
                mCenterX = (int) x;
                mCenterY = (int) y;
                operatorType = 1;
                break;
            case MotionEvent.ACTION_MOVE:
                mCenterY = (int) y;
                mCenterX = (int) x;
                if (mOldPosition != mNewPosition) {
                    if (mNewPosition >= 0 && mNewPosition < mLetters.size()) {
                        mChoosePosition = mNewPosition;
                        if (mListener != null) {
                            mListener.onLetterChange(mLetters.get(mNewPosition));
                        }
                    }
                }
                operatorType = 2;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mChoosePosition = -1;
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
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mItemHeight = (mHeight - mPadding) / mLetters.size();
        mPointX = mWidth - 1.6f * mTextSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制字母列表
        drawLetters(canvas);
        //绘制选中的字体
        if(mCenterX > 210)
        drawChooseText(canvas);

    }

    /**
     * 绘制字母列表
     *
     * @param canvas
     */
    private void drawLetters(Canvas canvas) {

        RectF rectF = new RectF();
        rectF.left = mPointX - mTextSize;
        rectF.right = mPointX + mTextSize;
        rectF.top = mTextSize / 2;
        rectF.bottom = mHeight - mTextSize / 2;

        mLettersPaint.reset();
        mLettersPaint.setStyle(Paint.Style.FILL);
        mLettersPaint.setColor(Color.parseColor("#F9F9F9"));
        mLettersPaint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, mTextSize, mTextSize, mLettersPaint);

        mLettersPaint.reset();
        mLettersPaint.setStyle(Paint.Style.STROKE);
        mLettersPaint.setColor(mTextColor);
        mLettersPaint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, mTextSize, mTextSize, mLettersPaint);

        for (int i = 0; i < mLetters.size(); i++) {
            mLettersPaint.reset();
            mLettersPaint.setColor(mTextColor);
            mLettersPaint.setAntiAlias(true);
            mLettersPaint.setTextSize(mTextSize);
            mLettersPaint.setTextAlign(Paint.Align.CENTER);

            Paint.FontMetrics fontMetrics = mLettersPaint.getFontMetrics();
            float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);

            float pointY = mItemHeight * i + baseline / 2 + mPadding;

            if (i == mChoosePosition) {
                mPointY = pointY;
            } else {
                canvas.drawText(mLetters.get(i), mPointX, pointY, mLettersPaint);
            }
        }

    }


    /**
     * 绘制选中的字母
     *
     * @param canvas
     */
    private void drawChooseText(Canvas canvas) {

        if(operatorType == 0 || mChoosePosition >= mLetters.size() || mChoosePosition < 0){
            return;
        }
        int popTextSize = 100;

/*
        RectF rectF = new RectF();
        rectF.left = mX - 160 - popTextSize;
        rectF.right = mX - 160 + popTextSize * 2;
        rectF.top = mY + mHeight / 2 + 1.5f - popTextSize / 2;
        rectF.bottom = mY + mHeight / 2 + 1.5f + popTextSize;

        mTextPaint.reset();
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setAntiAlias(true);
        canvas.drawArc(rectF, mX - 160, mY + mHeight / 2 + 1.5f,true, mLettersPaint);
*/


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
        String target = mLetters.get(mChoosePosition);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);
        canvas.drawText(target, x, y-50, mTextPaint);// TODO 50需要一个依据
//            }

    }

    public void setOnTouchLetterChangeListener(OnTouchLetterChangeListener listener) {
        this.mListener = listener;
    }

    public List<String> getLetters() {
        return mLetters;
    }

    public void setLetters(List<String> letters) {
        this.mLetters = letters;
        invalidate();
    }

    public interface OnTouchLetterChangeListener {
        void onLetterChange(String letter);
    }
}