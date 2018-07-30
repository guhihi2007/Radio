package cn.yuntk.radio.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.yuntk.radio.BR
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.utils.Lg

/**
 * Author : Gupingping
 * Date : 2018/7/15
 * Mail : gu12pp@163.com
 */
open class BaseDataBindingAdapter<T>(context: Context, private val item_layoutID: Int, private val listener: ItemClickPresenter<T>, private val list: ObservableArrayList<T>) : RecyclerView.Adapter<BindingViewHolder<ViewDataBinding>>() {

    private var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var mList = list

    init {
        //要对ViewModel的ObservableArrayList添加监听，不然数据无变化，一开始我也被坑了
        list.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<T>>() {
            override fun onChanged(sender: ObservableArrayList<T>) {
                Lg.e("OnListChangedCallback onChanged==${sender.size}")
                notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(sender: ObservableArrayList<T>, positionStart: Int, itemCount: Int) {
                if (sender.isEmpty()) {
                    Lg.e("OnListChangedCallback onItemRangeRemoved==${sender.size}")
                    notifyDataSetChanged()
                } else {
                    Lg.e("OnListChangedCallback onItemRangeRemoved ==$positionStart,itemCount==$itemCount")
                    notifyItemRangeRemoved(positionStart, itemCount)
                }
            }

            override fun onItemRangeMoved(sender: ObservableArrayList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
                Lg.e("OnListChangedCallback onItemRangeMoved ==${sender.size},fromPosition==$fromPosition,toPosition==$toPosition")
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onItemRangeInserted(sender: ObservableArrayList<T>, positionStart: Int, itemCount: Int) {
                Lg.e("OnListChangedCallback onItemRangeInserted ==${sender.size},positionStart==$positionStart,itemCount==$itemCount")
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeChanged(sender: ObservableArrayList<T>, positionStart: Int, itemCount: Int) {
                Lg.e("OnListChangedCallback onItemRangeChanged ==${sender.size},positionStart==$positionStart,itemCount==$itemCount")
                notifyItemRangeChanged(positionStart, itemCount)
            }
        })
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, position: Int) {
        val item = mList[position]
        holder.binding.setVariable(BR.item, item)
        holder.binding.setVariable(BR.itemClickPresenter, listener)
        holder.binding.executePendingBindings()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewDataBinding> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, item_layoutID, parent, false)
        return BindingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}