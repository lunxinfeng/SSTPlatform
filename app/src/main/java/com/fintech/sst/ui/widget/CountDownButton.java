package com.fintech.sst.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;


public class CountDownButton extends android.support.v7.widget.AppCompatButton {
    private OnClickWithCountDown onClickWithCountDownListener;
    private int startTime = 300;
    private int currTime;
    private Timer timer;

    public CountDownButton(Context context) {
        super(context);
        init();
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage("此操作不可逆，是否确认执行")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (onClickWithCountDownListener!=null)
                                    onClickWithCountDownListener.onPre();
                                setEnabled(false);
                                final String content = getText().toString();
                                currTime = startTime;
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        post(new Runnable() {
                                            @Override
                                            public void run() {
                                                setText(content + "(" + currTime + ")");
                                                currTime--;
                                                if (currTime<0){
                                                    setText(content);
                                                    setEnabled(true);
                                                    timer.cancel();
                                                    if (onClickWithCountDownListener!=null)
                                                        onClickWithCountDownListener.onClick(v);
                                                }
                                            }
                                        });
                                    }
                                },0,1000);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (timer!=null)
            timer.cancel();
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setOnClickWithCountDownListener(OnClickWithCountDown onClickWithCountDownListener) {
        this.onClickWithCountDownListener = onClickWithCountDownListener;
    }

    public interface OnClickWithCountDown{
        void onPre();
        void onClick(View v);
    }
}
