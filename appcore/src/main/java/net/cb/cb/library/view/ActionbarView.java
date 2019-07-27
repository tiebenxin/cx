package net.cb.cb.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cb.cb.library.R;
import net.cb.cb.library.utils.ClickFilter;
import net.cb.cb.library.utils.StringUtil;


/***
 * 替换actionbar 1.可通过配置设置标题或者setTitle设置标题 2.setListenEvent(ListenEvent)来添加监听事件
 * 
 * @author 姜永健
 * @date 2015年10月14日
 */
public class ActionbarView extends LinearLayout {

	private View rootView;
	private TextView txtTitle;
	private TextView txtTitleMore;
	private TextView txtLeft;
	private TextView txtRight;

	private ImageView btnBack;
	private ImageView btnRight;
	private View ViewLeft;
	private LinearLayout ViewRight;

	private Context context;
	private ListenEvent listenEvent;

	public void setOnListenEvent(ListenEvent listenEvent) {
		this.listenEvent = listenEvent;
	}

	/***
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		txtTitle.setText(title);
	}
	public void setTitleMore(String title) {
		if(StringUtil.isNotNull(title)){
			txtTitleMore.setText(title);
			txtTitleMore.setVisibility(VISIBLE);
		}else {
			txtTitleMore.setVisibility(GONE);
		}

	}
	public void setTitleMore(Spanned title) {
		if(StringUtil.isNotNull(title.toString())){
			txtTitleMore.setText(title);
			txtTitleMore.setVisibility(VISIBLE);
		}else {
			txtTitleMore.setVisibility(GONE);
		}

	}

	public String getTitle() {
		return txtTitle.getText().toString();
	}

	/***
	 * 设置左边文字
	 * 
	 * @param txt
	 */
	public void setTxtLeft(String txt) {
		txtLeft.setText(txt);
		txtLeft.setVisibility(View.VISIBLE);
	}

	/***
	 * 设置右边文字
	 * 
	 * @param txt
	 */
	public void setTxtRight(String txt) {
		txtRight.setText(txt);
		txtRight.setVisibility(View.VISIBLE);
	}

	public TextView getTxtRight() {
		return txtRight;
	}

	/**
	 * 获取中间TextView
	 * 
	 * @return
	 */
	public TextView getCenterTitle() {
		return txtTitle;
	}

	/***
	 * 获取左边图标按钮
	 * 
	 * @return
	 */
	public ImageView getBtnLeft() {
		return btnBack;
	}

	/***
	 * 获取右边图标按钮
	 * 
	 * @return
	 */
	public ImageView getBtnRight() {
		return btnRight;
	}

	/***
	 * 获取右侧所有控件
	 * 
	 * @return
	 */
	public LinearLayout getViewRight() {
		return ViewRight;
	}

	/***
	 * 右侧添加新控件
	 */
	public void addViewRight(View child) {
		ViewRight.addView(child, 0);
	}

	/**
	 * 根据给进来的颜色值设置文本颜色
	 * 
	 * @param color
	 */
	public void setTextColor(@ColorInt int color) {
		txtTitle.setTextColor(color);
		txtLeft.setTextColor(color);
		txtRight.setTextColor(color);
	}

	public void setTxtRightEnabled(boolean enabled){
		ViewRight.setEnabled(enabled);


		txtRight.setTextColor(enabled?Color.parseColor("#49C481"):Color.parseColor("#cccccc"));


	}

