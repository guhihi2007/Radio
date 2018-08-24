package cn.yuntk.radio.ibook.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.bean.ClassifyListBean;

import java.util.List;

/*分类Item适配器*/
public class ClassifyItemAdapter extends RecyclerView.Adapter<ClassifyItemAdapter.ViewHolder> {

    private List<ClassifyListBean.ClassifyItem> data;
    private RvItemClickInterface rvItemClickInterface;

    public ClassifyItemAdapter(List<ClassifyListBean.ClassifyItem> data) {
        this.data = data;
    }

    public void setRvItemClickInterface(RvItemClickInterface rvItemClickInterface) {
        this.rvItemClickInterface = rvItemClickInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listener_item_classify_items,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClassifyListBean.ClassifyItem item = data.get(position);
        holder.textView.setText(item.getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (rvItemClickInterface!=null){
                rvItemClickInterface.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null?0:data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.classify_items_name_tv);
        }
    }

}
