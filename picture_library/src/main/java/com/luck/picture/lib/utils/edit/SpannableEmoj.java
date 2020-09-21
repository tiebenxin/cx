package com.luck.picture.lib.utils.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.luck.picture.lib.face.FaceView;
import com.luck.picture.lib.utils.EmojBitmapCache;
import com.luck.picture.lib.utils.ExpressionUtil;
import com.luck.picture.lib.utils.edit.span.DataBindingSpan;
import com.luck.picture.lib.utils.edit.span.RemoveOnDirtySpan;


/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/15 0015
 * @description
 */
public class SpannableEmoj implements DataBindingSpan<String>, RemoveOnDirtySpan {
    private String text = null;
    private Context context;

    public SpannableEmoj(Context context, String spanned) {
        this.text = spanned;
        this.context = context;
    }

    @Override
    public CharSequence getSpannedText() {
        Integer fontSize = ExpressionUtil.DEFAULT_SIZE;
        SpannableString sb = new SpannableString(text);
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
