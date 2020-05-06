package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.ui.view.YLinkMovementMethod;
import com.yanlong.im.utils.ExpressionUtil;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ScreenUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.WebPageActivity;

import java.util.regex.Matcher;

/*
 * 文本消息，@消息，小助手消息
 * */
public class ChatCellText extends ChatCellBase {

    private TextView tv_content;

    protected ChatCellText(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_content);
        //设置自定义文字大小
        Integer fontSize=new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        if(fontSize!=null){
            tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        updateWidth();
        if (message.getMsg_type() == ChatEnum.EMessageType.TEXT) {
//            setText(message.getChat().getMsg());
            tv_content.setText(getSpan(message.getChat().getMsg()));
        } else if (message.getMsg_type() == ChatEnum.EMessageType.AT) {
//            setText(message.getAtMessage().getMsg());
            tv_content.setText(getSpan(message.getAtMessage().getMsg()));
        } else if (message.getMsg_type() == ChatEnum.EMessageType.ASSISTANT) {
            setText(message.getAssistantMessage().getMsg());
        }
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
                LogUtil.getLog().d("a=", "====");
                int groupCount = matcher.groupCount();
                if (groupCount >= 0) {
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
                tv_content.setMovementMethod(YLinkMovementMethod.getInstance());
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

    private SpannableString getSpan(String msg) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        SpannableString spannableString = null;
        if (fontSize != null) {
            spannableString = ExpressionUtil.getExpressionString(getContext(), fontSize.intValue(), msg);
        } else {
            spannableString = ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SIZE, msg);
        }
        return spannableString;
    }

    private void updateWidth() {
        int width = ScreenUtils.getScreenWidth(getContext());
        double maxWidth = 0.6 * width;
        if (maxWidth > 0 && tv_content != null) {
            tv_content.setMaxWidth((int) maxWidth);
            LogUtil.getLog().i("ChatCellText","");
        }
    }
}
