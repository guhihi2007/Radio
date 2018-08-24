package cn.yuntk.radio.ibook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.ClassifyListBean;
import cn.yuntk.radio.ibook.widget.GridDividerItemDecoration;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

import java.util.List;

/*分类适配器*/
public class ClassifiesAdapter extends BaseRefreshAdapter<ClassifyListBean.ClassifyType> {

    public ClassifiesAdapter(Context context, List<ClassifyListBean.ClassifyType> data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    private RvItemClickInterface rvItemClickInterface;

    public void setRvItemClickInterface(RvItemClickInterface rvItemClickInterface) {
        this.rvItemClickInterface = rvItemClickInterface;
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, ClassifyListBean.ClassifyType classifyType) {
        holder.setText(R.id.classify_name_tv,classifyType.getContent());
        RecyclerView rv = holder.getView(R.id.items_rv);
        ClassifyItemAdapter adapter = new ClassifyItemAdapter(classifyType.getTypes());
        adapter.setRvItemClickInterface(rvItemClickInterface);

        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
//        manager.setOrientation(LinearLayoutManager.VERTICAL);
//        manager.setAutoMeasureEnabled(true);
        manager.setSmoothScrollbarEnabled(true);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        int decorationcount = rv.getItemDecorationCount();
        if (decorationcount==0){//没有添加过ItemDecoration 才能添加
            GridDividerItemDecoration itemDecoration;
            itemDecoration= new GridDividerItemDecoration(20, Color.WHITE);
            rv.addItemDecoration(itemDecoration);
        }

        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
    }
}
