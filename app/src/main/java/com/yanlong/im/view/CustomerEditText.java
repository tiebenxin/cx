package com.yanlong.im.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.yanlong.im.utils.PatternUtil;
import com.yanlong.im.utils.edit.IRemovePredicate;
import com.yanlong.im.utils.edit.KeyCodeDeleteHelper;
import com.yanlong.im.utils.edit.NoCopySpanEditableFactory;
import com.yanlong.im.utils.edit.SpanFactory;
import com.yanlong.im.utils.edit.SpannableEmoj;
import com.yanlong.im.utils.edit.SpannableUser;
import com.yanlong.im.utils.edit.span.RemoveOnDirtySpan;
import com.yanlong.im.utils.edit.watcher.DirtySpanWatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 添加@
     *
     * @param showText 显示到界面的内容
     * @param userId   用户ID
     */
    public void addAtSpan(String maskText, String showText, long userId) {
        try {
            int start = getSelectionStart();
            SpannableStringBuilder sb = new SpannableStringBuilder(getText() == null ? "" : getText());
            if (TextUtils.isEmpty(maskText)) {//自己输入的@
                //移除掉上一个@
                sb.delete(start - 1, start);
                start = start - 1;
            }
            SpannableUser myTextSpan = new SpannableUser(showText, userId);
            Spannable spannable = SpanFactory.newSpannable( myTextSpan.getSpannedText(), myTextSpan);
            //binding @后面带一个空格
            sb.insert(start, spannable);
            setText(sb);
            setFocusable(true);
            setFocusableInTouchMode(true);
            requestFocus();
            //光标在最后
            setSelection(start + spannable.length());
        } catch (Exception e) {
        }
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
            if (user.bindingData() == 0L) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加Emoj表情,一个表情
     */
    public void addEmojSpan(String emojText) {
        try {
            int start = getSelectionStart();
            SpannableStringBuilder sb = new SpannableStringBuilder(getText() == null ? "" : getText());
            SpannableEmoj emoj = new SpannableEmoj(emojText);
            Spannable spannable = SpanFactory.newSpannable(emoj.getSpannedText(), emoj);
            sb.insert(start, spannable);
            setText(sb);
            setFocusable(true);
            setFocusableInTouchMode(true);
            requestFocus();
            //光标在最后
            setSelection(start + spannable.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
     *
     * @throws SecurityException
     * @throws NumberFormatException
     * @throws IllegalArgumentException
     */
    public void showDraftContent(String text) throws SecurityException,
            NumberFormatException, IllegalArgumentException {
        if(!TextUtils.isEmpty(text)) {
            String pattern = PatternUtil.PATTERN_FACE_EMOJI; // 正则表达式，用来判断消息内是否有表情
            Pattern patten = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
            Matcher matcher = patten.matcher(text);
            SpannableStringBuilder sb = new SpannableStringBuilder(text);
            while (matcher.find()) {
                String emojText = matcher.group();
                int start = matcher.start();
                int end = matcher.start() + emojText.length();
                SpannableEmoj emoj = new SpannableEmoj(emojText);
                Spannable spannable = SpanFactory.newSpannable(emoj.getSpannedText(), emoj);
//            sb.insert(start, spannable);
                sb.replace(start, end, spannable);
            }
            setText(sb);
        }
    }


    private void insert(String text) throws SecurityException,
            NumberFormatException, IllegalArgumentException {
        int index = this.getSelectionStart();
        if(!TextUtils.isEmpty(text)) {
            String pattern = PatternUtil.PATTERN_FACE_EMOJI; // 正则表达式，用来判断消息内是否有表情
            Pattern patten = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
            Matcher matcher = patten.matcher(text);
            SpannableStringBuilder sb = new SpannableStringBuilder(text);
            while (matcher.find()) {
                String emojText = matcher.group();
                int start = matcher.start();
                int end = matcher.start() + emojText.length();
                SpannableEmoj emoj = new SpannableEmoj(emojText);
                Spannable spannable = SpanFactory.newSpannable(emoj.getSpannedText(), emoj);
                sb.replace(start, end, spannable);
            }
            SpannableStringBuilder sbText = new SpannableStringBuilder(getText() == null ? "" : getText());
            sbText.insert(index,sb);
            setText(sbText);
            //光标在最后
            setSelection(index+sb.length());
        }
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
                    insert(value);
                } else {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    String value = clipboard.getText().toString();
                    // edit.clear();
                    insert(value);
                }
                return true;
            } catch (Exception e) {

            }
        }
        return super.onTextContextMenuItem(id);
    }
}
