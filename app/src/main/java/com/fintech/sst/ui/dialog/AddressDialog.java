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
import com.fintech.sst.net.Configuration;
import com.fintech.sst.net.Constants;


public class AddressDialog extends Dialog {
    private ClickListener clickListener;
    public AddressDialog(@NonNull Context context, ClickListener clickListener) {
        super(context);
        this.clickListener = clickListener;
    }

    public AddressDialog(@NonNull Context context, int themeResId, ClickListener clickListener) {
        super(context, themeResId);
        this.clickListener = clickListener;
    }

    protected AddressDialog(@NonNull Context context, boolean cancelable, ClickListener clickListener, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.clickListener = clickListener;
        setCancelable(cancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_address);

        final EditText etWebAddress = findViewById(R.id.etWebAddress);
        final EditText etNettyAddress = findViewById(R.id.etNettyAddress);
        Button btnApply = findViewById(R.id.btnApply);

        String web = Configuration.getUserInfoByKey(Constants.KEY_ADDRESS_WEB);
        String netty = Configuration.getUserInfoByKey(Constants.KEY_ADDRESS_NETTY);
        etWebAddress.setText(web.equals("")?Constants.baseUrl:web);
        etNettyAddress.setText(netty.equals("")?Constants.nettyAddress:netty);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.onClick(etWebAddress.getText().toString(),etNettyAddress.getText().toString());
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
        void onClick(String web, String netty);
    }

}
