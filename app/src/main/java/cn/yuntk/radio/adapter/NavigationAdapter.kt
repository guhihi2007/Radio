package cn.yuntk.radio.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.yuntk.radio.BR
import cn.yuntk.radio.R
import cn.yuntk.radio.bean.ChannelBean

/**
 * Author : Gupingping
 * Date : 2018/7/15
 * Mail : gu12pp@163.com
 */
class NavigationAdapter(private val context: Context) : RecyclerView.Adapter<BindingViewHolder<ViewDataBinding>>() {

    private var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var listener: OnNavigationItemClickListener? = null

    var mList = ArrayList<ChannelBean>()

    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, position: Int) {
        val item = mList[position]
        holder.binding.setVariable(BR.item, item)
        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener {
            listener?.onNavigationItemClick(item)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewDataBinding> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, R.layout.item_navigation, parent, false)
        return BindingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun addAll(list: List<ChannelBean>){
        mList.addAll(list)
    }

    interface OnNavigationItemClickListener {
        fun onNavigationItemClick(channelBean: ChannelBean)
    }

}