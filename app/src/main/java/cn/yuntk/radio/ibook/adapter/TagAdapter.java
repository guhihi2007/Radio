package cn.yuntk.radio.ibook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import cn.yuntk.radio.R;

/**
 * Created by HanHailong on 15/10/19.
 */
public class TagAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<String> mDataList;

    public TagAdapter(Context context, List<String> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.ting_tag_item, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_tag);

        String t = mDataList.get(position);
        textView.setText(t);
        view.setTag(t);//便于将文字取出来
        return view;
    }
}
