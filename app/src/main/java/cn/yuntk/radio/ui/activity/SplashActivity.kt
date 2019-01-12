package cn.yuntk.radio.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContentResolverCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import cn.yuntk.radio.Constants
import cn.yuntk.radio.R
import cn.yuntk.radio.R.id.*
import cn.yuntk.radio.ad.AdController
import cn.yuntk.radio.utils.jumpActivity
import cn.yuntk.radio.utils.lg
import cn.yuntk.radio.utils.toast
import cn.yuntk.radio.view.widget.AuthorityDialog
import cn.yuntk.radio.view.widget.SetPermissionDialog
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * Author : Gupingping
 * Date : 2018/10/25
 * QQ : 464955343
 */
class SplashActivity : AppCompatActivity() {

    private var builder: AdController? = null
    private val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"
    private val PHONE = "android.permission.READ_PHONE_STATE"
    private val LOCATION = "android.permission.ACCESS_FINE_LOCATION"
    private val FOREGROUND = "android.permission.FOREGROUND_SERVICE"
    private val REQUEST_PERMISSION_CODE = 100
    private var isForResult: Boolean = false
    private val FORRESULT_CODE: Int = 400
    private val STORAGE_MESSAGE: String = "存储空间"
    private var canJump = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        askPermissions()
    }


    private fun askPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (hasStoragePermission() && hasPhonePermission() && hasLocationPermission() && hasForegroundPermission()) {
                builder = AdController.Builder(this)
                        .setPage(Constants.START_PAGE)
                        .setContainer(splash_container)
                        .setSkipView(skip_view)
                        .setSplashHolder(splash_holder)
                        .setLogo(app_logo)
                        .create()
                builder?.show()
                return
            } else {
                val dialog = object : AuthorityDialog(this) {
                    override fun onClick(v: View?) {
                        sendRequest()
                        dismiss()
                        return
                    }
                }
                dialog.show()
            }
        } else if (hasStoragePermission() && hasPhonePermission()) {
//            XApplication.isFromStart = true
            builder = AdController.Builder(this)
                    .setPage(Constants.START_PAGE)
                    .setContainer(splash_container)
                    .setSkipView(skip_view)
                    .setSplashHolder(splash_holder)
                    .setLogo(app_logo)
                    .create()
            builder?.show()
        } else {
            val dialog = object : AuthorityDialog(this) {
                override fun onClick(v: View?) {
                    sendRequest()
                    dismiss()
                    return
                }
            }
            dialog.show()
        }
    }

    private fun hasStoragePermission(): Boolean {
        return AndPermission.hasPermission(this, WRITE_EXTERNAL_STORAGE)
    }

    private fun hasPhonePermission(): Boolean {
        return AndPermission.hasPermission(this, PHONE)
    }

    private fun hasLocationPermission(): Boolean {
        return AndPermission.hasPermission(this, LOCATION)
    }

    private fun hasForegroundPermission(): Boolean {
        return AndPermission.hasPermission(this, FOREGROUND)
    }

    private fun sendRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            AndPermission.with(this).requestCode(REQUEST_PERMISSION_CODE).permission(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.FOREGROUND_SERVICE
                    , Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION
            ).callback(permissionListener).start()

        } else {
            AndPermission.with(this).requestCode(REQUEST_PERMISSION_CODE).permission(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ).callback(permissionListener).start()
        }

    }

    private val permissionListener = object : PermissionListener {
        override fun onSucceed(requestCode: Int, grantPermissions: List<String>) {
            val hasStoragePermission = hasStoragePermission()
            if (requestCode == REQUEST_PERMISSION_CODE) {
                if (hasStoragePermission) {
                    checkIn()
                }
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
            if (requestCode == REQUEST_PERMISSION_CODE) {
                lg("拒绝权限回调")
                val hasStoragePermission = hasStoragePermission()
                val isDeniedStorage = deniedPermissions.contains(WRITE_EXTERNAL_STORAGE)
                if (AndPermission.hasAlwaysDeniedPermission(this@SplashActivity, deniedPermissions) && isDeniedStorage) {
                    var dialog: SetPermissionDialog? = null
                    lg("永久拒绝存储，弹出相应权限提示")
                    dialog = SetPermissionDialog(this@SplashActivity, FORRESULT_CODE, STORAGE_MESSAGE)
                    dialog.show()
                    isForResult = true
                    return
                }
                if (hasStoragePermission) {
                    //拒绝电话权限，有存储权限，可以启动
                    checkIn()
                } else {

                    object : SetPermissionDialog(this@SplashActivity, REQUEST_PERMISSION_CODE, STORAGE_MESSAGE) {
                        override fun onClick(v: View) {
                            sendRequest()
                            dismiss()
                        }
                    }.show()

                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        canJump = false
    }


    override fun onResume() {
        super.onResume()
        if (canJump) {
            next()
        }
        canJump = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //勾选了永久拒绝，从设置返回才会进入
        if (requestCode == FORRESULT_CODE && isForResult) {
            //设置里拒绝存储权限
            if (!hasStoragePermission()) {
                SetPermissionDialog(this@SplashActivity, FORRESULT_CODE, STORAGE_MESSAGE).show()
                return
            }
            //设置里开启存储权限
            checkIn()
        }
    }

    fun checkIn() {
        jumpActivity(MainActivity::class.java, null)
        finish()
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private operator fun next() {
        if (canJump) {
            checkIn()
        } else {
            canJump = true
        }
    }

    /** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费  */

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        builder?.destroy()
    }
}
