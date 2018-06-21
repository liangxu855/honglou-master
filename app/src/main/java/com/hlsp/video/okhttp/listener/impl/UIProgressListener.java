package com.hlsp.video.okhttp.listener.impl;

import android.os.Handler;
import android.os.Message;

import com.hlsp.video.okhttp.listener.ProgressListener;
import com.hlsp.video.okhttp.listener.impl.handler.ProgressHandler;
import com.hlsp.video.okhttp.listener.impl.model.ProgressModel;


/**
 * 请求体回调实现类，用于UI层回调
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-02
 * Time: 22:34
 */
public abstract class UIProgressListener implements ProgressListener {
    private boolean isFirst = false;

    //处理UI层的Handler子类
    private static class UIHandler extends ProgressHandler {
        public UIHandler(UIProgressListener uiProgressListener) {
            super(uiProgressListener);
        }

        @Override
        public void start(UIProgressListener uiProgressListener, long currentBytes, long contentLength, boolean done) {
            if (uiProgressListener!=null) {
                uiProgressListener.onUIStart(currentBytes, contentLength, done);
            }
        }

        @Override
        public void progress(UIProgressListener uiProgressListener, long currentBytes, long contentLength, boolean done) {
            if (uiProgressListener!=null){
                uiProgressListener.onUIProgress(currentBytes, contentLength, done);
            }
        }

        @Override
        public void finish(UIProgressListener uiProgressListener, long currentBytes, long contentLength, boolean done) {
            if (uiProgressListener!=null){
                uiProgressListener.onUIFinish(currentBytes, contentLength,done);
            }
        }
    }

    //主线程Handler
    private final Handler mHandler = new UIHandler(this);

    @Override
    public void onProgress(long bytesWrite, long contentLength, boolean done) {
        //如果是第一次，发送消息
        if (!isFirst) {
            isFirst = true;
            Message start = Message.obtain();
            start.obj = new ProgressModel(bytesWrite, contentLength, done);
            start.what = ProgressHandler.START;
            mHandler.sendMessage(start);
        }

        //通过Handler发送进度消息
        Message message = Message.obtain();
        message.obj = new ProgressModel(bytesWrite, contentLength, done);
        message.what = ProgressHandler.UPDATE;
        mHandler.sendMessage(message);

        if(done) {
            Message finish = Message.obtain();
            finish.obj = new ProgressModel(bytesWrite, contentLength, done);
            finish.what = ProgressHandler.FINISH;
            mHandler.sendMessage(finish);
        }
    }

    /**
     * UI层回调抽象方法
     *
     * @param currentBytes    当前的字节长度
     * @param contentLength 总字节长度
     * @param done          是否写入完成
     */
    public abstract void onUIProgress(long currentBytes, long contentLength, boolean done);

    /**
     * UI层开始请求回调方法
     * @param currentBytes 当前的字节长度
     * @param contentLength 总字节长度
     * @param done 是否写入完成
     */
    public void onUIStart(long currentBytes, long contentLength, boolean done) {

    }

    /**
     * UI层结束请求回调方法
     * @param currentBytes 当前的字节长度
     * @param contentLength 总字节长度
     * @param done 是否写入完成
     */
    public void onUIFinish(long currentBytes, long contentLength, boolean done) {

    }
}
