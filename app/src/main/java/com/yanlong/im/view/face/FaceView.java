package com.yanlong.im.view.face;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.example.nim_lib.config.Preferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.view.face.adapter.ViewPagerAdapter;
import com.yanlong.im.view.face.bean.FaceBean;

import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SpUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-11-15
 * @updateAuthor
 * @updateDate
 * @description 表情视图
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class FaceView extends RelativeLayout {

    /**
     * 上下文
     */
    private Context context;
    /**
     * 父视图
     */
    private View view_Parent;
    /**
     * 表情列表切换控件
     */
    private ViewPager mViewPager;
    /**
     * 表情列表切换适配器
     */
    private ViewPagerAdapter adapter;
    /**
     * 表情切换列表
     */
    private ArrayList<View> list_Views;
    /**
     * 分页显示圆点
     */
    private RadioGroup mRadioGroup;
    /**
     * 显示选择表情类型
     */
    private RadioGroup select_RadioGroup;
    /**
     * 表情点击事件监听
     */
    private FaceViewPager.FaceClickListener faceClickListener;
    /**
     * 表情点击事件监听
     */
    private OnClickListener mOnDeleteListener;
    /**
     * 表情长按事件监听
     */
    private FaceViewPager.FaceLongClickListener faceLongClickListener;
    /**
     * 默认选择位置
     */
    private int mCheckPostion = 0;
    private int mEmojiCheckPostion = 0;
    /**
     * 页数
     */
    private int pagerCount = -1;
    /**
     * 页大小
     */
    private int pagerSize = 20;
    /**
     * 经常使用保留大小
     */
    private final int OFTEN_USE_MAX = 20;
    /**
     * emoji表情组
     */
    public static String face_emoji = "emoji";
    /**
     * 动态动漫组
     */
    public static String face_animo = "animo";
    /**
     * 自定义表情组
     */
    public static String face_custom = "custom";
    /**
     * 当前选中页
     */
    private static FaceViewPager mCurViewPager;

    /**
     * 表情类型
     */
    @IntDef({FaceView.FaceType.FACE_EMOJI_TAB, FaceView.FaceType.FACE_ANIMO_TAB, FaceView.FaceType.FACE_CUSTOM_TAB, FaceView.FaceType.FACE_PIG,
            FaceView.FaceType.FACE_MAMMON, FaceView.FaceType.FACE_PANDA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FaceType {
        int FACE_EMOJI_TAB = 0;// 通用emoji表情
        int FACE_ANIMO_TAB = 1;// 经常使用的emoji表情
        int FACE_CUSTOM_TAB = 2;// 收藏的表情
        int FACE_PIG = 3;// 大图 猪
        int FACE_MAMMON = 4;// 大图 财神
        int FACE_PANDA = 5;// 大图 熊猫
    }

    /**
     * emoji表情ID列表
     */
    private static int[] face_EmojiIds = {R.mipmap.emoji_000, R.mipmap.emoji_001, R.mipmap.emoji_002, R.mipmap.emoji_003,
            R.mipmap.emoji_004, R.mipmap.emoji_005, R.mipmap.emoji_006, R.mipmap.emoji_007, R.mipmap.emoji_008, R.mipmap.emoji_009,
            R.mipmap.emoji_010, R.mipmap.emoji_011, R.mipmap.emoji_012, R.mipmap.emoji_013, R.mipmap.emoji_014, R.mipmap.emoji_015,
            R.mipmap.emoji_016, R.mipmap.emoji_017, R.mipmap.emoji_018, R.mipmap.emoji_019, R.mipmap.emoji_020, R.mipmap.emoji_021,
            R.mipmap.emoji_022, R.mipmap.emoji_023, R.mipmap.emoji_024, R.mipmap.emoji_025, R.mipmap.emoji_026, R.mipmap.emoji_027,
            R.mipmap.emoji_028, R.mipmap.emoji_029, R.mipmap.emoji_030, R.mipmap.emoji_031, R.mipmap.emoji_032, R.mipmap.emoji_033,
            R.mipmap.emoji_034, R.mipmap.emoji_035, R.mipmap.emoji_036, R.mipmap.emoji_037, R.mipmap.emoji_038, R.mipmap.emoji_039,
            R.mipmap.emoji_040, R.mipmap.emoji_041, R.mipmap.emoji_042, R.mipmap.emoji_043, R.mipmap.emoji_044, R.mipmap.emoji_045,
            R.mipmap.emoji_046, R.mipmap.emoji_047, R.mipmap.emoji_048, R.mipmap.emoji_049, R.mipmap.emoji_050, R.mipmap.emoji_051,
            R.mipmap.emoji_052, R.mipmap.emoji_053, R.mipmap.emoji_054, R.mipmap.emoji_055, R.mipmap.emoji_056, R.mipmap.emoji_057,
            R.mipmap.emoji_058, R.mipmap.emoji_059, R.mipmap.emoji_060, R.mipmap.emoji_061, R.mipmap.emoji_062, R.mipmap.emoji_063,
            R.mipmap.emoji_064, R.mipmap.emoji_065, R.mipmap.emoji_066, R.mipmap.emoji_067, R.mipmap.emoji_068, R.mipmap.emoji_069,
            R.mipmap.emoji_070, R.mipmap.emoji_071, R.mipmap.emoji_072, R.mipmap.emoji_073, R.mipmap.emoji_074, R.mipmap.emoji_075,
            R.mipmap.emoji_076, R.mipmap.emoji_077, R.mipmap.emoji_078, R.mipmap.emoji_079, R.mipmap.emoji_080, R.mipmap.emoji_081,
            R.mipmap.emoji_082, R.mipmap.emoji_083, R.mipmap.emoji_084, R.mipmap.emoji_085, R.mipmap.emoji_086, R.mipmap.emoji_087,
            R.mipmap.emoji_088, R.mipmap.emoji_089, R.mipmap.emoji_090, R.mipmap.emoji_091, R.mipmap.emoji_092, R.mipmap.emoji_093,
            R.mipmap.emoji_094, R.mipmap.emoji_095, R.mipmap.emoji_096, R.mipmap.emoji_097, R.mipmap.emoji_098, R.mipmap.emoji_099,};

    /**
     * emoji表情名称
     */
    private static String[] face_EmojiNames = {"[emoji_000]", "[emoji_001]", "[emoji_002]", "[emoji_003]", "[emoji_004]", "[emoji_005]",
            "[emoji_006]", "[emoji_007]", "[emoji_008]", "[emoji_009]", "[emoji_010]", "[emoji_011]", "[emoji_012]", "[emoji_013]", "[emoji_014]",
            "[emoji_015]", "[emoji_016]", "[emoji_017]", "[emoji_018]", "[emoji_019]", "[emoji_020]", "[emoji_021]", "[emoji_022]", "[emoji_023]",
            "[emoji_024]", "[emoji_025]", "[emoji_026]", "[emoji_027]", "[emoji_028]", "[emoji_029]", "[emoji_030]", "[emoji_031]", "[emoji_032]",
            "[emoji_033]", "[emoji_034]", "[emoji_035]", "[emoji_036]", "[emoji_037]", "[emoji_038]", "[emoji_039]", "[emoji_040]", "[emoji_041]",
            "[emoji_042]", "[emoji_043]", "[emoji_044]", "[emoji_045]", "[emoji_046]", "[emoji_047]", "[emoji_048]", "[emoji_049]", "[emoji_050]",
            "[emoji_051]", "[emoji_052]", "[emoji_053]", "[emoji_054]", "[emoji_055]", "[emoji_056]", "[emoji_057]", "[emoji_058]", "[emoji_059]",
            "[emoji_060]", "[emoji_061]", "[emoji_062]", "[emoji_063]", "[emoji_064]", "[emoji_065]", "[emoji_066]", "[emoji_067]", "[emoji_068]",
            "[emoji_069]", "[emoji_070]", "[emoji_071]", "[emoji_072]", "[emoji_073]", "[emoji_074]", "[emoji_075]", "[emoji_076]", "[emoji_077]",
            "[emoji_078]", "[emoji_079]", "[emoji_080]", "[emoji_081]", "[emoji_082]", "[emoji_083]", "[emoji_084]", "[emoji_085]", "[emoji_086]",
            "[emoji_087]", "[emoji_088]", "[emoji_089]", "[emoji_090]", "[emoji_091]", "[emoji_092]", "[emoji_093]", "[emoji_094]", "[emoji_095]",
            "[emoji_096]", "[emoji_097]", "[emoji_098]", "[emoji_099]",};
    /**
     * 动态表情ID列表
     */
    private static int[] animo_emojiIds = {R.mipmap.animation_emoti_000, R.mipmap.animation_emoti_001, R.mipmap.animation_emoti_002,
            R.mipmap.animation_emoti_003, R.mipmap.animation_emoti_004, R.mipmap.animation_emoti_005, R.mipmap.animation_emoti_006, R.mipmap.animation_emoti_007,};
    /**
     * 动态表情名称
     */
    private static String[] animo_emojiNames = {"animation_emoti_000.png", "animation_emoti_001.png", "animation_emoti_002.png", "animation_emoti_003.png",
            "animation_emoti_004.png", "animation_emoti_005.png", "animation_emoti_006.png", "animation_emoti_007.png",};
    /**
     * 动态表情ID列表
     */
    private static int[] animo_mamonIds = {R.mipmap.animation_mamon_000, R.mipmap.animation_mamon_001, R.mipmap.animation_mamon_002,
            R.mipmap.animation_mamon_003, R.mipmap.animation_mamon_004, R.mipmap.animation_mamon_005, R.mipmap.animation_mamon_006, R.mipmap.animation_mamon_007,};
    /**
     * 动态表情名称
     */
    private static String[] animo_mamonNames = {"animation_mamon_000.png", "animation_mamon_001.png", "animation_mamon_002.png", "animation_mamon_003.png",
            "animation_mamon_004.png", "animation_mamon_005.png", "animation_mamon_006.png", "animation_mamon_007.png",};

    /**
     * 动态表情ID列表
     */
    private static int[] animo_pandaIds = {R.mipmap.animation_panda_000, R.mipmap.animation_panda_001, R.mipmap.animation_panda_002,
            R.mipmap.animation_panda_003, R.mipmap.animation_panda_004, R.mipmap.animation_panda_005, R.mipmap.animation_panda_006, R.mipmap.animation_panda_007,};
    /**
     * 动态表情名称
     */
    private static String[] animo_pandaNames = {"animation_panda_000.png", "animation_panda_001.png", "animation_panda_002.png", "animation_panda_003.png",
            "animation_panda_004.png", "animation_panda_005.png", "animation_panda_006.png", "animation_panda_007.png",};

    /**
     * 当前页表情列表
     */
    private ArrayList<FaceBean> list_CurrentBeans = new ArrayList<FaceBean>();
    public static ArrayList<FaceBean> oftenUseBeans = new ArrayList<>();// 经常使用emoji表情列表
    public static ArrayList<FaceBean> tempBeans = new ArrayList<>();
    public static ArrayList<FaceBean> emojiUseBeans = new ArrayList<>();// emoji表情列表
    public static ArrayList<FaceBean> collectFaceBeans = new ArrayList<>();// 收藏表情列表
    public static ArrayList<FaceBean> pigFaceBeans = new ArrayList<>();// 猪表情列表
    public static ArrayList<FaceBean> mamonFaceBeans = new ArrayList<>();// 财神表情列表
    public static ArrayList<FaceBean> pandaFaceBeans = new ArrayList<>();// 熊猫表情列表
    public static String mUid = "";// 记录当前登录用户uid,切换账号是需要重新获取收藏表情
    /**
     * emoji 表情键值对，key表情名称，value表情对应的图片资源Id
     */
    public static HashMap<String, Object> map_FaceEmoji;

    public FaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FaceView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 初始化界面
     *
     * @param context
     * @version 1.0
     * @createTime 2013-10-20,下午4:51:48
     * @updateTime 2013-10-20,下午4:51:48
     * @createAuthor CodeApe
     * @updateAuthor CodeApe
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void init(Context context) {
        this.context = context;

        view_Parent = LayoutInflater.from(this.context).inflate(R.layout.view_face, null);
        this.addView(view_Parent);

        mViewPager = view_Parent.findViewById(R.id.view_face_viewpager);
        mRadioGroup = findViewById(R.id.view_face_radiogroup);
        select_RadioGroup = findViewById(R.id.view_face_select);
        initFaceList(FaceView.FaceType.FACE_EMOJI_TAB);
        widgetListener();
    }

    /**
     * 初始化表情键值对
     *
     * @version 1.0
     * @createTime 2013-11-24,上午12:11:11
     * @updateTime 2013-11-24,上午12:11:11
     * @createAuthor CodeApe
     * @updateAuthor CodeApe
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static void initFaceMap() {
        map_FaceEmoji = new HashMap<String, Object>();
        emojiUseBeans.clear();
        pigFaceBeans.clear();
        mamonFaceBeans.clear();
        pandaFaceBeans.clear();
        collectFaceBeans.clear();
        for (int i = 0; i < face_EmojiIds.length; i++) {
            map_FaceEmoji.put(face_EmojiNames[i], face_EmojiIds[i]);
            addFace(emojiUseBeans, face_emoji, face_EmojiIds[i], face_EmojiNames[i]);
        }
        for (int i = 0; i < animo_emojiIds.length; i++) {
            map_FaceEmoji.put(animo_emojiNames[i], animo_emojiIds[i]);
            addFace(pigFaceBeans, face_animo, animo_emojiIds[i], animo_emojiNames[i]);
        }
        for (int i = 0; i < animo_mamonIds.length; i++) {
            map_FaceEmoji.put(animo_mamonNames[i], animo_mamonIds[i]);
            addFace(mamonFaceBeans, face_animo, animo_mamonIds[i], animo_mamonNames[i]);
        }
        for (int i = 0; i < animo_pandaIds.length; i++) {
            map_FaceEmoji.put(animo_pandaNames[i], animo_pandaIds[i]);
            addFace(pandaFaceBeans, face_animo, animo_pandaIds[i], animo_pandaNames[i]);
        }
//        getFaceData(false);
    }

    /**
     * 添加表情
     *
     * @param faceBeans
     * @param group
     * @param resId
     * @param name
     */
    private static void addFace(ArrayList<FaceBean> faceBeans, String group, int resId, String name) {
        FaceBean bean = new FaceBean();
        bean.setGroup(group);
        bean.setResId(resId);
        bean.setName(name);
        faceBeans.add(bean);
    }

    /**
     * 初始化表情列表
     *
     * @version 1.0
     * @createTime 2013-11-23,下午2:45:16
     * @updateTime 2013-11-23,下午2:45:16
     * @createAuthor CodeApe
     * @updateAuthor CodeApe
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public void initFaceList(int type_emoji) {

        mRadioGroup.setVisibility(VISIBLE);
        // 计算表情页数
        pagerCount = emojiUseBeans.size() % pagerSize == 0 ? emojiUseBeans.size() / pagerSize : emojiUseBeans.size() / pagerSize + 1;

        // 设置圆点和分页列表
        mRadioGroup.removeAllViews();
        list_Views = new ArrayList<View>();
        for (int i = 0; i < pagerCount; i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setId(i);
            radioButton.setButtonDrawable(R.drawable.radio_dot_selector);
            radioButton.setEnabled(false);
            RadioGroup.LayoutParams radioParams = new RadioGroup.LayoutParams(20, 20);
            radioParams.leftMargin = 10;
            radioButton.setLayoutParams(radioParams);
            mRadioGroup.addView(radioButton);

            FaceViewPager view = new FaceViewPager(context, type_emoji);
            view.setOnItemClikListener(faceClickListener);
            view.setOnItemLongClickListener(faceLongClickListener);
            view.setOnDeleteListener(mOnDeleteListener);
            list_Views.add(view);
        }

//        for (int i = 0; i < 4; i++) {
//            if (i == 0) {
//                type_emoji = FaceView.FaceType.FACE_ANIMO_TAB;
//            }
////            else if (i == 1) {
////                type_emoji = FaceView.FaceType.FACE_CUSTOM_TAB;
////            }
//            else if (i == 1) {
//                type_emoji = FaceView.FaceType.FACE_PIG;
//            } else if (i == 2) {
//                type_emoji = FaceView.FaceType.FACE_MAMMON;
//            } else if (i == 3) {
//                type_emoji = FaceView.FaceType.FACE_PANDA;
//            }
//            FaceViewPager view = new FaceViewPager(context, type_emoji);
//            view.setOnItemClikListener(faceClickListener);
//            view.setOnItemLongClickListener(faceLongClickListener);
//            view.setOnDeleteListener(mOnDeleteListener);
//            list_Views.add(view);
//        }

        // 设置分页列表
        adapter = new ViewPagerAdapter(list_Views);
        mViewPager.setAdapter(adapter);
        // 预加载1页
        mViewPager.setOffscreenPageLimit(1);

        // 设置当前显示列表
        if (emojiUseBeans.size() > pagerSize) {
            list_CurrentBeans.clear();
            list_CurrentBeans.addAll(emojiUseBeans.subList(0, pagerSize));
        } else {
            list_CurrentBeans.clear();
            list_CurrentBeans.addAll(emojiUseBeans.subList(0, emojiUseBeans.size()));
        }
        mRadioGroup.check(0);
        mCurViewPager = ((FaceViewPager) list_Views.get(0));
        mCurViewPager.setFaceList(list_CurrentBeans);
    }

    /**
     * 是否允许显示
     *
     * @param enabled true允许，false不显示
     * @version 1.0
     * @createTime 2014年1月7日, 下午2:49:24
     * @updateTime 2014年1月7日, 下午2:49:24
     * @createAuthor CodeApe
     * @updateAuthor CodeApe
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public void setSelectEnable(boolean enabled) {
        if (enabled) {
            select_RadioGroup.setVisibility(View.VISIBLE);
        } else {
            select_RadioGroup.setVisibility(View.GONE);
        }
    }

    /**
     * 组件监听模块
     *
     * @version 1.0
     * @createTime 2013-11-23,下午2:57:53
     * @updateTime 2013-11-23,下午2:57:53
     * @createAuthor CodeApe
     * @updateAuthor CodeApe
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void widgetListener() {

        // 列表切换监听事件
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (mCheckPostion == position) {
                    return;
                }
                mCheckPostion = position;
                int currentItem = mViewPager.getCurrentItem();
                mCurViewPager = (FaceViewPager) list_Views.get(currentItem);
                if (position > 4) {
                    switch (position) {
                        case 5:
                            mRadioGroup.setVisibility(INVISIBLE);
                            select_RadioGroup.check(R.id.animo_emoji);
                            mCurViewPager.setFaceList(oftenUseBeans);
                            break;
//                        case 6:
//                            mRadioGroup.setVisibility(GONE);
//                            select_RadioGroup.check(R.id.custom_emoji);
//                            mCurViewPager.setFaceList(collectFaceBeans);
//                            break;
                        case 6:
                            mRadioGroup.setVisibility(GONE);
                            select_RadioGroup.check(R.id.cb_face_pig);
                            mCurViewPager.setFaceList(pigFaceBeans);
                            break;
                        case 7:
                            mRadioGroup.setVisibility(GONE);
                            select_RadioGroup.check(R.id.cb_face_mammon);
                            mCurViewPager.setFaceList(mamonFaceBeans);
                            break;
                        case 8:
                            mRadioGroup.setVisibility(GONE);
                            select_RadioGroup.check(R.id.cb_face_panda);
                            mCurViewPager.setFaceList(pandaFaceBeans);
                            break;
                    }
                } else {
                    mEmojiCheckPostion = position;
                    select_RadioGroup.check(R.id.face_emoji);
                    mRadioGroup.setVisibility(VISIBLE);
                    mRadioGroup.check(position);
                    list_CurrentBeans.clear();
                    if ((currentItem * pagerSize + pagerSize) >= emojiUseBeans.size() && emojiUseBeans.size() >= (currentItem * pagerSize)) {
                        list_CurrentBeans.addAll(emojiUseBeans.subList(currentItem * pagerSize, emojiUseBeans.size()));
                    } else {
                        if (emojiUseBeans.size() >= (currentItem * pagerSize + pagerSize)) {
                            list_CurrentBeans.clear();
                            list_CurrentBeans.addAll(emojiUseBeans.subList(currentItem * pagerSize, currentItem * pagerSize + pagerSize));
                        }
                    }
                    mCurViewPager.setFaceList(list_CurrentBeans);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
        // 表情类型单选
        select_RadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.face_emoji:
                        mViewPager.setCurrentItem(mEmojiCheckPostion);
                        break;
                    case R.id.animo_emoji:
                        mViewPager.setCurrentItem(5);
                        break;
                    case R.id.custom_emoji:
//                        mViewPager.setCurrentItem(6);
                        break;
                    case R.id.cb_face_pig:// 猪
                        mViewPager.setCurrentItem(6);
                        break;
                    case R.id.cb_face_mammon:// 财神
                        mViewPager.setCurrentItem(7);
                        break;
                    case R.id.cb_face_panda:// 熊猫
                        mViewPager.setCurrentItem(8);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 设置表情列表点击事件
     *
     * @param listener
     * @version 1.0
     * @createTime 2013-11-23,下午10:59:53
     * @updateTime 2013-11-23,下午10:59:53
     * @createAuthor CodeApe
     * @updateAuthor CodeApe
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public void setOnItemClickListener(FaceViewPager.FaceClickListener listener) {
        this.faceClickListener = listener;
        if (list_Views == null) {
            return;
        }
        for (int i = 0; i < list_Views.size(); i++) {
            FaceViewPager view = (FaceViewPager) list_Views.get(i);
            view.setOnItemClikListener(this.faceClickListener);
        }
    }

    /**
     * 添加到最近使用表情视图
     *
     * @param faceBean
     */
    public void addOftenUseFace(FaceBean faceBean) {
        if (oftenUseBeans != null && faceBean != null && face_emoji.equals(faceBean.getGroup())) {
            for (FaceBean bean : oftenUseBeans) {
                if (bean.getResId() == faceBean.getResId()) {
                    oftenUseBeans.remove(bean);
                    break;
                }
            }
            oftenUseBeans.add(0, faceBean);
            if (oftenUseBeans.size() >= OFTEN_USE_MAX) {
                tempBeans.clear();
                tempBeans.addAll(oftenUseBeans.subList(0, OFTEN_USE_MAX));
                oftenUseBeans.clear();
                oftenUseBeans.addAll(tempBeans);
            }
        }
    }

    /**
     * 保存经常使用表情
     */
    public void saveOftenUseFace() {
        SpUtil spUtil = SpUtil.getSpUtil();
        if (oftenUseBeans != null && oftenUseBeans.size() > 0) {
            String value = new Gson().toJson(oftenUseBeans);
            if (UserAction.getMyId().intValue() != -1) {
                spUtil.putSPValue(UserAction.getMyId().intValue() + "", value);
            } else {
                Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
                spUtil.putSPValue(uid + "", value);
            }
        }
    }

    /**
     * 获取经常使用表情列表
     */
    public void getOftenUseFace() {
        SpUtil spUtil = SpUtil.getSpUtil();
        String result = "";
        if (UserAction.getMyId().intValue() != -1) {
            result = spUtil.getSPValue(UserAction.getMyId().intValue() + "", "");
        } else {
            Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
            result = spUtil.getSPValue(uid + "", "");
        }

        oftenUseBeans = new Gson().fromJson(result, new TypeToken<List<FaceBean>>() {
        }.getType());
        if (oftenUseBeans == null) {
            oftenUseBeans = new ArrayList<>();
        }
    }

    /**
     * 获取收藏表情
     *
     * @param update 是否刷新收藏表情
     */
    public static void getFaceData(boolean update) {
        collectFaceBeans.clear();
        SpUtil spUtil = SpUtil.getSpUtil();
        String value = "";
        if (UserAction.getMyId().intValue() != -1) {
            value = spUtil.getSPValue(UserAction.getMyId().intValue() + Preferences.FACE_DATA, "");
        } else {
            Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
            value = spUtil.getSPValue(uid + Preferences.FACE_DATA, "");
        }
        addFace(collectFaceBeans, face_custom, R.mipmap.img_add_face, "add");
        if (!TextUtils.isEmpty(value)) {
            List<FaceBean> list = new Gson().fromJson(value, new TypeToken<List<FaceBean>>() {
            }.getType());
            if (list.size() > 0) {// 去掉第一个加号
                list.remove(0);
            }
            collectFaceBeans.addAll(list);
        }
        if (update) {
            mCurViewPager.setFaceList(collectFaceBeans);
        }
    }

    /**
     * 设置表情列表长按事件
     *
     * @param listener
     * @version 1.0
     * @createTime 2013-12-30,上午10:59:55
     * @updateTime 2013-12-30,上午10:59:55
     * @createAuthor liujingguo
     * @updateAuthor liujingguo
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public void setOnItemLongClickListener(FaceViewPager.FaceLongClickListener listener) {
        this.faceLongClickListener = listener;
        if (list_Views == null) {
            return;
        }
        for (int i = 0; i < list_Views.size(); i++) {
            FaceViewPager view = (FaceViewPager) list_Views.get(i);
            view.setOnItemLongClickListener(this.faceLongClickListener);
        }
    }

    /**
     * @version 1.0
     * @createTime 2013-12-30,下午4:44:29
     * @updateTime 2013-12-30,下午4:44:29
     * @createAuthor liujingguo
     * @updateAuthor liujingguo
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public void updateView() {
        initFaceList(FaceView.FaceType.FACE_CUSTOM_TAB);
    }

    /**
     * 设置删除按钮点击事件
     *
     * @param listener
     * @version 1.0
     * @createTime 2013-11-23,下午11:01:24
     * @updateTime 2013-11-23,下午11:01:24
     * @createAuthor CodeApe
     * @updateAuthor CodeApe
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public void setOnDeleteListener(OnClickListener listener) {
        mOnDeleteListener = listener;
        if (list_Views == null) {
            return;
        }
        for (int i = 0; i < list_Views.size(); i++) {
            FaceViewPager view = (FaceViewPager) list_Views.get(i);
            view.setOnDeleteListener(listener);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (list_Views == null) {
            select_RadioGroup.check(R.id.face_emoji);
        }
    }

}
