package cn.yuntk.radio.ibook.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.base.view.IBaseView;
import cn.yuntk.radio.ibook.common.ILoadingAction;
import cn.yuntk.radio.ibook.component.AppComponent;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 创建时间:2018/4/2
 * 创建人: yb
 * 描述:
 */

public abstract class BaseTitleActivity<T extends BasePresenter> extends RxAppCompatActivity
        implements IBaseView,View.OnClickListener,ILoadingAction {

    @Inject
    protected T mPresenter;

    private View title;
    private View contentView;
    protected Context mContext;

    protected Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setupActivityComponent(XApplication.getsInstance().getAppComponent());
        init();
    }

    private void init() {
        mContext = this;
        mPresenter = getPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        setContentView(createContentView());

        unbinder = ButterKnife.bind(this);
        initViews();
        bindEvent();
        loadData();
    }

    protected abstract void initViews();

    protected abstract void bindEvent();

    protected abstract void loadData();

    protected abstract int getLayoutId();

    protected abstract T getPresenter();

    protected void processClick(View view) {
    }

    private View createContentView() {
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        parent.setLayoutParams(contentParams);
//        不使用标题栏 统一使用自己定义
//        title = getCustomTitle() != null ? getCustomTitle() :
//                LayoutInflater.from(this).inflate(R.layout.layout_common_title, parent, false);
        contentView = LayoutInflater.from(this).inflate(getLayoutId(), parent, false);
//        parent.addView(title);
        parent.addView(contentView);
        return parent;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBar() {
        //改变手机状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
//            int visibility = View.SYSTEM_UI_FLAG_FULLSCREEN;// 全屏设置
            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE;// 表示允许应用的主题内容占用状态栏的空间
            decorView.setSystemUiVisibility(visibility);
            getWindow().setStatusBarColor(Color.parseColor("#d81e06"));// 通知栏颜色
        }
//            ScreenUtils.paintScreen(this, Color.parseColor("#000000"));
    }

    protected View getContentView() {
        return contentView;
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.detachView();
        }
        if (unbinder!=null){
            unbinder.unbind();
        }
    }

    @Override
    public void onClick(View view) {
        processClick(view);
    }

    @Override
    public boolean showLoading() {
        return true;
    }

    @Override
    public Context getLoadingContext() {
        return this;
    }

    protected abstract void setupActivityComponent(AppComponent appComponent);


    /**
     * 加载弹出框
     *
     * @param activity
     * @param message
     */
    ProgressDialog progressDialog;

    public void showProgressBarDialog(String message) {
        if (this != null && !this.isFinishing()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            } else {
                progressDialog = new ProgressDialog(this);
            }
            progressDialog = new ProgressDialog(this);
//            progressDialog.setCancelable(false);
//            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    /**
     * 关闭加载对话框
     */
    public void hideProgress() {
        if (this != null && !this.isFinishing()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }


    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }

}