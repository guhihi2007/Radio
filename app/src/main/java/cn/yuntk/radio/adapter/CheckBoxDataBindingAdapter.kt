package cn.yuntk.radio.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RelativeLayout
import cn.yuntk.radio.R
import cn.yuntk.radio.base.CheckBoxPresenter
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.utils.Lg
import com.android.databinding.library.baseAdapters.BR

/**
 * Author : Gupingping
 * Date : 2018/7/26
 * Mail : gu12pp@163.com
 */
class CheckBoxDataBindingAdapter(context: Context, private val item_layoutID: Int,
                                 private val list: ArrayList<FMBean>) : RecyclerView.Adapter<BindingViewHolder<ViewDataBinding>>() {
    private var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var mList = list
    private val deleteList = ArrayList<FMBean>()

    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, position: Int) {
        val item = mList[position]
        holder.binding.setVariable(cn.yuntk.radio.BR.item, item)
        holder.binding.executePendingBindings()
        holder.binding.root.findViewById<RelativeLayout>(R.id.rl_item_collection).setOnClickListener {
            holder.binding.root.findViewById<CheckBox>(R.id.checkBox).isChecked = true
        }
        holder.binding.root.findViewById<CheckBox>(R.id.checkBox).isChecked = false
        holder.binding.root.findViewById<CheckBox>(R.id.checkBox).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                deleteList.add(list[if (position >= mList.size) mList.size - 1 else position])
            } else {
                deleteList.remove(list[if (position >= mList.size) mList.size - 1 else position])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewDataBinding> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, item_layoutID, parent, false)
        return BindingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    fun getDeleteList(): ArrayList<FMBean> {
        return deleteList
    }

    fun setData(list: List<FMBean>) {
        if (!mList.isEmpty())
            mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun removeList(deleteList: ArrayList<FMBean>) {
        mList.removeAll(deleteList)
        notifyDataSetChanged()
    }
}