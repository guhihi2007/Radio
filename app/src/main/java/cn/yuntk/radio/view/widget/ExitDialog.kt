package cn.yuntk.radio.view.widget

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import cn.yuntk.radio.R

/**
 * Author : Gupingping
 * Date : 2018/8/8
 * Mail : gu12pp@163.com
 */
class ExitDialog(context: Activity) : Dialog(context,R.style.BottomDialog) {
    init {

        //-----初始化dialog--start--
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_exit, null)
        setContentView(contentView)
        val params = contentView.layoutParams as ViewGroup.MarginLayoutParams
        params.width = context.resources.displayMetrics.widthPixels
//        params.height = context.resources.displayMetrics.heightPixels/4
//        params.bottomMargin = DensityUtil.dp2px(context, 8f)
        contentView.layoutParams = params
        window?.setGravity(Gravity.BOTTOM)
        setCanceledOnTouchOutside(true)
        getWindow()!!.setWindowAnimations(R.style.BottomDialog_Animation)
        //-----初始化dialog--end--

        contentView.findViewById<TextView>(R.id.exit_confirm).setOnClickListener {
            dismiss()
            context.finish()
        }
        contentView.findViewById<TextView>(R.id.exit_cancel).setOnClickListener {
            dismiss()
        }
    }
}