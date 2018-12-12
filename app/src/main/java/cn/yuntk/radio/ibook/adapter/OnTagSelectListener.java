package cn.yuntk.radio.ibook.adapter;


import java.util.List;

import cn.yuntk.radio.ibook.widget.FlowTagLayout;

/**
 * Created by HanHailong on 15/10/20.
 */
public interface OnTagSelectListener {
    void onItemSelect(FlowTagLayout parent, List<Integer> selectedList);
}
