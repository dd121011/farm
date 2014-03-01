package com.f5.ourfarm.widget;

import com.f5.ourfarm.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Tip {

	private Button image;
	private Dialog mDialog;

	public Tip(Context context) {
		mDialog = new Dialog(context, R.layout.dialog);
		Window window = mDialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = -30;
		wl.y = 20;
		window.setAttributes(wl);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		// window.setGravity(Gravity.CENTER);
		window.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		mDialog.setContentView(R.layout.tip);
		mDialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		image = (Button) mDialog.findViewById(R.id.image);
		image.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();
			}
		});
	}
	
	public Tip(Context context,int type) {
		mDialog = new Dialog(context, R.layout.dialog);
		Window window = mDialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = -30;
		wl.y = 20;
		window.setAttributes(wl);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		// window.setGravity(Gravity.CENTER);
		window.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		//1为联系我们
		if(type == 1){
			LayoutInflater flater = LayoutInflater.from(context);
			View v = flater.inflate(R.layout.tip, null);
			TextView ds = (TextView) v.findViewById(R.id.description);
			ds.setText(R.string.more_contact_us_content);
			v.invalidate();
			mDialog.setContentView(v);
		}
		mDialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		image = (Button) mDialog.findViewById(R.id.image);
		image.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();
			}
		});
	}

	public void show() {
		mDialog.show();
	}

}
