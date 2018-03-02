package com.browser2345.player.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.browser2345.player.R;
import com.browser2345.player.R2;

/**
 * @author th 2014-5-13 类说明：自定义对话框基类
 */
public class CustomDialog extends Dialog{

	public Context mContext;

	private View mRootView;

	@BindView(R2.id.masking_view)
	protected View mMarskView;

	@BindView(R2.id.dialog_bottom_divider)
	View mBottomDivder;

	@BindView(R2.id.dialog_bottom_btn_divider)
	View mBottomBtnDivider;

	@BindView(R2.id.news_play_video_msg)
	protected TextView mMessageView;
	/**
	 * 确定按钮
	 */
	@BindView(R2.id.btn_confirm)
	protected TextView mPositiveButton;

	/**
	 * 取消按钮
	 */
	@BindView(R2.id.btn_cancel)
	protected TextView mNegativeButton;

	public boolean isNight() {
		return mIsNight;
	}

	public void setIsNight(boolean mIsNight) {
		this.mIsNight = mIsNight;
	}

	protected boolean mIsNight;

	public CustomDialog(Context context) {
		super(context, R.style.dialog);
		mContext = context;
		setCanceledOnTouchOutside(false);
		mRootView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
		ButterKnife.bind(this, mRootView);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mRootView == null) {
			mRootView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
		}
		setContentView(mRootView);
	}


	/**
	 * 设置弹窗icon
	 * 
	 * @param
	 */
	public void setMessage(String message) {
		mMessageView.setText(message);
	}

	public void setMessage(int resId) {
		mMessageView.setText(resId);
	}


	public void setPositiveButtonText(String text) {
		mPositiveButton.setText(text);
	}

	public void setNegativeButtonText(String text) {
		mNegativeButton.setText(text);
	}

	public void setMessageTextColor(int color) {
		mMessageView.setTextColor(mContext.getResources().getColor(color));
	}



	/**
	 * 设置确定按钮点击事件
	 * 
	 * @param listener
	 */
	public void setPositiveButtonListener(View.OnClickListener listener) {
		mPositiveButton.setOnClickListener(listener);
	}

	/**
	 * 取消按钮点击事件
	 * 
	 * @param listener
	 */
	public void setNegativeButtonListener(View.OnClickListener listener) {
		mNegativeButton.setOnClickListener(listener);
	}

	public void setPositiveButtonGone() {
		mPositiveButton.setVisibility(View.GONE);
	}

	public void setNegativeButtonGone() {
		mNegativeButton.setVisibility(View.GONE);
	}

	public void nightMode(boolean isNight) {
		mIsNight = isNight;
		if (mMarskView != null && mIsNight) {
			mMarskView.setVisibility(View.VISIBLE);
			mMarskView.getBackground().setAlpha((int) (255 * 0.67));
		}

		Resources resources = mContext.getResources();
		if (mRootView != null && ((ViewGroup) mRootView).getChildAt(0) != null) {
			((ViewGroup) mRootView).getChildAt(0).setSelected(mIsNight);
		}

		if (mMessageView != null) {
			mMessageView.setTextColor(mIsNight ? resources.getColor(R.color.C011)
					: resources.getColor(R.color.C010));
		}

		if (mNegativeButton != null) {
			mNegativeButton.setTextColor(mIsNight ? resources.getColor(R.color.C021)
					: resources.getColor(R.color.C020));
			mNegativeButton.setSelected(mIsNight);
		}

		if (mBottomBtnDivider != null) {
			mBottomBtnDivider.setBackgroundColor(mIsNight ? resources.getColor(R.color.B051)
					: resources.getColor(R.color.B050));
		}

		if (mBottomDivder != null) {
			mBottomDivder.setBackgroundColor(mIsNight ? resources.getColor(R.color.B051)
					: resources.getColor(R.color.B050));
		}
	}

}
