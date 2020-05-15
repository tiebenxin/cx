package com.yanlong.im.utils.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.yanlong.im.MyAppLication;
import com.yanlong.im.utils.EmojBitmapCache;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.edit.span.DataBindingSpan;
import com.yanlong.im.utils.edit.span.RemoveOnDirtySpan;
import com.yanlong.im.view.face.FaceView;

import net.cb.cb.library.utils.SharedPreferencesUtil;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/15 0015
 * @description
 */
public class SpannableEmoj implements DataBindingSpan<String>, RemoveOnDirtySpan {
    private String text = null;
    public SpannableEmoj(String spanned) {
        this.text = spanned;
    }
    @Override
    public CharSequence getSpannedText() {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        if (fontSize == null) {
            fontSize = ExpressionUtil.DEFAULT_SIZE;
        }
        SpannableString sb = new SpannableString(text);
        Context context= MyAppLication.getInstance().getApplicationContext();
        Bitmap bitmap = EmojBitmapCache.getInstance().get(text, fontSize);
        if (bitmap == null) {
            if (FaceView.map_FaceEmoji.containsKey(text)) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), Integer.parseInt(FaceView.map_FaceEmoji.get(text).toString()));
                bitmap = Bitmap.createScaledBitmap(bitmap, ExpressionUtil.dip2px(context, fontSize), ExpressionUtil.dip2px(context, fontSize), true);
//				bitmap = getBitmapFromDrawable(context, Integer.parseInt(FaceView.map_FaceEmoji.get(key).toString()));
                EmojBitmapCache.getInstance().put(text, bitmap, fontSize);
            }
        }
        if (bitmap != null) {
            ImageSpan imageSpan = new ImageSpan(context, bitmap);
//				// 通过图片资源id来得到bitmap，用一个ImageSpan来包装
            // 计算该图片名字的长度，也就是要替换的字符串的长度
            // spannableString.setSpan(imageSpan, matcher.start(), end,
            // Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // 将该图片替换字符串中规定的位置中
            sb.setSpan(imageSpan, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 将该图片替换字符串中规定的位置中
        }
        return sb;
    }

    @Override
    public String bindingData() {
        return text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean isDirty(Spannable text) {
        int spanStart = text.getSpanStart(this);
        int spanEnd = text.getSpanEnd(this);
        return spanStart >= 0 && spanEnd >= 0 && text.subSequence(spanStart, spanEnd) != this.text;
    }
}
