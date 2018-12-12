package cn.yuntk.radio.ibook.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import cn.yuntk.radio.R;


/**
 * <p>类描述：加载菊花</p>
 * <p>创建人：yb</p>
 * <p>创建时间：2018/1/16</p>
 * <p>修改人：       </p>
 * <p>修改时间：   </p>
 * <p>修改备注：   </p>
 */
public class SimpleLoadingDialog extends AlertDialog {
    private Context mContext;
    private View mContentView;
    private ProgressBar mProgressBar;
    protected SimpleLoadingDialog(Context context) {
        super(context, R.style.dialog_bond);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.ting_layout_simple_loading_dialog,null);
        initViews();
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
//        window.setWindowAnimations(R.style.Animation);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(mContentView);
    }

    private void initViews() {
        mProgressBar = mContentView.findViewById(R.id.progressBar);
    }

    public static SimpleLoadingDialog build(Context context) {
        SimpleLoadingDialog dialog = new SimpleLoadingDialog(context);
        dialog.show();
        return dialog;
    }
}
