package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hm.cxpay.utils.DateUtils;
import com.yanlong.im.R;
import com.yanlong.im.adapter.AdapterBalanceLabel;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.MsgTagHandler;
import com.yanlong.im.chat.bean.AssistantMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.ui.view.ControllerLinearList;
import com.yanlong.im.chat.ui.view.YLinkMovementMethod;
import com.yanlong.im.utils.ExpressionUtil;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.WebPageActivity;

import java.util.regex.Matcher;

/*
 * 文本消息，@消息，小助手消息
 * */
public class ChatCellText extends ChatCellBase {

    private TextView tv_content;
    private TextView tvTitle;
    private TextView tvLoginTime;
    private TextView tvTemaName;
    private TextView tvDate;
    private LinearLayout llLabelParent;

    protected ChatCellText(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_content);
        tvTitle = getView().findViewById(R.id.tv_title);
        tvLoginTime = getView().findViewById(R.id.tv_login_time);
        tvTemaName = getView().findViewById(R.id.tv_tema_name);
        tvDate = getView().findViewById(R.id.tv_date);
        llLabelParent = getView().findViewById(R.id.ll_parent);

        //设置自定义文字大小
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        if (fontSize != null) {
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
        } else if (message.getMsg_type() == ChatEnum.EMessageType.ASSISTANT_NEW) {
            setNewAssistantMessage(message.getAssistantMessage());
        } else if (message.getMsg_type() == ChatEnum.EMessageType.TRANSFER_NOTICE) {
            tv_content.setText(Html.fromHtml(message.getTransferNoticeMessage().getContent(), null,
                    new MsgTagHandler(getContext(), true, message.getMsg_id(), actionTagClickListener)));
            tv_content.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void setNewAssistantMessage(AssistantMessage message) {
        tvTitle.setText(message.getTitle());
        if (message.getTime() != 0 && message.getTime() != -1) {
            tvLoginTime.setText(DateUtils.getFullTime(message.getTime()));
            tvLoginTime.setVisibility(View.VISIBLE);
        } else {
            tvLoginTime.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(message.getContent())) {
            tv_content.setText(message.getContent());
            tv_content.setVisibility(View.VISIBLE);
        } else {
            tv_content.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(message.getSignature())) {
            tvTemaName.setText(message.getSignature());
            tvTemaName.setVisibility(View.VISIBLE);
        } else {
            tvTemaName.setVisibility(View.GONE);
        }
        if (message.getSignature_time() != 0 && message.getSignature_time() != -1) {
            tvDate.setText(DateUtils.getFullTime(message.getSignature_time()));
            tvDate.setVisibility(View.VISIBLE);
        } else {
            tvDate.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(message.getItems()) || "[]".equals(message.getItems())) {
            llLabelParent.setVisibility(View.GONE);
        } else {
            llLabelParent.setVisibility(View.VISIBLE);
            tv_content.setVisibility(View.GONE);
            ControllerLinearList controller = new ControllerLinearList(llLabelParent);
            AdapterBalanceLabel adapterLabel = new AdapterBalanceLabel(message.getLabelItems(), getContext());
            controller.setAdapter(adapterLabel);
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
        int width = ScreenUtil.getScreenWidth(getContext());
        double maxWidth = 0.6 * width;
        if (maxWidth > 0 && tv_content != null) {
            tv_content.setMaxWidth((int) maxWidth);
            LogUtil.getLog().i("ChatCellText", "");
        }
    }
}