	public ActionbarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 rootView = inflater.inflate(R.layout.view_actionbar, this);
		txtTitle = rootView.findViewById(R.id.txt_title);
		txtTitleMore = rootView.findViewById(R.id.txt_title_more);
		btnBack = rootView.findViewById(R.id.btn_icon);
		btnRight = rootView.findViewById(R.id.btn_icon_right);
		ViewLeft = rootView.findViewById(R.id.action_left);
		ViewRight = rootView.findViewById(R.id.action_right);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ActionbarView);
		// 左图标
		if (typedArray.getBoolean(R.styleable.ActionbarView_actionbar_showIconLeft, true)) {
			btnBack.setVisibility(View.VISIBLE);
		} else {
			btnBack.setVisibility(View.GONE);
		}
		// 左图标
		int bid = typedArray.getResourceId(R.styleable.ActionbarView_actionbar_btnBackIcon, 0);
		if (bid != 0) {
			btnBack.setImageResource(bid);
		}

		// 右图标
		int rid = typedArray.getResourceId(R.styleable.ActionbarView_actionbar_rightIcon, 0);
		if (rid != 0) {
			btnRight.setVisibility(View.VISIBLE);
			btnRight.setImageResource(rid);
		} else {
			btnRight.setVisibility(View.GONE);
		}

		// 标题
		String title = typedArray.getString(R.styleable.ActionbarView_actionbar_txtTitle);
		txtTitle.setText(title);
		// 左文字
		txtLeft = rootView.findViewById(R.id.txt_back);
		String txtl = typedArray.getString(R.styleable.ActionbarView_actionbar_txtLeft);
		if (txtl == null || txtl.equals("")) {
			txtLeft.setVisibility(View.GONE);
		} else {
			txtLeft.setText(txtl);
			txtLeft.setVisibility(View.VISIBLE);
		}
		// 右文字
		txtRight = rootView.findViewById(R.id.txt_right);

		String txtr = typedArray.getString(R.styleable.ActionbarView_actionbar_txtRight);
		if (txtr == null || txtr.equals("")) {
			txtRight.setVisibility(View.GONE);
		} else {
			txtRight.setText(txtr);
			txtRight.setVisibility(View.VISIBLE);
		}

		ClickFilter.onClick(ViewRight, new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listenEvent != null
						&& (txtRight.getVisibility() == View.VISIBLE || btnRight.getVisibility() == View.VISIBLE))
					listenEvent.onRight();
			}
		});

		ViewLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (listenEvent != null)
					listenEvent.onBack();
			}
		});

		// int color = typedArray.getColor(
		// R.styleable.ActionbarView_backgroundColor, Integer.MAX_VALUE);
		// if (color != Integer.MAX_VALUE) {
		// rootView.findViewById(R.id.ll_main).setBackgroundColor(color);
		// }

		for (int i = 0, size = attrs.getAttributeCount(); i < size; i++) {
			String name = attrs.getAttributeName(i);
			String value = attrs.getAttributeValue(i);

			Log.i("ActionbarView", "#onCreate attr[" + i + "] name = " + name + " and value = " + value);
			if ("background".equals(name)) {
				if (value.startsWith("@")) {
					int bgResId = Integer.parseInt(value.substring(1));
					rootView.findViewById(R.id.ll_main).setBackgroundResource(bgResId);
				} else if (value.startsWith("#")) {
					String alphaStr = null;
					String colorStr = null;
					if (value.length() == 9) {
						alphaStr = value.substring(1, 3);
						colorStr = value.substring(3);
					} else if (value.length() <= 7) {
						colorStr = value.substring(1);
					}
					if (colorStr != null) {
						try {
							int color = Integer.parseInt(colorStr, 16);
							rootView.findViewById(R.id.ll_main).setBackgroundColor(color);
						} catch (NumberFormatException e) {

						}
					}

					if (alphaStr != null) {
						try {
							int alpha = Integer.parseInt(alphaStr, 16);
							rootView.findViewById(R.id.ll_main).setAlpha(alpha);
						} catch (NumberFormatException e) {

						}
					}

				}
			}

		}

		String value = attrs.getAttributeValue("android", "background");

		Log.i("ActionbarView", "#onCreate background = " + value);

	}

	/***
	 * 监听事件
	 * 
	 * @author 姜永健
	 * @date 2015年10月14日
	 */
	public interface ListenEvent {
		/***
		 * 左边按钮监听
		 */
		void onBack();

		/***
		 * 右边按钮监听
		 */
		void onRight();

	}


	public void setWhite(){
		//白色主题 1.16
			rootView.findViewById(R.id.ll_main).setBackgroundColor(Color.parseColor("#ffffff"));
			txtTitle.setTextColor(Color.parseColor("#000000"));
	}

}
