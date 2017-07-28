package com.zwhd.appbase.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import com.zwhd.appbase.R;
import com.zwhd.appbase.activity.BaseActivity;

public class BaseDialog extends Dialog implements OnClickListener {
    public View view;
    protected Context context;
    protected LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    public BaseDialog(Context context) {
        this(context, R.style.LoadingDialog);
    }

    public BaseDialog(Context context, int style) {
        super(context, style);
        this.context = context;

    }

    @Override
    public void onClick(View v) {

    }
    protected void fullWindowCenter(Context context) {
        layoutParams = getWindow().getAttributes();
        BaseActivity ba = (BaseActivity) context;
        Rect rect = new Rect();
        View v = getWindow().getDecorView();
        v.getWindowVisibleDisplayFrame(rect);
        layoutParams.width = ba.displayMetrics.widthPixels;
    }
}
