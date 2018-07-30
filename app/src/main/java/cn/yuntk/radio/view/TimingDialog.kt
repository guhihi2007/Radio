package cn.yuntk.radio.view

import android.app.Dialog
import android.content.Context
import android.databinding.ObservableArrayList
import android.graphics.Point
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import cn.yuntk.radio.R
import cn.yuntk.radio.adapter.BaseDataBindingAdapter
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.utils.DensityUtil

/**
 * Author : Gupingping
 * Date : 2018/7/24
 * Mail : gu12pp@163.com
 */
class TimingDialog constructor(context: Context, listener: ItemClickPresenter<Any>) : Dialog(context, R.style.BottomDialog) {

    private lateinit var titleTextView: TextView
    private val list = ObservableArrayList<String>()

    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_playlist, null)
        this.setContentView(contentView)
        val params = contentView.layoutParams as ViewGroup.MarginLayoutParams
        params.width = context.resources.displayMetrics.widthPixels - DensityUtil.dp2px(context, 16f)
        params.bottomMargin = DensityUtil.dp2px(context, 8f)
        contentView.layoutParams = params
        this.window!!.setGravity(Gravity.BOTTOM)
        this.setCanceledOnTouchOutside(true)
        this.window!!.setWindowAnimations(R.style.BottomDialog_Animation)

        val recycleView = contentView.findViewById<RecyclerView>(R.id.RecyclerView_playList)
        recycleView.layoutManager = LinearLayoutManager(context)
        val point = Point()
        window.windowManager.defaultDisplay.getSize(point)
        recycleView.layoutParams = LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, point.y / 3)
        titleTextView = contentView.findViewById(R.id.dialog_title_tv)

        val adapter = BaseDataBindingAdapter(context, R.layout.item_timing_close, listener, list)
        recycleView.adapter = adapter

    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setItems(item: Array<String>) {
        list.addAll(item)
    }
}