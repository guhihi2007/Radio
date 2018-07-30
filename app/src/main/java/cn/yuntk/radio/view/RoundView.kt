package cn.yuntk.radio.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import cn.yuntk.radio.R
import cn.yuntk.radio.utils.ImageUtils

/**
 * Author : Gupingping
 * Date : 2018/7/19
 * Mail : gu12pp@163.com
 */
class RoundView : View {
    private val UPDATE = 50L
    private val ROTATION_INCREASE = 1.0f
    private val ROTATION_PLAY = 0.0f
    private val ROTATION_PAUSE = -25.0f
    private val mHandler = Handler()
    private var mCoverBitmap: Bitmap = ImageUtils.getBitmap(context, R.mipmap.playimage)
    private val mCoverMatrix = Matrix()
    private var mPlayAnimator: ValueAnimator = ValueAnimator.ofFloat(ROTATION_PAUSE, ROTATION_PLAY)
    private var mPauseAnimator: ValueAnimator = ValueAnimator.ofFloat(ROTATION_PLAY, ROTATION_PAUSE)
    private var mDiscRotation = 0.0f
    private var isPlaying = false

    // 图片起始坐标
    private val mCoverPoint = Point()
    // 旋转中心坐标
    private val mCoverCenterPoint = Point()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mCoverBitmap = ImageUtils.resizeImage(mCoverBitmap,mCoverBitmap.width/2,mCoverBitmap.height/2)

        mCoverBitmap = ImageUtils.createCircleImage(mCoverBitmap)
        mPlayAnimator.duration = 1000
        mPauseAnimator.duration = 1000
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initSize()
    }

    /**
     * 确定图片起始坐标与旋转中心坐标
     */
    private fun initSize() {
        mCoverPoint.x = (width - mCoverBitmap.width) / 2
        mCoverPoint.y = (height - mCoverBitmap.height) / 2
        mCoverCenterPoint.x = width / 2
        mCoverCenterPoint.y = height / 2
    }

    override fun onDraw(canvas: Canvas) {
        mCoverMatrix.setRotate(mDiscRotation, mCoverCenterPoint.x.toFloat(), mCoverCenterPoint.y.toFloat())
        mCoverMatrix.preTranslate(mCoverPoint.x.toFloat(), mCoverPoint.y.toFloat())
        canvas.drawBitmap(mCoverBitmap, mCoverMatrix, null)
    }

    fun setCoverBitmap(context: Context, resId: Int = R.mipmap.playimage) {
        mCoverBitmap = ImageUtils.getBitmap(context, resId)
        mDiscRotation = 0.0f
        invalidate()
    }

    fun start() {
        if (isPlaying) {
            return
        }
        isPlaying = true
        mHandler.post(mRotationRunnable)
        mPlayAnimator.start()
    }

    fun pause() {
        if (!isPlaying) {
            return
        }
        isPlaying = false
        mHandler.removeCallbacks(mRotationRunnable)
        mPauseAnimator.start()
    }

    fun isRunning(): Boolean {
        return isPlaying
    }

    private val mRotationRunnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                mDiscRotation += ROTATION_INCREASE
                if (mDiscRotation >= 360) {
                    mDiscRotation = 0f
                }
                invalidate()
            }
            mHandler.postDelayed(this, UPDATE)
        }
    }

}
