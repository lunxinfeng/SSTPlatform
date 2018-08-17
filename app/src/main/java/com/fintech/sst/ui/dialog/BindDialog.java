package com.fintech.sst.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.fintech.sst.R;


public class BindDialog extends Dialog {
    public static final int TYPE_LOGIN = 100;
    public static final int TYPE_BIND = 200;
    private ClickListener clickListener;
    private int type;
    public BindDialog(@NonNull Context context, int type, ClickListener clickListener) {
        super(context);
        this.clickListener = clickListener;
        this.type = type;
    }

    public BindDialog(@NonNull Context context, int type, int themeResId, ClickListener clickListener) {
        super(context, themeResId);
        this.clickListener = clickListener;
        this.type = type;
    }

    protected BindDialog(@NonNull Context context, int type , boolean cancelable, ClickListener clickListener, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.clickListener = clickListener;
        this.type = type;
        setCancelable(cancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_account);

        final EditText et_account = findViewById(R.id.et_account);
        final EditText et_password = findViewById(R.id.et_password);
        Button btnBind = findViewById(R.id.btnBind);
        switch (type){
            case TYPE_LOGIN:
                btnBind.setText("登陆");
                break;
            case TYPE_BIND:
                btnBind.setText("绑定");
                break;
        }

        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.onClick(et_account.getText().toString(),et_password.getText().toString());
                dismiss();
            }
        });



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
        void onClick(String name, String password);
    }

}
