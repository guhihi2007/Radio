package cn.yuntk.radio.ibook.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.bean.TCBean2;

/*分类Item适配器*/
public class ClassifyItemAdapter extends RecyclerView.Adapter<ClassifyItemAdapter.ViewHolder> {

    private List<TCBean2> data;
    private String type;
    private ClassifyItemClickInterface clickInterface;

    public ClassifyItemAdapter(List<TCBean2> data, String type) {
        this.data = data;
        this.type = type;
    }

    public void setClickInterface(ClassifyItemClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ting_item_classify_items,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TCBean2 item = data.get(position);
        holder.textView.setText(item.getName());
        holder.itemView.setOnClickListener(v -> {
            if (clickInterface!=null){
                clickInterface.onTCBean2ItemClick(item,type);
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
