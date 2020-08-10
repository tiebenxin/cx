package com.yanlong.im;

import android.os.SystemClock;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.gkzxhn.autoespresso.operate.TRecyclerView;
import com.gkzxhn.autoespresso.operate.TSystem;
import com.gkzxhn.autoespresso.operate.TView;
import com.yanlong.im.user.ui.SplashActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * @author Liszt
 * @date 2020/7/14
 * Description
 */
@RunWith(AndroidJUnit4.class)
public class SendMessageTest {
    private int sendCount = 20;

    //需要屏蔽的朋友 备注、昵称
    private List<String> filter() {
        List<String> filterFriends = new ArrayList<>();
        filterFriends.add("常信零钱小助手");
        filterFriends.add("常信小助手");
        filterFriends.add("常信文件传输助手");
        filterFriends.add("成语接龙机器人");
        return filterFriends;
    }

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void splashTest() {
        SystemClock.sleep(2000);
        ViewInteraction tabView = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.bottom_tab),
                                0),
                        1),
                        isDisplayed()));
        tabView.perform(click());
        SystemClock.sleep(1000);
        foreachFriends();
    }

    /**
     * 遍历好友-发送消息
     */
    private void foreachFriends() {
        //已保存的群聊列表id
        int recyclerViewId = R.id.listView;
        //遍历所有好友
        for (int position = 1; position < TRecyclerView.getChildCount(recyclerViewId); position++) {
            System.out.println("position=" + position);
            if (!filter().contains(TRecyclerView.get_item_view_text(recyclerViewId, position, R.id.txt_name))) {
                TRecyclerView.scroll_to_position(recyclerViewId, position);
                TRecyclerView.click_item(recyclerViewId, position);
                TView.click_id(R.id.btn_msg);
                SystemClock.sleep(500);
                sendText(sendCount);
                TSystem.press_back();
                SystemClock.sleep(500);
                TSystem.press_back();
            }
        }
    }

    private void sendText(int count) {
        String text = "关于汉字的起源，中国古代文献上有种种说法，如“结绳”、“八卦”、“图画”、“书契”等，古书上还普遍记载有黄帝史官仓颉造字的传说。现代学者认为，系统的文字工具不可能完全由一个人创造出来，仓颉如果确有其人，也应该是文字创作者之一，文字整理者或颁布者。\n" +
                "　　最早刻划符号距今8000多年 [2] \n" +
                "最近几十年，中国考古界先后发布了一系列较殷墟甲骨文更早、与汉字起源有关的出土资料。这些资料主要是指原始社会晚期及有史社会早期出现在陶器上面的刻画或彩绘符号，另外还包括少量的刻写在甲骨、玉器、石器等上面的符号。可以说，它们共同为解释汉字的起源提供了新的依据。\n" +
                "　　通过系统考察、对比遍布中国各地的19种考古学文化的100多个遗址里出土的陶片上的刻划符号，郑州大学博士生导师王蕴智认为，中国最早的刻划符号出现在河南舞阳贾湖遗址，距今已有8000多年的历史。";
        for (int position = 0; position < count; position++) {
            TView.input_text(R.id.edit_chat, "测试文字" + position + text);
            //点击发送
            TView.click_id(R.id.btn_send);
        }

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
