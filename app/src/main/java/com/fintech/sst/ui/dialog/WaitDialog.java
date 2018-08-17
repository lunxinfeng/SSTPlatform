package com.fintech.sst.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.fintech.sst.R;


public class WaitDialog extends Dialog {

    public WaitDialog(Context context, int style) {
        super(context, style);
        init();
    }
    public WaitDialog(Context context, int style, String message) {
        super(context, style);
        init();

        TextView tv_text = findViewById(R.id.tv_text);
        tv_text.setText(message);
    }

    private void init() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.layout_loading);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

}