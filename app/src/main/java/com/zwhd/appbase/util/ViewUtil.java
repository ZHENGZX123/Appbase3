package com.zwhd.appbase.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zwhd.appbase.activity.BaseActivity;

/**
 * 视图工具集
 *
 * @author YI
 */
public class ViewUtil {
    private static Toast toast;

    /**
     * 获取 图像的位图并转化为位数组
     */
    public static byte[] getImageBytes(ImageView view, int pinzhi, int w) {
        try {
            Drawable drawable = view.getDrawable();
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicHeight(),
                    drawable.getIntrinsicHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            int h = (int) (((float) drawable.getIntrinsicHeight() / (float) drawable
                    .getIntrinsicHeight()) * w);
            drawable.setBounds(0, 0, h, h);
            drawable.draw(canvas);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, pinzhi, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 控件的位移动画
     */
    public static void moveFrontBg(View v, int startX, int toX, int startY,
                                   int toY) {
        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY,
                toY);
        anim.setDuration(200);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    public static void translateAnimation(View v, int startX, int toX,
                                          int startY, int toY) {

        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY,
                toY);
        anim.setDuration(500);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    /**
     * 图片的压缩     通用
     */
    public static Bitmap revitionImageSize(String path, int size)
            throws IOException {
        // 取得图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(path), null, options);
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= size)
                    && (options.outHeight >> i <= size)) {
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(new FileInputStream(path),
                        null, options);
                break;
            }
            i += 1;
        }
        if (bitmap == null) {
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(path),
                    null, options);
        }
        return bitmap;
    }







    public static Bitmap revitionImageSize(byte[] data, int size)
            throws IOException {
        // 取得图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= size)
                    && (options.outHeight >> i <= size)) {
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                        options);
                break;
            }
            i += 1;
        }
        if (bitmap == null) {
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                    options);
        }
        return bitmap;
    }

    public static Bitmap revitionImageSize(Context context, int rId, int size)
            throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), rId, options);
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= size)
                    && (options.outHeight >> i <= size)) {
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        rId, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }

    /**
     * 图像大小压缩
     */
    public static Bitmap compressImage(Bitmap image, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        int options = 90;
        while (baos.toByteArray().length / 1024 > size) {
            baos.reset();
            image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 消息提示
     *
     * @param context   上下文
     * @param messageId 消息资源ID
     */
    public static void showMessage(Context context, int messageId) {
        if (toast == null) {
            toast = Toast.makeText(context, messageId, Toast.LENGTH_LONG);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.setText(messageId);
        toast.show();
    }

    /**
     * 消息提示
     *
     * @param context 上下文
     * @param message 消息
     */
    public static void showMessage(Context context, String message) {
        if (StringUtil.isEmpty(message))
            return;
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.setText(message);
        toast.show();
    }

    /**
     * 获取视图控件
     *
     * @param activity 当前视图
     * @param id       控件ID
     */
    @SuppressWarnings("unchecked")
    public static <T> T findViewById(Activity activity, int id) {
        return (T) activity.findViewById(id);

    }

    @SuppressWarnings("unchecked")
    public static <T> T findViewById(View view, int id) {
        return (T) view.findViewById(id);

    }

    /**
     * 获取文本内容
     *
     * @param view 控件
     */
    public static String getContent(TextView view) {
        return view.getText().toString();
    }

    public static String getContent(BaseActivity context, int id) {
        return getContent((TextView) findViewById(context, id));

    }

    public static String getContent(View view, int id) {
        return getContent((TextView) findViewById(view, id));
    }

    /**
     * 设置文本
     */
    public static void setContent(TextView view, int rId) {
        if (view != null)
            view.setText(rId);
    }

    public static void setContent(TextView view, String str) {
        if (view != null)
            view.setText(str);
    }

    public static void setContent(TextView view, Spanned str) {
        if (view != null)
            view.setText(str);
    }

    public static void setContent(BaseActivity context, int dId, int rId) {
        TextView view = findViewById(context, dId);
        setContent(view, rId);
    }

    public static void setContent(View context, int dId, int rId) {
        TextView view = findViewById(context, dId);
        setContent(view, rId);
    }

    public static void setContent(BaseActivity context, int dId, String str) {
        TextView view = findViewById(context, dId);
        setContent(view, str);
    }

    public static void setContent(BaseActivity context, int dId, Spanned str) {
        TextView view = findViewById(context, dId);
        setContent(view, str);
    }

    public static void setContent(View context, int dId, String str) {
        TextView view = findViewById(context, dId);
        setContent(view, str);
    }

    /**
     * 设置文本提示
     */
    public static void setContentHint(TextView view, int rId) {
        if (view != null)
            view.setHint(rId);
    }

    public static void setContentHint(TextView view, String str) {
        if (view != null)
            view.setHint(str);
    }

    public static void setContentHint(BaseActivity context, int dId, int rId) {
        TextView view = findViewById(context, dId);
        setContentHint(view, rId);
    }

    public static void setContentHint(View context, int dId, int rId) {
        TextView view = findViewById(context, dId);
        setContentHint(view, rId);
    }

    public static void setContentHint(BaseActivity context, int dId, String str) {
        TextView view = findViewById(context, dId);
        setContentHint(view, str);
    }

    public static void setContentHint(View context, int dId, String str) {
        TextView view = findViewById(context, dId);
        setContentHint(view, str);
    }

    /**
     * 对比控件内容
     *
     * @param activity 上下文
     * @param ids      包含TextView控件ID的数组
     */
    public static boolean compare(BaseActivity activity, Integer... ids) {
        if (ids == null || ids.length == 0)
            return false;
        String str = getContent(activity, ids[0]);
        boolean bl = true;
        for (Integer id : ids) {
            if (!str.equals(getContent(activity, id))) {
                bl = false;
                break;
            }
        }
        return bl;
    }


    /**
     * 布局返回视图
     */
    @SuppressWarnings("unchecked")
    public static <T> T inflate(Context context, int layoutId) {
        try {
            return (T) LayoutInflater.from(context).inflate(layoutId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取 图像的位图并转化为位数组
     */
    public static byte[] getBytes(ImageView view) {
        try {
            Drawable drawable = view.getDrawable();
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, 100, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 隐藏软键盘
     *
     * @param act
     */
    public static void hideKeyboard(Activity act) {
        try {
            InputMethodManager imm = (InputMethodManager) act
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = act.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param act
     */
    public static void hideKeyboard(Dialog dialog, Activity act) {
        try {
            InputMethodManager imm = (InputMethodManager) act
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = dialog.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示软键盘
     *
     * @param act
     */
    public static void diaplyKeyboard(Activity act, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) act
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置文本域的文字颜色
     *
     * @param view      要设置颜色的文本控件
     * @param resources 资源管理器 可以NULL 为NULL时 corlor 需传递完整的颜色值
     * @param corlor    颜色值得ID
     * @param st        开始位置
     * @param end       结束位置
     */
    public static void setTextFontColor(TextView view, Resources resources,
                                        int corlor, int st, int end) throws Exception {
        SpannableStringBuilder builder = new SpannableStringBuilder(
                view.getText());
        int corlorValue = corlor;
        if (resources != null)
            corlorValue = resources.getColor(corlor);
        builder.setSpan(new ForegroundColorSpan(corlorValue), st, end,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        view.setText(builder);
    }

    /**
     * 设置控件周围的图形
     */
    public static void setArroundDrawable(TextView view, int rLeft, int rTop,
                                          int rRight, int rBottom) {
        Resources resources = view.getResources();
        Drawable left = null, top = null, right = null, bottom = null;
        if (rLeft != -1) {
            left = resources.getDrawable(rLeft);

        }
        if (rTop != -1) {
            top = resources.getDrawable(rTop);

        }
        if (rRight != -1) {
            right = resources.getDrawable(rRight);

        }
        if (rBottom != -1) {
            bottom = resources.getDrawable(rBottom);

        }
        view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    /**
     * 设置控件周围的图形
     */
    public static void setArroundDrawable(BaseActivity activity, int id,
                                          int rLeft, int rTop, int rRight, int rBottom) {
        TextView view = findViewById(activity, id);
        Resources resources = activity.getResources();
        Drawable left = null, top = null, right = null, bottom = null;
        if (rLeft != -1) {
            left = resources.getDrawable(rLeft);

        }
        if (rTop != -1) {
            top = resources.getDrawable(rTop);

        }
        if (rRight != -1) {
            right = resources.getDrawable(rRight);

        }
        if (rBottom != -1) {
            bottom = resources.getDrawable(rBottom);

        }
        view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    /**
     * 设置控件周围的图形
     */
    public static void setArroundDrawable(View v, int id, int rLeft, int rTop,
                                          int rRight, int rBottom) {
        TextView view = findViewById(v, id);
        Resources resources = v.getResources();
        Drawable left = null, top = null, right = null, bottom = null;
        if (rLeft != -1) {
            left = resources.getDrawable(rLeft);

        }
        if (rTop != -1) {
            top = resources.getDrawable(rTop);

        }
        if (rRight != -1) {
            right = resources.getDrawable(rRight);

        }
        if (rBottom != -1) {
            bottom = resources.getDrawable(rBottom);

        }
        view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    /**
     * 隐藏视图控件
     */
    public static void hideViews(int value, View... views) {
        if (views == null || views.length == 0)
            return;
        for (View view : views) {
            if (view != null)
                view.setVisibility(value);
        }
    }

    /**
     * 设置radiobutton的选择状态
     */
    public static void setCheckStatusRadioButton(Activity context, int rId,
                                                 boolean bl) {
        RadioButton button = findViewById(context, rId);
        if (button != null)
            button.setChecked(true);

    }

    public static void setCheckStatusRadioButton(Activity context, boolean bl,
                                                 int... ids) {
        if (ids != null && ids.length > 0) {
            for (int id : ids) {
                RadioButton button = findViewById(context, id);
                if (button != null)
                    button.setChecked(bl);
            }
        }
    }

    public static void setCheckStatusRadioButton(View context, int rId,
                                                 boolean bl) {
        RadioButton button = findViewById(context, rId);
        if (button != null)
            button.setChecked(true);
    }

    /**
     * 文本编辑域获取焦点 并选择是否弹出输入法
     *
     * @param editText  文本域
     * @param showInput 是否弹出输入法
     */
    public static void requestFoucus(EditText editText, boolean showInput) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        if (showInput) {
            InputMethodManager inputManager = (InputMethodManager) editText
                    .getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, 0);
        }
    }


}
