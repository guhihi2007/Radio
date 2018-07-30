//package cn.yuntk.radio.view;
//
//import android.content.Context;
//import android.databinding.DataBindingUtil;
//import android.databinding.ViewDataBinding;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//
//import cn.yuntk.radio.BR;
//import cn.yuntk.radio.R;
//import cn.yuntk.radio.base.Presenter;
//import cn.yuntk.radio.bean.FMBean;
//import cn.yuntk.radio.utils.Lg;
//import cn.yuntk.radio.utils.ScreenUtils;
//
///**
// * Author : Gupingping
// * Date : 2018/7/26
// * Mail : gu12pp@163.com
// */
//public class FloatView extends FrameLayout implements View.OnTouchListener {
//    private float mOriginalRawX;
//    private float mOriginalRawY;
//    private float mOriginalX;
//    private float mOriginalY;
//    protected int mScreenWidth;
//    private int mScreenHeight;
//    private int mStatusBarHeight;
//    private long mLastTouchDownTime;
//    private static final int TOUCH_TIME_THRESHOLD = 150;
//    private Presenter listener;
//    private ViewDataBinding binding;
//    private LinearLayout ll_left, ll_play;
//    private ImageView mFloatPlay;
//
//    private int startX;
//    private int startY;
//    private int preX;
//    private int preY;
//    private int mTouchSlop;
//    private FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//            RelativeLayout.LayoutParams.WRAP_CONTENT,
//            RelativeLayout.LayoutParams.WRAP_CONTENT);
//
//    public FloatView(@NonNull Context context) {
//        this(context, null);
//        View view = inflate(context, R.layout.float_window, this);
//        ll_left = view.findViewById(R.id.ll_out);
//        ll_play = view.findViewById(R.id.ll_play);
//        mFloatPlay = view.findViewById(R.id.float_play);
//        ll_left.setOnTouchListener(this);
//        ll_play.setOnTouchListener(this);
//        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//
//        Lg.e("FloatView", "ll_left==" + ll_left.getId());
//        Lg.e("FloatView", "ll_play==" + ll_play.getId());
//        Lg.e("FloatView", "mFloatPlay==" + mFloatPlay.getId());
//
//    }
//
//    public FloatView(@NonNull Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public FloatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initView();
//    }
//
//    private void initView() {
////        mStatusBarHeight = ScreenUtils.getStatusBarHeight(getContext());
////        setClickable(true);
////        updateSize();
//
//    }
//
//    protected void updateSize() {
//        mScreenWidth = (ScreenUtils.getScreenWidth(getContext()) - this.getWidth());
//        mScreenHeight = ScreenUtils.getScreenHeight(getContext());
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event == null) return false;
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                changeOriginalTouchParams(event);
//                updateSize();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                updateViewPosition(event);
//                break;
//            case MotionEvent.ACTION_UP:
//                if (isOnClickEvent()) {
//                    dealClickEvent();
//                }
//                break;
//        }
//        return false;
//    }
//
//    protected boolean isOnClickEvent() {
//        return System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD;
//    }
//
//    protected void dealClickEvent() {
//        if (listener != null) {
//            listener.onClick(this);
//        }
//    }
//
//    private void updateViewPosition(MotionEvent event) {
//        setX(mOriginalX + event.getRawX() - mOriginalRawX);
//        // 限制不可超出屏幕高度
//        float desY = mOriginalY + event.getRawY() - mOriginalRawY;
//        if (desY < mStatusBarHeight) {
//            desY = mStatusBarHeight;
//        }
//        if (desY > mScreenHeight - getHeight()) {
//            desY = mScreenHeight - getHeight();
//        }
//        setY(desY);
//    }
//
//    private void changeOriginalTouchParams(MotionEvent event) {
//        mOriginalX = getX();
//        mOriginalY = getY();
//        mOriginalRawX = event.getRawX();
//        mOriginalRawY = event.getRawY();
//        mLastTouchDownTime = System.currentTimeMillis();
//    }
//
//    public void setListener(Presenter listener) {
//        this.listener = listener;
//    }
//
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        Lg.e("FloatView", " Override onTouch==" + v.getId());
//        Lg.e("FloatView", " Override event==" +event);
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:// 按下
//                startX = (int) event.getRawX();
//                preX = startX;
//                startY = (int) event.getRawY();
//                preY = startY;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int newX = (int) event.getRawX();
//                int newY = (int) event.getRawY();
//
//                int dx = newX - startX;
//                int dy = newY - startY;
//                if (Math.abs(dx) > mTouchSlop || Math.abs(dy) > mTouchSlop) {
//                    params.width += dx;
//                    params.height += dy;
//
//                    if (params.width < 0) {
//                        params.width = 0;
//                    }
//                    if (params.height < 0) {
//                        params.height = 0;
//                    }
//                    if (params.width > mScreenWidth) {
//                        params.width = mScreenWidth;
//                    }
//                    if (params.height > mScreenHeight) {
//                        params.height = mScreenHeight;
//                    }
//                    startX = (int) event.getRawX();
//                    startY = (int) event.getRawY();
//                    setLayoutParams(params);
//                    invalidate();
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//                preX = (int) (event.getRawY()) - preX;
//                preY = (int) (event.getRawY()) - preY;
//                if (Math.abs(preX) > mTouchSlop || Math.abs(preY) > mTouchSlop) {
//                    return true;
//                }
//                break;
//        }
//        return false;
//    }
//}
