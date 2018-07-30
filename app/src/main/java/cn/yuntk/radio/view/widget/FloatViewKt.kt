//package cn.yuntk.radio.view.widget
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.app.ActivityManager
//import android.content.Context
//import android.databinding.DataBindingUtil
//import android.databinding.ObservableField
//import android.databinding.ViewDataBinding
//import android.graphics.PixelFormat
//import android.os.Build
//import android.os.Handler
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.WindowManager
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.ProgressBar
//import android.widget.TextView
//import cn.yuntk.radio.BR
//import cn.yuntk.radio.Constants
//
//import cn.yuntk.radio.R
//import cn.yuntk.radio.base.Presenter
//import cn.yuntk.radio.bean.FMBean
//import cn.yuntk.radio.bean.messageEvent.ListenEvent
//import cn.yuntk.radio.manager.ActivityStack
//import cn.yuntk.radio.manager.PlayServiceManager
//import cn.yuntk.radio.ui.activity.ListenerFMBeanActivity
//import cn.yuntk.radio.utils.Lg
//import cn.yuntk.radio.utils.jumpActivity
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//
///**
// * Author : Gupingping
// * Date : 2018/7/25
// * Mail : gu12pp@163.com
// *
// * 6.0版本以上要申请悬浮窗权限
// */
//class FloatViewKt(private val mContext: Activity, private val fmBean: ObservableField<FMBean>?) : View.OnTouchListener, Presenter {
//
//    //定义浮动窗口布局
//    private var mFloatLayout: LinearLayout? = null
//    internal lateinit var ll_out: LinearLayout
//    internal lateinit var ll_play: LinearLayout
//    private lateinit var wmParams: WindowManager.LayoutParams
//    //创建浮动窗口设置布局参数的对象
//    private lateinit var mWindowManager: WindowManager
//    //浮动窗口内的按钮图片,Icon
//    internal lateinit var mFloatPlay: ImageView
//    private lateinit var mFloatIcon: ImageView
//    private lateinit var mFloatProgress: ProgressBar
//    //浮动窗口内的文字
//    private lateinit var mFloatText: TextView
//    //上一次的位置
//    private var preX: Int = 0
//    private var preY: Int = 0
//    //屏幕尺寸
//    private var mScreenWidth: Int = 0
//    private var mScreenHeight: Int = 0
//    private var floatBtnSize: Int = 0
//    private var floatBtnHeight: Int = 0
//    //表示滑动的时候，手的移动要大于这个距离才开始移动控件
//    private val mTouchSlop: Int = 0
//    //是否显示了浮动窗口
//    private var shown: Boolean = false
//    private var startX: Int = 0
//    private var startY: Int = 0
//    private var handler: Handler = Handler()
//    private lateinit var binding: ViewDataBinding
//    //前后台切换时，显示或者隐藏浮动窗口
//    private val loopRunnable = object : Runnable {
//        override fun run() {
//
//            if (isRunningForeground) {
//                if (ActivityStack.getInstance().currentActivity() == null) {
//                    showFloatView()
//                } else {
//                    hideFloatView()
//                }
//            } else {
//                hideFloatView()
//            }
//
//            handler.postDelayed(this, 500)
//        }
//    }
//
//    //判断是否为前台
//    private// 枚举进程
//    val isRunningForeground: Boolean
//        get() {
//            val activityManager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//            val appProcessInfos = activityManager.runningAppProcesses
//            for (appProcessInfo in appProcessInfos) {
//                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    if (appProcessInfo.processName == mContext.applicationInfo.processName) {
//                        return true
//                    }
//                }
//            }
//            return false
//        }
//
//    init {
//        initFloatView()
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private fun initFloatView() {
//        Lg.e("FloatViewKt", "initFloatView fmBean==$fmBean")
//        setParams(mContext)
//        //获取浮动窗口视图所在布局
//        binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(mContext), R.layout.float_window, null, false)
//        binding.setVariable(BR.fmBean, fmBean?.get())
//        binding.setVariable(BR.presenter, this)
//        binding.executePendingBindings()
//
////        mFloatLayout = LayoutInflater.from(mContext).inflate(R.layout.float_window, null) as LinearLayout
////        ll_out = mFloatLayout!!.findViewById(R.id.ll_out)
////        ll_play = mFloatLayout!!.findViewById(R.id.ll_play)
////        mFloatPlay = mFloatLayout!!.findViewById(R.id.float_play)
////        mFloatIcon = mFloatLayout!!.findViewById(R.id.float_icon)
////        mFloatText = mFloatLayout!!.findViewById(R.id.float_text)
//        mFloatLayout = binding.root.findViewById(R.id.float_window)
//        ll_out = binding.root.findViewById(R.id.ll_out)
//        ll_play = binding.root.findViewById(R.id.ll_play)
//        mFloatPlay = binding.root.findViewById(R.id.float_play)
//        mFloatIcon = binding.root.findViewById(R.id.float_icon)
//        mFloatText = binding.root.findViewById(R.id.float_text)
//        mFloatProgress = binding.root.findViewById(R.id.float_progressBar)
//
//        // 菜单宽度
//        floatBtnSize = mFloatIcon.layoutParams.width +
//                mFloatText.layoutParams.width + mFloatPlay.layoutParams.width
//        floatBtnHeight = mFloatIcon.layoutParams.height
//
//        //设置监听浮动窗口的触摸移动
//        ll_out.setOnTouchListener(this)
//        ll_play.setOnTouchListener(this)
//        mFloatText.isSelected = true
////        ll_out.setOnClickListener(this)
////        ll_play.setOnClickListener(this)
//    }
//
//    override fun onClick(view: View?) {
//        when (view?.id) {
//            R.id.ll_play -> {
//                Lg.e("FloatViewKt", "ll_play")
//                mFloatPlay.isSelected = !mFloatPlay.isSelected
//                val state = PlayServiceManager.getListenerState()
//                if (state == Constants.STATE_IDLE || state == Constants.STATE_PAUSE) {
//                    PlayServiceManager.play(fmBean?.get()!!, mContext)
//                } else {
//                    PlayServiceManager.pauseContinue()
//                }
//            }
//            R.id.ll_out -> {
//                mContext.jumpActivity(ListenerFMBeanActivity::class.java, fmBean?.get())
//                Lg.e("FloatViewKt", "ll_out")
//            }
//        }
//    }
//
//
//    //设置windows Params
//    private fun setParams(context: Activity) {
//        wmParams = WindowManager.LayoutParams()
//        mScreenWidth = context.resources.displayMetrics.widthPixels
//        mScreenHeight = context.resources.displayMetrics.heightPixels
//        //获取WindowManagerImpl.CompatModeWrapper
//        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        //设置window type
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST
//        } else {
//            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE
//        }
//
//        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
//        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//
//        val flags = context.window.attributes.flags
//        if (flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
//            // FULL_SCREEN
//            wmParams.flags = wmParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//        }
//        //设置图片格式，效果为背景透明
//        wmParams.format = PixelFormat.RGBA_8888
//
//        //调整悬浮窗显示的停靠位置为左侧置顶
//        wmParams.gravity = Gravity.START or Gravity.TOP
//        // 以屏幕左上角为原点，设置x、y初始值
//        wmParams.x = 0
//        wmParams.y = 200
//
//        //设置悬浮窗口长宽数据
//        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT
//        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT
//    }
//
//
//    fun showFloatView() {
//        //添加mFloatLayout
//        if (!shown && mFloatLayout != null && fmBean != null) {
//            Lg.e("FloatViewKt", "showFloatView")
//            mWindowManager.addView(mFloatLayout, wmParams)
//            mFloatPlay.isSelected = PlayServiceManager.isListenerFMBean()
//            handler.post(loopRunnable)
//            shown = true
//        }
//    }
//
//    fun setFMBean(mFMBean: FMBean) {
//        binding.setVariable(BR.fmBean, mFMBean)
//        fmBean?.set(mFMBean)
//        binding.setVariable(BR.presenter, this)
//        binding.executePendingBindings()
//    }
//
//    fun hideFloatView() {
//        if (shown && mFloatLayout != null) {
//            Lg.e("FloatViewKt", "hideFloatView")
//            mWindowManager.removeViewImmediate(mFloatLayout)
//            handler.removeCallbacks(loopRunnable)
//            shown = false
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun isListening(event: ListenEvent) {
//        mFloatPlay.isSelected = PlayServiceManager.isListenerFMBean()
//
//    }
//
//    fun destroyFloatView() {
//        if (mFloatLayout != null) {
//            hideFloatView()
//        }
//    }
//
//    override fun onTouch(v: View, event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN // 按下
//            -> {
//                startX = event.rawX.toInt()
//                preX = startX
//                startY = event.rawY.toInt()
//                preY = startY
//            }
//            MotionEvent.ACTION_MOVE // 滑动
//            -> {
//                Lg.e("FloatViewKt", " ACTION_MOVE")
//
//                val newX = event.rawX.toInt()
//                val newY = event.rawY.toInt()
//
//                val dx = newX - startX
//                val dy = newY - startY
//
//                if (Math.abs(dx) > mTouchSlop || Math.abs(dy) > mTouchSlop) {
//
//                    wmParams.x += dx
//                    wmParams.y += dy
//                    if (wmParams.x < 0) {
//                        wmParams.x = 0
//                    }
//                    if (wmParams.y < 0) {
//                        wmParams.y = 0
//                    }
//                    if (wmParams.x > mScreenWidth - floatBtnSize) {
//                        wmParams.x = mScreenWidth - floatBtnSize
//                    }
//                    if (wmParams.y > mScreenHeight - floatBtnHeight) {
//                        wmParams.y = mScreenHeight - floatBtnHeight
//                    }
//                    mWindowManager.updateViewLayout(mFloatLayout, wmParams)
//                    startX = event.rawX.toInt()
//                    startY = event.rawY.toInt()
//                }
//            }
//            MotionEvent.ACTION_UP // 松开
//            -> {
//                preX = event.rawX.toInt() - preX
//                preY = event.rawY.toInt() - preY
//                if (Math.abs(preX) > mTouchSlop || Math.abs(preY) > mTouchSlop) {
//                    Lg.e("FloatViewKt", " ACTION_UP")
//                    return true
//                }
//            }
//        }
//        return false
//    }
//}
