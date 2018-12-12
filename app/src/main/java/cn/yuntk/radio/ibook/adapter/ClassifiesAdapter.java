package cn.yuntk.radio.ibook.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;


import java.util.List;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.TCBean1_1;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

/**
 * 分类适配器
 * */
public class ClassifiesAdapter extends BaseRefreshAdapter<TCBean1_1> {

    public ClassifiesAdapter(Context context, List<TCBean1_1> data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    private ClassifyItemClickInterface clickInterface;

    public void setClickInterface(ClassifyItemClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, TCBean1_1 classifyType) {
        holder.setText(R.id.classify_name_tv,classifyType.getContent());
        if (holder.getLayoutPosition() == 0){
            holder.setImageResource(R.id.classify_icon_iv,R.drawable.ting_img_1);
        }else if (holder.getLayoutPosition() == 1){
            holder.setImageResource(R.id.classify_icon_iv,R.drawable.ting_img_2);
        }else {
            holder.setImageResource(R.id.classify_icon_iv,R.drawable.ting_img_3);
        }

        RecyclerView rv = holder.getView(R.id.items_rv);
        ClassifyItemAdapter adapter = new ClassifyItemAdapter(classifyType.getTypes(),classifyType.getType());
        adapter.setClickInterface(clickInterface);

        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        manager.setSmoothScrollbarEnabled(true);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

//        int decorationcount = rv.getItemDecorationCount();
//        if (decorationcount==0){//没有添加过ItemDecoration 才能添加
//            GridDividerItemDecoration itemDecoration;
//            itemDecoration= new GridDividerItemDecoration(20, Color.WHITE);
//            rv.addItemDecoration(itemDecoration);
//        }

        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
    }
}
