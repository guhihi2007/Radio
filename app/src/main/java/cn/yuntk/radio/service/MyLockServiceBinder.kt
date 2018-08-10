package cn.yuntk.radio.service

import android.os.Binder

/**
 * Author : Gupingping
 * Date : 2018/7/17
 * Mail : gu12pp@163.com
 */
class MyLockServiceBinder(service: LockService) : Binder() {
    val mService = service
}