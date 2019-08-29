package net.cb.cb.library.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public abstract  class BaseDialog extends Dialog implements OnClickListener{

	
	public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);

	}

	public BaseDialog(Context context, int theme) {
		super(context, theme);

	}

	public BaseDialog(Context context) {
		super(context);

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initdata();
		initEvent();
	}

	public abstract void initView();
	public void initdata(){}
	public void initEvent(){}
	public abstract void processClick(View view);
	@Override
	public void onClick(View v) {
		processClick(v);
		
	}
	
}
