package com.zwhd.appbase.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.zwhd.appbase.R;
import com.zwhd.appbase.download.AppModel;

/**
 * Created by YI on 2015/12/11.
 */
public class ConfirmDialog extends BaseDialog {
    ConfirmCallBack confirmCallBack;
    public AppModel model;
    public ConfirmDialog(Context context, ConfirmCallBack confirmCallBack) {
        super(context);
        this.confirmCallBack = confirmCallBack;
        fullWindowCenter(context);
        view = View.inflate(context, R.layout.dialog_confirm, null);
        setContentView(view, layoutParams);
        view.findViewById(R.id.ok).setOnClickListener(this);
        view.findViewById(R.id.cancle).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ok:
                if (confirmCallBack != null) confirmCallBack.ok(this,model);
                break;
            case R.id.cancle:
                if (confirmCallBack != null) confirmCallBack.cancle(this,model);
                break;
        }
    }
    public interface ConfirmCallBack {
        public void ok(Dialog dialog,AppModel model);

        public void cancle(Dialog dialog,AppModel model);
    }
}
