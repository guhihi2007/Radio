package cn.yuntk.radio.view.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yanzhenjie.permission.Rationale;


/**
 * Author : Gupingping
 * Date : 2018/8/8
 * Mail : gu12pp@163.com
 */

public abstract class PDialogHolder extends Dialog implements View.OnClickListener {
    protected String mMsg;
    protected Rationale mRationale;
    protected int mRequestCode;
    protected Activity mActivity;

    public PDialogHolder(@NonNull Context context) {
        super(context, android.R.style.Theme);
        style();
    }

    public PDialogHolder(@NonNull Context context, String msg) {
        super(context, android.R.style.Theme);
        style();
        this.mMsg = msg;
    }

    public PDialogHolder(@NonNull Context context, Rationale rationale) {
        super(context, android.R.style.Theme);
        style();
        this.mRationale = rationale;
    }

    public PDialogHolder(@NonNull Context context, Rationale rationale, String msg) {
        super(context, android.R.style.Theme);
        style();
        this.mMsg = msg;
        this.mRationale = rationale;
    }

    public PDialogHolder(Activity activity, int requestCode) {
        super(activity, android.R.style.Theme);
        style();
        this.mRequestCode = requestCode;
        this.mActivity = activity;
    }

    public PDialogHolder(Activity activity, int requestCode, String msg) {
        super(activity, android.R.style.Theme);
        style();
        this.mMsg = msg;
        this.mRequestCode = requestCode;
        this.mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutID());
        findViews();
        setMsg();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected abstract void findViews();

    protected abstract int layoutID();

    protected void setMsg() {
        String str;
        if (TextUtils.isEmpty(mMsg)) {
            str = "FM收音机需要获取存储空间权限，为您推荐最适合的电台，保障收听顺畅。";
        } else {
            str = "FM收音机需要获取" + mMsg + "权限，为您推荐最适合的电台，保障收听顺畅。";
        }
        Spannable spannable = new SpannableString(str);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8800")), 9, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        msgTextView().setText(spannable);
    }

    protected abstract TextView msgTextView();

    private void style() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

}
