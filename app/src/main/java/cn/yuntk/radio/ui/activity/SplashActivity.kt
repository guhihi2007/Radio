package cn.yuntk.radio.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import cn.yuntk.radio.R
import cn.yuntk.radio.utils.jumpActivity
import cn.yuntk.radio.utils.log
import cn.yuntk.radio.view.widget.AuthorityDialog
import cn.yuntk.radio.view.widget.SetPermissionDialog
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener

/**
 * Author : Gupingping
 * Date : 2018/8/8
 * Mail : gu12pp@163.com
 */
class SplashActivity : AppCompatActivity() {
    private val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"
    private val REQUEST_PERMISSION_CODE = 100
    private var isForResult: Boolean = false
    private val FORRESULT_CODE: Int = 400
    private val STORAGE_MESSAGE: String = "存储空间"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        askPermissions()
    }

    private fun askPermissions() {
        if (hasStoragePermission()) {
            checkIn()
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

    private fun sendRequest() {
        AndPermission.with(this).requestCode(REQUEST_PERMISSION_CODE).permission(
//                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(permissionListener).start()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else
            super.onKeyDown(keyCode, event)
    }

    private fun hasStoragePermission(): Boolean {
        return AndPermission.hasPermission(this, WRITE_EXTERNAL_STORAGE)
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
                log("拒绝权限回调")
                val hasStoragePermission = hasStoragePermission()
                val isDeniedStorage = deniedPermissions.contains(WRITE_EXTERNAL_STORAGE)
                if (AndPermission.hasAlwaysDeniedPermission(this@SplashActivity, deniedPermissions) && isDeniedStorage) {
                    var dialog: SetPermissionDialog? = null
                    log("永久拒绝存储，弹出相应权限提示")
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

    private fun checkIn() {
        Handler(mainLooper).postDelayed({
            jumpActivity(MainActivity::class.java, null)
            finish()
        }, 1000)
    }
}

