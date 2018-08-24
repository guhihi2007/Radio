package cn.yuntk.radio.view.widget

import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import cn.yuntk.radio.R
import cn.yuntk.radio.ibook.receiver.MediaButtonReceiver

/**
 * Author : Gupingping
 * Date : 2018/8/8
 * Mail : gu12pp@163.com
 */
class ExitDialog(context: Activity) : Dialog(context, R.style.BottomDialog) {
    companion object {
        public var is_exit_app = false

    }

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
            if (android.os.Build.VERSION.SDK_INT < 21) {
                (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).unregisterMediaButtonEventReceiver(ComponentName(context, MediaButtonReceiver::class.java))
            }
            is_exit_app = true
            context.finish()
        }
        contentView.findViewById<TextView>(R.id.exit_cancel).setOnClickListener {
            dismiss()
        }
    }
}