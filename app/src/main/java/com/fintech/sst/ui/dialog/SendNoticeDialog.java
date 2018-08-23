package com.fintech.sst.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.fintech.sst.R;
import com.fintech.sst.data.db.Notice;


public class SendNoticeDialog extends Dialog {
    private ClickListener clickListener;
    private Notice notice = new Notice();
    public SendNoticeDialog(@NonNull Context context, ClickListener clickListener) {
        super(context);
        this.clickListener = clickListener;
        setCanceledOnTouchOutside(false);
    }

    public SendNoticeDialog(@NonNull Context context, int themeResId, ClickListener clickListener) {
        super(context, themeResId);
        this.clickListener = clickListener;
        setCanceledOnTouchOutside(false);
    }

    protected SendNoticeDialog(@NonNull Context context , boolean cancelable, ClickListener clickListener, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.clickListener = clickListener;
        setCanceledOnTouchOutside(false);
        setCancelable(cancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_send_notice);

        RadioButton rb_ali = findViewById(R.id.rb_ali);
        RadioButton rb_wechat = findViewById(R.id.rb_wechat);
        EditText etAccount = findViewById(R.id.et_account);
        Button btnY = findViewById(R.id.btnY);
        Button btnN = findViewById(R.id.btnN);

        rb_ali.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
//                    notice.status
                }
            }
        });

//        btnRefresh.setOnClickWithCountDownListener(new CountDownButton.OnClickWithCountDown() {
//            @Override
//            public void onPre() {
//                setCancelable(false);
//            }
//
//            @Override
//            public void onClick(View v) {
//                if (clickListener!=null)
//                    clickListener.onRefresh();
//                dismiss();
//            }
//        });
//        btnDel.setOnClickWithCountDownListener(new CountDownButton.OnClickWithCountDown() {
//            @Override
//            public void onPre() {
//                setCancelable(false);
//            }
//            @Override
//            public void onClick(View v) {
//                if (clickListener!=null)
//                    clickListener.onDel();
//                dismiss();
//            }
//        });



        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int windowWidth = outMetrics.widthPixels;
        int windowHeight = outMetrics.heightPixels;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (windowWidth * 0.8); // 宽度设置为屏幕的一定比例大小
//        if (heightScale == 0) {
//            params.gravity = Gravity.CENTER;
//        } else {
//            params.gravity = Gravity.TOP;
//            params.y = (int) (windowHeight * heightScale); // 距离顶端高度设置为屏幕的一定比例大小
//        }
        getWindow().setAttributes(params);

    }

    public interface ClickListener{
        void onRefresh();
        void onDel();
    }

}
