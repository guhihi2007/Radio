package cn.yuntk.radio.ibook.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.OnClick;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.BaseTitleActivity;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.fragment.RankFragment;

public class StoryTellingListActivity extends BaseTitleActivity {

    @BindView(R.id.title_left_text)
    TextView title_left_text;
    @BindView(R.id.title_right_text)
    TextView title_right_text;
    @BindView(R.id.title_content_text)
    TextView title_content_text;

    @BindView(R.id.st_container_fl)
    FrameLayout st_container_fl;

    RankFragment fragment;

    String book_type_id ="";
    String type_name ="";
    String type ="";

    @Override
    protected void initViews() {

        book_type_id = getIntent().getStringExtra("book_type_id");
        type_name = getIntent().getStringExtra("type_name");
        type = getIntent().getStringExtra("type");

        title_content_text.setText(type_name);
        title_right_text.setVisibility(View.GONE);

        fragment = RankFragment.newInstance(book_type_id,type, type_name);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.st_container_fl,fragment).commitAllowingStateLoss();
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void loadData() {
        showLoading();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ting_activity_story_telling;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @OnClick({R.id.title_left_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_left_text:
                finish();
                break;
        }
    }

}