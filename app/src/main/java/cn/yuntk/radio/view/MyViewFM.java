package cn.yuntk.radio.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.yuntk.radio.R;
import cn.yuntk.radio.base.MyViewTouch;
import cn.yuntk.radio.ui.activity.FMActivity;

public class MyViewFM extends View {
    private Context mContext;
    private MyViewTouch myViewTouch;
    private static final String OUTER_COLOR = "#F2D782";
    private static final String INER_COLOR = "#FFFFFF";
    private static final String SCALE_COLOR = "#3e4667";

    private String fmChannel;//当前频道，例如：FM106.1
    private double currentFM;//在整个圆中的相对位置，该值在0到360
    private String name;

    public MyViewFM(Context context) {
        super(context);
        this.mContext = context;
        if (context instanceof FMActivity)
            myViewTouch = (FMActivity) context;
    }

    public MyViewFM(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (context instanceof FMActivity)
            myViewTouch = (FMActivity) context;
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.MyViewFM);
            fmChannel = typedArray.getString(R.styleable.MyViewFM_fmChannel);
            currentFM = (double) typedArray.getFloat(R.styleable.MyViewFM_currentFM, 0);
            name = typedArray.getString(R.styleable.MyViewFM_name);
        }
    }

    private Paint getPaint(int color, int width) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2;//圆心x坐标
        float cy = getHeight() / 2;//圆心y坐标
        float radius = getWidth() / 3 + 20;//半径
        canvas.drawCircle(cx, cy, radius, getPaint(Color.parseColor(OUTER_COLOR), 5));
        int padding = 20;
//        canvas.drawCircle(cx, cy, radius-padding,getPaint(Color.parseColor(OUTER_COLOR),10));
        canvas.translate(cx, cy);//将坐标移动到
        canvas.save();
        for (int i = 0; i < 120; i++) {
            canvas.drawLine(radius - padding - 25, 0, radius - padding, 0, getPaint(Color.parseColor(INER_COLOR), 7));
            canvas.rotate(3);
        }
        onDrawText(canvas, fmChannel, radius);
        canvas.translate(0, -radius / 3);
        onDrawText(canvas, name, radius);
        canvas.translate(0, radius / 3);
        onDrawCurrentFM(canvas, radius - padding);

    }

    /**
     * 先判断触摸事件是不是在圆内
     * 如果在圆内则计算出其与圆心的的角度并更新圆
     *
     * @param event
     * @return
     */
    boolean touchFlag;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        float cx = getWidth() / 2;//圆心x坐标
        float cy = getHeight() / 2;//圆心y坐标
        float radius = getWidth() / 3 + 20;//半径
        float evenR = (float) Math.sqrt((touchX - cx) * (touchX - cx) + (touchY - cy) * (touchY - cy));
        //这里将要计算出与起始位置的角度
        if (radius + 15 > evenR) {
            float sin;
            float cos;
            float tan;
            //以绘制圆的坐标系为参考系的触摸位置
            touchX -= cx;
            touchY -= cy;
            touchY = -touchY;
            sin = (float) (touchX / Math.sqrt(touchX * touchX + touchY * touchY));
            cos = (float) (touchY / Math.sqrt(touchX * touchX + touchY * touchY));
            if (cos == 0 && sin > 0) {
                currentFM = 90;
                invalidate();
                return true;
            } else if (cos == 0 && sin < 0) {
                currentFM = 270;
                invalidate();
                return true;
            }
            tan = sin / cos;
            if (sin >= 0 && cos > 0)
                currentFM = Math.atan(tan) * 180 / 3.1415926;
            else if (sin > 0 && cos < 0)
                currentFM = 180 + Math.atan(tan) * 180 / 3.1415926;
            else if (sin <= 0 && cos < 0)
                currentFM = 180 + Math.atan(tan) * 180 / 3.1415926;
            else if (sin < 0 && cos > 0)
                currentFM = 360 + Math.atan(tan) * 180 / 3.1415926;
            myViewTouch.onTouch(currentFM, event.getAction());
            invalidate();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                touchFlag = false;
            } else
                touchFlag = true;

        } else if (touchFlag) {
            myViewTouch.onTouch(currentFM, MotionEvent.ACTION_UP);
            invalidate();
            touchFlag = false;
        }
        return true;
    }

    private void onDrawText(Canvas canvas, String text, float radius) {
        Paint mPaint = new Paint();
        mPaint.setTextSize(50);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        Rect textBound = new Rect();//创建一个矩形
        mPaint.getTextBounds(text, 0, text.length(), textBound);
        StringBuilder temp = new StringBuilder(text);
        boolean flag = false;
        while (textBound.width() > radius * 2 / 3 * 2) {
            temp.deleteCharAt(temp.length() - 1);
            flag = true;
            mPaint.getTextBounds(temp.toString(), 0, temp.length(), textBound);
        }
        if (flag) {
            temp.delete(temp.length() - 3, temp.length());
            temp.append("...");
            text = temp.toString();
            mPaint.getTextBounds(text, 0, text.length(), textBound);
        }
        canvas.drawText(text, -textBound.width() / 2, 0, mPaint);
    }

    private void onDrawCurrentFM(Canvas canvas, float radius) {//在整个集合中的相对位置
        Paint paint = getPaint(Color.parseColor(SCALE_COLOR), 7);
        paint.setAntiAlias(true);
        currentFM += 1.5;
        canvas.rotate((int) (currentFM / 3) * 3 - 30);
        for (int i = -10; i < 11; i++) {
            canvas.drawLine(0, -(radius), 0, -(radius - 30), paint);
            canvas.rotate(3);
        }
        canvas.rotate(-33);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_play_current);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2, -(radius + bitmap.getHeight() / 2), paint);
    }

    @BindingAdapter({"fmChannel"})
    public static void setFmChannel(View view, String fmChannel) {
        ((MyViewFM) view).setFmChannel(fmChannel);
    }

    @BindingAdapter({"currentFM"})
    public static void setCurrentFM(View view, float currentFM) {
        ((MyViewFM) view).setCurrentFM(currentFM);
    }

    @BindingAdapter({"name"})
    public static void setName(View view, String name) {
        ((MyViewFM) view).setName(name);
    }

    public String getFmChannel() {
        return fmChannel;
    }

    public void setFmChannel(String fmChannel) {
        this.fmChannel = fmChannel;
    }


    public double getCurrentFM() {
        return currentFM;
    }

    public void setCurrentFM(float currentFM) {
        this.currentFM = currentFM;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
