package cn.yuntk.radio.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.permission.Rationale;

import cn.yuntk.radio.R;


/**
 * Author : Gupingping
 * Date : 2018/8/8
 * Mail : gu12pp@163.com
 */
public class AuthorityDialog extends PDialogHolder {
    private View empty_view, mlayout;
    private TextView mTitle, mContent, mBtn;

    public AuthorityDialog(@NonNull Context context) {
        super(context);
    }

    public AuthorityDialog(@NonNull Context context, String msg) {
        super(context, msg);
    }

    public AuthorityDialog(@NonNull Context context, Rationale rationale) {
        super(context, rationale);
    }
    public AuthorityDialog(@NonNull Context context, Rationale rationale, String msg) {
        super(context, rationale, msg);
    }

    @Override
    protected void findViews() {
        mlayout = findViewById(R.id.layout_view);
        empty_view = findViewById(R.id.blank_view);
        mTitle = (TextView) findViewById(R.id.listener_title_tv);
        mContent = (TextView) findViewById(R.id.mContent);
        mBtn = (TextView) findViewById(R.id.authority_tv);
        mBtn.setOnClickListener(this);
    }

    @Override
    protected int layoutID() {
        return R.layout.permission_dialog_layout;
    }

    @Override
    protected TextView msgTextView() {
        return mContent;
    }

    @Override
    public void onClick(View v) {
        mRationale.resume();
        dismiss();
    }
}
