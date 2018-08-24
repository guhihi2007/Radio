package cn.yuntk.radio.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.yuntk.radio.Constants;
import cn.yuntk.radio.R;
import cn.yuntk.radio.base.Presenter;
import cn.yuntk.radio.bean.FMBean;
import cn.yuntk.radio.manager.PlayServiceManager;
import cn.yuntk.radio.ui.activity.ListenerFMBeanActivity;
import cn.yuntk.radio.utils.SPUtil;
import cn.yuntk.radio.utils.SystemUtils;

import static cn.yuntk.radio.utils.BaseActivityExtendKt.jumpActivity;

/**
 * Author : Gupingping
 * Date : 2018/7/26
 * Mail : gu12pp@163.com
 * 不知为什么不能用binding
 */
public class FloatView extends FrameLayout implements View.OnClickListener, View.OnTouchListener {

    private float mOriginalRawX;
    private float mOriginalRawY;
    private float mOriginalX;
    private float mOriginalY;
    private static final int TOUCH_TIME_THRESHOLD = 150;
    private long mLastTouchDownTime;
    protected int mScreenWidth;
    private int mScreenHeight;
    private int mStatusBarHeight;
    private View view;
    private LinearLayout ll_out;
    private ImageView float_play;
    private TextView float_fm, float_text;
    private Presenter presenter;
    private Activity mContext;

    public FloatView(Activity context) {
        this(context, null);
        mContext = context;
        view = inflate(context, R.layout.float_window, this);
        ll_out = view.findViewById(R.id.ll_out);
        float_play = view.findViewById(R.id.float_play);
        float_fm = view.findViewById(R.id.float_fm);
        float_text = view.findViewById(R.id.float_text);

        ll_out.setOnClickListener(this);
        float_play.setOnClickListener(this);
        //这个是关键，设置之后可以点击和移动
        ll_out.setOnTouchListener(this);
        float_play.setOnTouchListener(this);
        setLayoutParams(getParams());
    }

    public FloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mStatusBarHeight = SystemUtils.getStatusBarHeight(getContext());
        setClickable(true);
        updateSize();
    }


    //用于判断是移动事件还是点击事件
    protected boolean isOnClickEvent() {
        return System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD;
    }


    protected void updateSize() {
        mScreenWidth = (SystemUtils.getScreenWidth(getContext()) - this.getWidth());
        mScreenHeight = SystemUtils.getScreenHeight(getContext());
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setFMBean(FMBean fmBean) {
        if (fmBean != null) {
            float_fm.setText(fmBean.getRadioFm());
            float_text.setText(fmBean.getName());
            float_text.setSelected(true);
        }
    }

    public ImageView getFloat_play() {
        return float_play;
    }

    @Override
    public void onClick(View v) {
        //回调给外层
        if (presenter != null) {
            presenter.onClick(v);
            return;
        }
        FMBean fmBean = SPUtil.getInstance().getObject(Constants.LAST_PLAY, FMBean.class);
        switch (v.getId()) {
            case R.id.ll_out:
                jumpActivity(mContext, ListenerFMBeanActivity.class, fmBean);
                break;
            case R.id.float_play:
                v.setSelected(!v.isSelected());
                int state = PlayServiceManager.getListenerState();
                if (state == Constants.STATE_IDLE || state == Constants.STATE_PAUSE) {
                    PlayServiceManager.play(fmBean, mContext);
                } else {
                    PlayServiceManager.pauseContinue();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOriginalX = getX();
                mOriginalY = getY();
                mOriginalRawX = event.getRawX();
                mOriginalRawY = event.getRawY();
                mLastTouchDownTime = System.currentTimeMillis();
                updateSize();
                break;
            case MotionEvent.ACTION_MOVE:
                float desX = mOriginalX + event.getRawX() - mOriginalRawX;
                if (desX < 0) {
                    desX = 0;
                }
                if (desX > mScreenWidth) {
                    desX = mScreenWidth;
                }
                setX(desX);
                // 限制不可超出屏幕高度
                float desY = mOriginalY + event.getRawY() - mOriginalRawY;
                if (desY < mStatusBarHeight) {
                    desY = mStatusBarHeight;
                }
                if (desY > mScreenHeight) {
                    desY = mScreenHeight;
                }
                setY(desY);

                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return !isOnClickEvent();//根据是否是点击事件，拦截
    }

    private FrameLayout.LayoutParams getParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.END;
        params.topMargin = mScreenHeight / 2;
        params.rightMargin = 200;
//        params.setMargins(13, params.topMargin, params.rightMargin, 56);
        return params;
    }
}
