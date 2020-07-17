package com.yanlong.im.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;

import com.luck.picture.lib.tools.DoubleUtils;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.HtmlBean;
import com.yanlong.im.chat.bean.HtmlBeanList;
import com.yanlong.im.chat.interf.IActionTagClickListener;
import com.yanlong.im.user.ui.UserInfoActivity;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建人 shenxin
 * @创建时间 2019/8/16 0016 15:00
 */
public class HtmlTransitonUtils {

    public SpannableStringBuilder getSpannableString(Context context, String html, int type) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(html)) {
            HtmlBean bean = htmlTransition(html);
            switch (type) {
                case ChatEnum.ENoticeType.ENTER_BY_QRCODE: //二维码分享进群
                    setType1(context, style, bean);
                    break;
                case ChatEnum.ENoticeType.INVITED: //邀请进群
                    setType2(context, style, bean);
                    break;
                case ChatEnum.ENoticeType.KICK: //群主移出群聊
                    setType3(context, style, bean);
                    break;
                case ChatEnum.ENoticeType.TRANSFER_GROUP_OWNER: //群主转让
                    setType5(context, style, bean);
                    break;
                case ChatEnum.ENoticeType.LEAVE: //离开群聊
                    setType6(context, style, bean);
                    break;
                case ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED: // xxx领取了你的云红包
                    setType7(context, style, bean);
                    break;
                case ChatEnum.ENoticeType.RECEIVE_RED_ENVELOPE: // 你领取的xxx的云红包
                    setType8(context, style, bean);
                    break;
                case ChatEnum.ENoticeType.CANCEL: //消息撤回
                    setType9(context, style, bean);
                    break;
                case ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF://自己领取了自己的云红包

                    break;
                case ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED: // xxx领取了你的云红包
                    setTypeEnvelopSend(context, style, bean, 1);
                    break;
                case ChatEnum.ENoticeType.RECEIVE_SYS_ENVELOPE: // 你领取的xxx的云红包
                    setTypeEnvelopeReceived(context, style, bean, 1);
                    break;
                case ChatEnum.ENoticeType.NO_FRI_ERROR://被好友删除，消息发送失败
                case ChatEnum.ENoticeType.OPEN_UP_RED_ENVELOPER:// 领取群红包
                case ChatEnum.ENoticeType.FORBIDDEN_WORDS_SINGE:// 单人禁言
                case ChatEnum.ENoticeType.GROUP_OTHER_REMOVE:// 其它人被移出群
                case ChatEnum.ENoticeType.FORBIDDEN_WORDS_OPEN:// 群禁言
                case ChatEnum.ENoticeType.FORBIDDEN_WORDS_CLOSE:// 群禁言
                case ChatEnum.ENoticeType.CHANGE_VICE_ADMINS_ADD://群管理变更通知
                case ChatEnum.ENoticeType.CHANGE_VICE_ADMINS_CANCEL_OTHER:// 群管理变更通知 自己取消其他人
                case ChatEnum.ENoticeType.CHANGE_VICE_ADMINS_CANCEL:// 群管理变更通知 自己被取消
                    Spanned spannedHtml = Html.fromHtml(html);
                    // subSequence 是去掉换行
                    SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(spannedHtml.subSequence(0, spannedHtml.length() - 2));
                    URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
                    for (int i = 0; i < urls.length; i++) {
                        setLinkClickable(context, clickableHtmlBuilder, urls[i], bean.getList().get(i).getId(), bean.getGid());
                    }
                    return clickableHtmlBuilder;
                case ChatEnum.ENoticeType.CANCEL_CAN_EDIT://撤销能重新编辑

                    break;
            }
        }
        return style;
    }

    /**
     * 设置点击超链接对应的处理内容
     */
    private void setLinkClickable(Context context, SpannableStringBuilder clickableHtmlBuilder, URLSpan urlSpan, final String id, String gid) {
        int start = clickableHtmlBuilder.getSpanStart(urlSpan);
        int end = clickableHtmlBuilder.getSpanEnd(urlSpan);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                goToUserInfoActivity(context, Long.valueOf(id), gid, true);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);// 取消下划线
                ds.setColor(Color.parseColor("#276baa"));// 设置文本颜色
            }
        };

        clickableHtmlBuilder.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        clickableHtmlBuilder.removeSpan(urlSpan);// //解决方法 自定义ClickableSpan无效问题
    }

    public SpannableStringBuilder getSpannableString(Context context, String html, int type, IActionTagClickListener listener) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(html)) {
            HtmlBean bean = htmlTransition(html);
            switch (type) {
                case ChatEnum.ENoticeType.LOCK://端到端加密
                    setType12(context, style, bean, listener);
                    break;
                case ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED: // xxx领取了你的云红包
                    setTypeEnvelopSend(context, style, bean, 1);
                    break;
                case ChatEnum.ENoticeType.RECEIVE_SYS_ENVELOPE: // 你领取的xxx的云红包
                    setTypeEnvelopeReceived(context, style, bean, 1);
                    break;
                case ChatEnum.ENoticeType.NO_FRI_ERROR://被好友删除，消息发送失败
                case ChatEnum.ENoticeType.OPEN_UP_RED_ENVELOPER:// 领取群红包
                case ChatEnum.ENoticeType.FORBIDDEN_WORDS_SINGE:// 单人禁言
                case ChatEnum.ENoticeType.GROUP_OTHER_REMOVE:// 其它人被移出群
                case ChatEnum.ENoticeType.FORBIDDEN_WORDS_OPEN:// 群禁言
                case ChatEnum.ENoticeType.FORBIDDEN_WORDS_CLOSE:// 群禁言
                case ChatEnum.ENoticeType.CHANGE_VICE_ADMINS_ADD://群管理变更通知
                case ChatEnum.ENoticeType.CHANGE_VICE_ADMINS_CANCEL_OTHER:// 群管理变更通知 自己取消其他人
                case ChatEnum.ENoticeType.CHANGE_VICE_ADMINS_CANCEL:// 群管理变更通知 自己被取消
                    Spanned spannedHtml = Html.fromHtml(html);
                    SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(spannedHtml.subSequence(0, spannedHtml.length() - 2));
                    URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
                    for (int i = 0; i < urls.length; i++) {
                        setLinkClickable(context, clickableHtmlBuilder, urls[i], bean.getList().get(i).getId(), bean.getGid());
                    }
                    return clickableHtmlBuilder;
                case ChatEnum.ENoticeType.CANCEL_CAN_EDIT:// 撤销能重新编辑

                    break;
            }
        }
        return style;
    }

    private void setType1(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean) {
        List<HtmlBeanList> list = htmlBean.getList();

        for (final HtmlBeanList bean : list) {
            if (bean.getType() == 1) {
                builder.append("你、");
            } else if (bean.getType() == 2) {
                String content = "\"" + bean.getName() + "\"、";
                builder.append(content);

                int state = builder.toString().length() - content.length() + 1;
                int end = builder.toString().length() - 2;

                ClickableSpan clickProtocol = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                };
                builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
                builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        //最后一个名字，不显示、号
        if (builder.charAt(builder.length() - 1) == '、')
            builder.delete(builder.length() - 1, builder.length());
        builder.append("通过扫");
        for (final HtmlBeanList bean : list) {
            if (bean.getType() == 3) {
                builder.append("你");
            } else if (bean.getType() == 4) {
                String content = "\"" + bean.getName() + "\"";
                builder.append(content);

                int state = builder.toString().length() - content.length() + 1;
                int end = builder.toString().length() - 1;

                ClickableSpan clickProtocol = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                };
                builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
                builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        builder.append("分享的二维码加入了群聊");
    }

    private void setType2(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean) {
        List<HtmlBeanList> list = htmlBean.getList();
        for (final HtmlBeanList bean : list) {
            if (bean.getType() == 3) {
                builder.append("你");
            } else if (bean.getType() == 4) {
                String content = "\"" + bean.getName() + "\"";
                builder.append(content);

                int state = builder.toString().length() - content.length() + 1;
                int end = builder.toString().length() - 1;

                ClickableSpan clickProtocol = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                };
                builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
                builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        builder.append("邀请");

        for (final HtmlBeanList bean : list) {
            if (bean.getType() == 1) {
                builder.append("你、");
            } else if (bean.getType() == 2) {
                String content = "\"" + bean.getName() + "\"、";
                builder.append(content);

                int state = builder.toString().length() - content.length() + 1;
                int end = builder.toString().length() - 2;

                ClickableSpan clickProtocol = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                };
                builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
                builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        //最后一个名字，不显示、号
        if (builder.charAt(builder.length() - 1) == '、')
            builder.delete(builder.length() - 1, builder.length());
        builder.append("加入了群聊");
    }

    private void setType3(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean) {
        List<HtmlBeanList> list = htmlBean.getList();
        builder.append("你将");
        for (final HtmlBeanList bean : list) {
            String content = "\"" + bean.getName() + "\"、";
            builder.append(content);
            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 2;
            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }

            };
            builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
            builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.delete(builder.length() - 1, builder.length());
        builder.append("移出群聊");
    }

    private void setType5(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean) {
        List<HtmlBeanList> list = htmlBean.getList();
        for (final HtmlBeanList bean : list) {
            String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }

            };
            builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
            builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.append("已成为新群主");
    }

    private void setType6(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean) {
        List<HtmlBeanList> list = htmlBean.getList();
        for (final HtmlBeanList bean : list) {
            String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }

            };
            builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
            builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.append("离开群聊");
    }

    private void setType7(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean) {
        List<HtmlBeanList> list = htmlBean.getList();
        for (final HtmlBeanList bean : list) {
            String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }

            };
            builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
            builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.append("领取了你的云红包");
        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#cc5944"));
        builder.setSpan(protocolColorSpan, builder.toString().length() - 3, builder.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setType8(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean) {
        List<HtmlBeanList> list = htmlBean.getList();
        builder.append("你领取了");
        for (final HtmlBeanList bean : list) {
            String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }

            };
            builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
            builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.append("的云红包");
        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#cc5944"));
        builder.setSpan(protocolColorSpan, builder.toString().length() - 3, builder.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setType9(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean) {
        List<HtmlBeanList> list = htmlBean.getList();
        for (final HtmlBeanList bean : list) {
            final String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
//                    Intent intent = new Intent(context, UserInfoActivity.class);
//                    intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
//                    intent.putExtra(UserInfoActivity.JION_TYPE_SHOW, 1);
//                    context.startActivity(intent);
//                    goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }

            };
            builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
//            builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.append("撤回了一条消息");
    }

    //端到端加密
    private void setType12(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean, IActionTagClickListener listener) {
        builder.append("聊天中所有信息已进行");
        String content = "端对端加密";
        builder.append(content);
        int start, end;
        start = builder.toString().length() - content.length();
        end = builder.toString().length();

        ClickableSpan clickProtocol = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                listener.clickLock();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }

        };
        builder.setSpan(clickProtocol, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#1f5305"));
        builder.setSpan(protocolColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("保护");

    }

    private void goToUserInfoActivity(Context context, Long id, String gid, boolean isGroup) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
        context.startActivity(new Intent(context, UserInfoActivity.class)
                .putExtra(UserInfoActivity.ID, id)
                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1)
                .putExtra(UserInfoActivity.GID, gid)
                .putExtra(UserInfoActivity.IS_GROUP, isGroup));
    }

    private HtmlBean htmlTransition(String html) {
        HtmlBean htmlBean = new HtmlBean();
        List<HtmlBeanList> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements fonts = doc.select("font");
        for (Element element : fonts) {
            HtmlBeanList bean = new HtmlBeanList();
            String id = element.id();
            if (!TextUtils.isEmpty(element.id())) {
//                LogUtil.getLog().e(TAG, "id------------>" + element.id());
                bean.setId(id);
            }
            String name = element.text();
            if (!TextUtils.isEmpty(element.val())) {
                bean.setType(Integer.valueOf(element.val()));
//                LogUtil.getLog().e(TAG, "type------------>" + element.val());
            }
            bean.setId(id);
            bean.setName(name);
            list.add(bean);
        }
        htmlBean.setList(list);
        Elements divs = doc.select("div");
        if (divs != null && divs.size() > 0) {
            for (int i = 0; i < divs.size(); i++) {
//                LogUtil.getLog().e(TAG, "gid------------>" + divs.get(i).id());
                htmlBean.setGid(divs.get(i).id());
            }
        }

        return htmlBean;
    }

    //别人领取你的
    private void setTypeEnvelopSend(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean, int envelopeType) {
        String envelopeName = envelopeType == 0 ? "云红包" : "零钱红包";
        List<HtmlBeanList> list = htmlBean.getList();
        for (final HtmlBeanList bean : list) {
            String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }

            };
            builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
            builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.append("领取了你的").append(envelopeName);
        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#cc5944"));
        builder.setSpan(protocolColorSpan, builder.toString().length() - 3, builder.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //你领取
    private void setTypeEnvelopeReceived(final Context context, SpannableStringBuilder builder, final HtmlBean htmlBean, int envelopeType) {
        String envelopeName = envelopeType == 0 ? "云红包" : "零钱红包";
        List<HtmlBeanList> list = htmlBean.getList();
        builder.append("你领取了");
        for (final HtmlBeanList bean : list) {
            String content = "\"" + bean.getName() + "\"";
            if ("你".equals(bean.getName())) {
                builder.append(bean.getName());
            } else {
                builder.append(content);
            }

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;
            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    goToUserInfoActivity(context, Long.valueOf(bean.getId()), htmlBean.getGid(), false);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }

            };
            builder.setSpan(clickProtocol, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
            builder.setSpan(protocolColorSpan, state, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.append("的").append(envelopeName);
        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#cc5944"));
        builder.setSpan(protocolColorSpan, builder.toString().length() - 3, builder.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}

