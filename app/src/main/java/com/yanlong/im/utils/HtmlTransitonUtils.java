package com.yanlong.im.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import com.yanlong.im.chat.bean.HtmlBean;
import com.yanlong.im.user.ui.UserInfoActivity;



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
    private static final String TAG = "HtmlTransitonUtils";

    public SpannableStringBuilder getSpannableString(Context context, String html, int type) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        Log.v(TAG, "html---------------->" + html);
        if (!TextUtils.isEmpty(html)) {
            List<HtmlBean> list = htmlTransition(html);
            switch (type) {
                case 1:
                    setType1(context, style, list);
                    break;
                case 2:
                    setType2(context, style, list);
                    break;
                case 3:
                    setType3(context, style, list);
                    break;
                case 5:
                    setType5(context, style, list);
                    break;
                case 6:
                    setType6(context, style, list);
                    break;
                case 7:
                    setType7(context, style, list);
                    break;
                case 8:
                    setType8(context, style, list);
                    break;
                case 9:
                    setType9(context, style, list);
                    break;
            }

        }

        return style;
    }

    private void setType1(final Context context, SpannableStringBuilder builder, List<HtmlBean> list) {
        for (final HtmlBean bean : list) {
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
                        Intent intent = new Intent(context, UserInfoActivity.class);
                        intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
                        context.startActivity(intent);
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
        builder.append("通过扫");
        for (final HtmlBean bean : list) {
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
                        Intent intent = new Intent(context, UserInfoActivity.class);
                        intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
                        context.startActivity(intent);
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


    private void setType2(final Context context, SpannableStringBuilder builder, List<HtmlBean> list) {
        for (final HtmlBean bean : list) {
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
                        Intent intent = new Intent(context, UserInfoActivity.class);
                        intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
                        context.startActivity(intent);
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

        for (final HtmlBean bean : list) {
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
                        Intent intent = new Intent(context, UserInfoActivity.class);
                        intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
                        context.startActivity(intent);
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
        builder.append("加入了群聊");
    }


    private void setType3(final Context context, SpannableStringBuilder builder, List<HtmlBean> list) {
        builder.append("你将");
        for (final HtmlBean bean : list) {
            String content = "\"" + bean.getName() + "\"、";
            builder.append(content);
            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 2;
            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
                    context.startActivity(intent);
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
        builder.append("移出群聊");
    }

    private void setType5(final Context context, SpannableStringBuilder builder, List<HtmlBean> list) {
        for (final HtmlBean bean : list) {
            String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
                    context.startActivity(intent);
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


    private void setType6(final Context context, SpannableStringBuilder builder, List<HtmlBean> list) {
        for (final HtmlBean bean : list) {
            String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
                    context.startActivity(intent);
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


    private void setType7(final Context context, SpannableStringBuilder builder, List<HtmlBean> list) {
        for (final HtmlBean bean : list) {
            String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
                    context.startActivity(intent);
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
    }


    private void setType8(final Context context, SpannableStringBuilder builder, List<HtmlBean> list) {
        builder.append("你领取了");
        for (final HtmlBean bean : list) {
            String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
                    context.startActivity(intent);
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
    }


    private void setType9(final Context context, SpannableStringBuilder builder, List<HtmlBean> list) {
        for (final HtmlBean bean : list) {
            String content = "\"" + bean.getName() + "\"";
            builder.append(content);

            int state = builder.toString().length() - content.length() + 1;
            int end = builder.toString().length() - 1;

            ClickableSpan clickProtocol = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.ID, Long.valueOf(bean.getId()));
                    context.startActivity(intent);
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
        builder.append("撤回了一条消息");
    }


    private List<HtmlBean> htmlTransition(String html) {
        List<HtmlBean> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements fonts = doc.select("font");
        for (Element element : fonts) {
            HtmlBean bean = new HtmlBean();
            String id = element.id();
            if (!TextUtils.isEmpty(element.id())) {
                Log.v(TAG, "id------------>" + element.id());
            }
            String name = element.text();
            if (!TextUtils.isEmpty(element.val())) {
                bean.setType(Integer.valueOf(element.val()));
                Log.v(TAG, "type------------>" + element.val());
            }
            bean.setId(id);
            bean.setName(name);
            list.add(bean);
        }
        return list;
    }
}
