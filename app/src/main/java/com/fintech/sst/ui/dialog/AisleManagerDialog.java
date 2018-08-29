package com.fintech.sst.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.fintech.sst.R;
import com.fintech.sst.ui.widget.CountDownButton;


public class AisleManagerDialog extends Dialog {
    private ClickListener clickListener;
    public AisleManagerDialog(@NonNull Context context, ClickListener clickListener) {
        super(context);
        this.clickListener = clickListener;
        setCanceledOnTouchOutside(false);
    }

    public AisleManagerDialog(@NonNull Context context, int themeResId, ClickListener clickListener) {
        super(context, themeResId);
        this.clickListener = clickListener;
        setCanceledOnTouchOutside(false);
    }

    protected AisleManagerDialog(@NonNull Context context , boolean cancelable, ClickListener clickListener, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.clickListener = clickListener;
        setCanceledOnTouchOutside(false);
        setCancelable(cancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_aisle_manager);

        CountDownButton btnRefresh = findViewById(R.id.btnRefresh);
        CountDownButton btnDel = findViewById(R.id.btnDel);

        btnRefresh.setOnClickWithCountDownListener(new CountDownButton.OnClickWithCountDown() {

            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.onRefresh();
                dismiss();
            }
        });
        btnDel.setOnClickWithCountDownListener(new CountDownButton.OnClickWithCountDown() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.onDel();
                dismiss();
            }
        });



        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int windowWidth = outMetrics.widthPixels;
        int windowHeight = outMetrics.heightPixels;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (windowWidth * 0.95); // 宽度设置为屏幕的一定比例大小
//        if (heightScale == 0) {
//            params.gravity = Gravity.CENTER;
//        } else {
//            params.gravity = Gravity.TOP;
//            params.y = (int) (windowHeight * heightScale); // 距离顶端高度设置为屏幕的一定比例大小
//        }
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public interface ClickListener{
        void onRefresh();
        void onDel();
    }

}
