package com.yanlong.im.user.ui.register;

import android.graphics.Color;
import android.view.View;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.luck.picture.lib.utils.DateUtil;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.RegisterDetailBean;
import com.yanlong.im.databinding.FragmentRegisterFirstBinding;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;

import java.util.Calendar;
import java.util.Date;


/**
 * @author Liszt
 * @date 2020/11/16
 * Description 性别，生日（10-100，默认20）
 */
public class RegisterDetailFirstFragment extends BaseRegisterFragment<FragmentRegisterFirstBinding> {

    private Calendar defaultCalendar;
    private RegisterDetailBean mDetailBean;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_register_first;
    }

    @Override
    public void init() {
        mViewBinding.ivLeft.setVisibility(View.INVISIBLE);
        mViewBinding.ivRight.setVisibility(View.VISIBLE);
        if (infoStat == 2) {//老用户
            mViewBinding.ivBack.setVisibility(View.VISIBLE);
            mViewBinding.tvTitle.setText("需要您完善个人资料");
        } else {//新用户
            mViewBinding.tvTitle.setText("您的资料");
            mViewBinding.ivBack.setVisibility(View.GONE);
        }
        RegisterDetailBean detailBean = ((RegisterDetailActivity) getActivity()).getDetailBean();
        if (detailBean.isBirthDayInit()) {
            defaultCalendar = Calendar.getInstance();
            defaultCalendar.setTimeInMillis(detailBean.getBirthday());
        } else {
            defaultCalendar = Calendar.getInstance();
            Calendar current = Calendar.getInstance();
            defaultCalendar.set(current.get(Calendar.YEAR) - 20, 0, 1);//默认20岁
        }
//        ((RegisterDetailActivity) getActivity()).getDetailBean().setBirthday(defaultCalendar.getTimeInMillis());
        if (mDetailBean != null) {
            updateDetailUI(mDetailBean);
        }

    }

    @Override
    public void initListener() {
        mViewBinding.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) {
                    return;
                }
                RegisterDetailBean bean = ((RegisterDetailActivity) getActivity()).getDetailBean();
                if (bean != null) {
                    if (bean.getSex() <= 0) {
                        ToastUtil.show("请选择性别");
                        return;
                    }
                    if (!bean.isBirthDayInit()) {
                        ToastUtil.show("请选择生日");
                        return;
                    }
                }
                if (listener != null) {
                    listener.onNext();
                }
            }
        });
        mViewBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onExit();
                }
            }
        });

        mViewBinding.llMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null || ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (mViewBinding.ivAvatarMan.isSelected()) {
                    return;
                } else {
                    changeSexUI(true);
                    ((RegisterDetailActivity) getActivity()).getDetailBean().setSex(CoreEnum.ESexType.MAN);
                }
            }
        });

        mViewBinding.llWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null || ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (mViewBinding.ivAvatarWoman.isSelected()) {
                    return;
                } else {
                    changeSexUI(false);
                    ((RegisterDetailActivity) getActivity()).getDetailBean().setSex(CoreEnum.ESexType.WOMAN);
                }
            }
        });

        mViewBinding.tvBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker();
            }
        });
    }


    //时间选择器。选择生日
    private void initTimePicker() {
        Calendar current = Calendar.getInstance();
        if (defaultCalendar == null) {
            Calendar defaultCalendar = Calendar.getInstance();
            defaultCalendar.set(current.get(Calendar.YEAR) - 20, 0, 1);//默认20岁
        } else {
            if (getActivity() != null && !((RegisterDetailActivity) getActivity()).getDetailBean().isBirthDayInit()) {
                defaultCalendar.setTimeInMillis(((RegisterDetailActivity) getActivity()).getDetailBean().getBirthday());
            }
        }
        Calendar start = Calendar.getInstance();
        start.set(current.get(Calendar.YEAR) - 100, 0, 1);//1920-1-1
        Calendar end = Calendar.getInstance();
        end.set(current.get(Calendar.YEAR) - 10, 11, 31);//2010-12-31

        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                ((RegisterDetailActivity) getActivity()).getDetailBean().setBirthday(calendar.getTimeInMillis());
                mViewBinding.tvBirthday.setText(DateUtil.formatDate(date, DateUtil.DATE_PATTERN_YMD_STANDARD_CHINESE));
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setDate(defaultCalendar)
                .setRangDate(start, end)
                .setCancelText("取消")
                .setCancelColor(Color.parseColor("#878787"))
                .setSubmitText("确定")
                .setSubmitColor(Color.parseColor("#32b152"))
                .build();
        pvTime.show();
    }


    @Override
    public void updateDetailUI(RegisterDetailBean bean) {
        if (bean == null) {
            return;
        }
        if (mViewBinding == null) {
            mDetailBean = bean;
            return;
        }
        if (bean.getSex() > 0) {
            if (bean.getSex() == CoreEnum.ESexType.MAN) {
                changeSexUI(true);
            } else if (bean.getSex() == CoreEnum.ESexType.WOMAN) {
                changeSexUI(false);
            }
        } else {
            mViewBinding.ivAvatarMan.setSelected(false);
            mViewBinding.ivAvatarWoman.setSelected(false);
            mViewBinding.ivAvatarMan.setImageResource(R.mipmap.ic_man_avatar_dark);
            mViewBinding.ivAvatarWoman.setImageResource(R.mipmap.ic_woman_avatar_dark);
        }
        if (bean.getBirthday() != -1) {
            mViewBinding.tvBirthday.setText(DateUtil.formatDate(bean.getBirthday(), DateUtil.DATE_PATTERN_YMD_STANDARD_CHINESE));
        }
    }

    public void changeSexUI(boolean isMan) {
        mViewBinding.ivAvatarMan.setSelected(isMan);
        mViewBinding.ivAvatarMan.setImageResource(isMan ? R.mipmap.ic_man_avatar_light : R.mipmap.ic_man_avatar_dark);
        mViewBinding.ivAvatarWoman.setSelected(!isMan);
        mViewBinding.ivAvatarWoman.setImageResource(isMan ? R.mipmap.ic_woman_avatar_dark : R.mipmap.ic_woman_avatar_light);
    }
}
