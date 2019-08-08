package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.WebPageActivity;

import java.util.regex.Matcher;

public class ChatCellText extends ChatCellBase {

    private TextView tv_content;

    protected ChatCellText(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        super(context, cellLayout, listener, adapter, viewGroup);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_content);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        setText(message.getChat().getMsg());
    }

    private void setText(String msg) {
        if (!StringUtil.isNotNull(msg)) {
            return;
        }
        try {
            Matcher matcher = StringUtil.URL.matcher(msg);
            int i = 0;
            int preLast = 0;
            int len = msg.length();
            SpannableStringBuilder builder = new SpannableStringBuilder();
            while (matcher.find()) {
                System.out.println();
                int groupCount = matcher.groupCount();
                if (groupCount > 0) {
                    int start = matcher.start();
                    int end = matcher.end();
                    if (i == 0) {
                        if (start != 0) {
                            builder.append(msg.substring(0, start));
                            builder.append(setClickableSpan(msg.substring(start, end)));
                        } else {
                            builder.append(setClickableSpan(msg.substring(start, end)));
                        }
                    } else {
                        if (end != len - 1) {
                            builder.append(msg.substring(preLast, start));
                            builder.append(setClickableSpan(msg.substring(start, end)));
                        }
                    }
                    preLast = end;
                }
                i++;
            }
            if (preLast != 0) {
                builder.append(msg.substring(preLast));
                tv_content.setText(builder);
                tv_content.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                tv_content.setText(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv_content.setText("文本解析异常");
        }

    }

    private SpannableString setClickableSpan(final String url) {
        LogUtil.getLog().i(this.getClass().getSimpleName(), url);
        SpannableString span = new SpannableString(url);
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@androidx.annotation.NonNull View view) {
                Intent intent = new Intent(getContext(), WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL, url);
                getContext().startActivity(intent);
            }
        }, 0, url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }
}
