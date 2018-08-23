package com.fintech.sst.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;


public class CountDownButton extends android.support.v7.widget.AppCompatButton {
    private OnClickWithCountDown onClickWithCountDownListener;
    private int startTime = 60;
    private int currTime;
    private Timer timer;
    private int clickNum = 1;

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

    private void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (clickNum++ == 1) {
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
                                    if (currTime < 0) {
                                        setText(content);
                                        setEnabled(true);
                                        timer.cancel();
                                    }
                                }
                            });
                        }
                    }, 0, 1000);
                }else{
                    if (onClickWithCountDownListener!=null)
                        onClickWithCountDownListener.onClick(v);
                    clickNum = 1;
                }

            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (timer != null)
            timer.cancel();
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setOnClickWithCountDownListener(OnClickWithCountDown onClickWithCountDownListener) {
        this.onClickWithCountDownListener = onClickWithCountDownListener;
    }

    public interface OnClickWithCountDown {
        void onClick(View v);
    }
}
