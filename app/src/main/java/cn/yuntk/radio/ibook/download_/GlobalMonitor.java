/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.yuntk.radio.ibook.download_;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadMonitor;

import cn.yuntk.radio.ibook.bean.DownloadMusicInfo;
import cn.yuntk.radio.ibook.util.LogUtils;

/**
 * Created by Jacksgong on 1/19/16.
 */
public class GlobalMonitor implements FileDownloadMonitor.IMonitor {
    private volatile int markStart;
    private volatile int markOver;

    private final static class HolderClass {
        private final static GlobalMonitor INSTANCE = new GlobalMonitor();
    }

    public static GlobalMonitor getImpl() {
        return HolderClass.INSTANCE;
    }

    private final static String TAG = "GlobalMonitor";

    @Override
    public void onRequestStart(int count, boolean serial, FileDownloadListener lis) {
        markStart = 0;
        markOver = 0;
        LogUtils.showLog(TAG+":FileDownloadListener:onRequestStart:count"+count);
    }

    @Override
    public void onRequestStart(BaseDownloadTask task) {
    }

    @Override
    public void onTaskBegin(BaseDownloadTask task) {
        markStart++;
        DownloadMusicInfo info = (DownloadMusicInfo) task.getTag();
        LogUtils.showLog(TAG+":FileDownloadListener:onTaskBegin:"+info.getTitle());
    }

    @Override
    public void onTaskStarted(BaseDownloadTask task) {

    }

    @Override
    public void onTaskOver(BaseDownloadTask task) {
        markOver++;
        DownloadMusicInfo info = (DownloadMusicInfo) task.getTag();
        LogUtils.showLog(TAG+":FileDownloadListener:onTaskOver:"+info.getTitle());
    }

    public int getMarkStart() {
        return markStart;
    }

    public int getMarkOver() {
        return markOver;
    }

}