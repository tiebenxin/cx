package com.yanlong.im.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.edit.IRemovePredicate;
import com.yanlong.im.utils.edit.KeyCodeDeleteHelper;
import com.yanlong.im.utils.edit.NoCopySpanEditableFactory;
import com.yanlong.im.utils.edit.SpanFactory;
import com.yanlong.im.utils.edit.SpannableUser;
import com.yanlong.im.utils.edit.span.RemoveOnDirtySpan;
import com.yanlong.im.utils.edit.watcher.DirtySpanWatcher;

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
        init();
    }

    public CustomerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setText(null);
        IRemovePredicate iRemovePredicate = new IRemovePredicate() {
            @Override
            public boolean isToRemove(Object object) {
                return object instanceof RemoveOnDirtySpan;
            }
        };
        this.setEditableFactory(new NoCopySpanEditableFactory(
                new DirtySpanWatcher(iRemovePredicate)));
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    KeyCodeDeleteHelper.onDelDown(((EditText) v).getText());
                }
                return false;
            }
        });
    }


    /**
     *
     *
     * @param showText 显示到界面的内容
     * @param userId   用户ID
     */
    public void addAtSpan(String maskText, String showText, long userId) {
        SpannableStringBuilder sb = new SpannableStringBuilder(getText()==null?"":getText());
        if (!TextUtils.isEmpty(maskText)) {//@显示
            SpannableUser myTextSpan = new SpannableUser(showText, userId);
            //binding @后面带一个空格
            sb.append(SpanFactory.newSpannable(maskText + myTextSpan.getSpannedText() + " ", myTextSpan));
        } else {
            SpannableUser myTextSpan = new SpannableUser(showText, userId);
            //移除掉上一个@
            sb.delete(sb.length() - 1, sb.length());
            //binding @后面带一个空格
            sb.append(SpanFactory.newSpannable("@" + myTextSpan.getSpannedText() + " ", myTextSpan));
        }
        setText(sb);
        //光标在最后
        setSelection(getSelectionEnd());
    }


    //获取用户Id集合
    public List<Long> getUserIdList() {
        List<Long> list = new ArrayList<>();
        SpannableUser[] spans = getText().getSpans(0, getText().length(), SpannableUser.class);
        for (SpannableUser user : spans) {
            list.add(user.bindingData());
        }
        return list;
    }


    public boolean isAtAll() {
        SpannableUser[] spans = getText().getSpans(0, getText().length(), SpannableUser.class);
        for (SpannableUser user : spans) {
            if (user.bindingData() == 0L ) {
                return true;
            }
        }
        return false;
    }


//    @Override
//    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
//        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        //向前删除一个字符，@后的内容必须大于一个字符，可以在后面加一个空格
//        if (lengthBefore == 1 && lengthAfter == 0) {
//            EditUser[] spans = getText().getSpans(0, getText().length(), EditUser.class);
//            for (EditUser myImageSpan : spans) {
//                if (getText().getSpanEnd(myImageSpan) == start && !text.toString().endsWith(myImageSpan.getShowText())) {
//                    getText().delete(getText().getSpanStart(myImageSpan), getText().getSpanEnd(myImageSpan));
//                    break;
//                }
//            }
//        }
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
//    }

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
}
