package com.yanlong.im.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.util.AttributeSet;

import com.yanlong.im.utils.ExpressionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-11-23
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class CustomerEditText extends AppCompatEditText {

    public CustomerEditText(Context context) {
        super(context);
    }

    public CustomerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private StringBuilder builder;

    /**
     * 添加一个块,在文字的后面添加
     *
     * @param showText 显示到界面的内容
     * @param userId   用户ID
     */
    public void addAtSpan(String maskText, String showText, long userId) {
        builder = new StringBuilder();
        if (!TextUtils.isEmpty(maskText)) {
            //已经添加了@
            builder.append(maskText).append(showText).append(" ");
        } else {
            builder.append(showText).append(" ");
        }
        getText().insert(getSelectionStart(), builder.toString());
        SpannableString sps = new SpannableString(getText());

        int start = getSelectionEnd() - builder.toString().length() - (TextUtils.isEmpty(maskText) ? 1 : 0);
        int end = getSelectionEnd();
        makeSpan(sps, new UnSpanText(start, end, builder.toString()), userId);
        setText(sps);
        setSelection(end);
    }

    //获取用户Id列表
    public String getUserIdString() {
        MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
        StringBuilder builder = new StringBuilder();
        for (MyTextSpan myTextSpan : spans) {
            String realText = getText().toString().substring(getText().getSpanStart(myTextSpan), getText().getSpanEnd(myTextSpan));
            String showText = myTextSpan.getShowText();
            if (realText.equals(showText)) {
                builder.append(myTextSpan.getUserId()).append(",");
            }
        }
        if (!TextUtils.isEmpty(builder.toString())) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    //获取用户Id集合
    public List<Long> getUserIdList() {
        List<Long> list = new ArrayList<>();
        MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
        for (MyTextSpan myTextSpan : spans) {
            String realText = getText().toString().substring(getText().getSpanStart(myTextSpan), getText().getSpanEnd(myTextSpan));
            String showText = myTextSpan.getShowText();
            if (realText.contains(showText)) {
                list.add(myTextSpan.getUserId());
            }
        }
        return list;
    }


    public boolean isAtAll() {
        MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
        for (MyTextSpan myTextSpan : spans) {
            if (myTextSpan.getUserId() == 0) {
                return true;
            }
        }
        return false;
    }


    //生成一个需要整体删除的Span
    private void makeSpan(Spannable sps, UnSpanText unSpanText, long userId) {
        MyTextSpan what = new MyTextSpan(unSpanText.returnText, userId);
        int start = unSpanText.start;
        int end = unSpanText.end;
        sps.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        //向前删除一个字符，@后的内容必须大于一个字符，可以在后面加一个空格
        if (lengthBefore == 1 && lengthAfter == 0) {
            MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
            for (MyTextSpan myImageSpan : spans) {
                if (getText().getSpanEnd(myImageSpan) == start && !text.toString().endsWith(myImageSpan.getShowText())) {
                    getText().delete(getText().getSpanStart(myImageSpan), getText().getSpanEnd(myImageSpan));
                    break;
                }
            }
        }
//        if (lengthBefore>lengthAfter) {//删除操作，即字符减少
//            //具体的操作代码
//            // 获取光标的位置。如果在最末，则同字符串长度
//            // 光标之前至少有一个字符。尽管显示的是图片，其实内容仍是字符
//            if (start > 0) {
//                String body = this.getText().toString();
//                    // 包括起始位置，不包括结束位置
//                    String substring = body.substring(0, start);
//                    // 预提取光标前最后一个表情的位置
//                    int i = substring.lastIndexOf("[");
//                    // 提取到了
//                    if (i != -1) {
//                        // 从预提取位置到光标直接的字符
//                        CharSequence cs = substring.subSequence(i, start);
//                        // 是不是表情占位符
//                        if (FaceView.map_FaceEmoji.containsKey(cs.toString()+"]")) {
//                            // 是，就删除完整占位符
//                            this.getEditableText().delete(i, start);
//                        }
//                    }
//            }
//        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            try {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    String value = clipboard.getText().toString();
                    Editable edit = getEditableText();
                    // edit.clear();
                    int index = this.getSelectionStart();
                    if (index < 0 || index >= edit.length()) {
                        edit.append(ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SIZE, value));
                    } else {
                        edit.insert(index, ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SIZE, value));// 光标所在位置插入文字
                    }

                } else {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    String value = clipboard.getText().toString();
                    Editable edit = getEditableText();
                    // edit.clear();
                    int index = this.getSelectionStart();
                    if (index < 0 || index >= edit.length()) {
                        edit.append(ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SIZE, value));
                    } else {
                        edit.insert(index, ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SIZE, value));// 光标所在位置插入文字
                    }
                }
                return true;
            } catch (Exception e) {

            }
        }
        return super.onTextContextMenuItem(id);
    }

    private class MyTextSpan extends MetricAffectingSpan {
        private String showText;
        private long userId;

        public MyTextSpan(String showText, long userId) {
            this.showText = showText;
            this.userId = userId;
        }


        public String getShowText() {
            return showText;
        }

        public long getUserId() {
            return userId;
        }

        @Override
        public void updateMeasureState(TextPaint p) {

        }

        @Override
        public void updateDrawState(TextPaint tp) {

        }
    }

    private class UnSpanText {
        int start;
        int end;
        String returnText;

        UnSpanText(int start, int end, String returnText) {
            this.start = start;
            this.end = end;
            this.returnText = returnText;
        }
    }


}
