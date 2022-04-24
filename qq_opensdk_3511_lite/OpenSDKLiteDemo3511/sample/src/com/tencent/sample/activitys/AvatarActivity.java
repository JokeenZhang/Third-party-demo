package com.tencent.sample.activitys;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.tencent.connect.common.Constants;
import com.tencent.sample.AppConstants;
import com.tencent.sample.BaseUIListener;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.tauth.DefaultUiListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;


public class AvatarActivity extends BaseActivity implements OnClickListener {
	private static final int REQUEST_SET_AVATAR = 2;
	private static final int REQUEST_SET_AVATAR2 = 3;
	private static final int REQUEST_SET_DAYNMIC_AVATAR = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBarTitle("用户头像");
		setLeftButtonEnable();
		setContentView(R.layout.avatar_activity);
		findViewById(R.id.set_avatar_btn).setOnClickListener(this);
		findViewById(R.id.set_avatar_btn2).setOnClickListener(this);
		findViewById(R.id.set_dynamic_btn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_avatar_btn:
			onClickSetAvatar();
			break;
		case R.id.set_avatar_btn2:
			onClickSetAvatar2();
			break;
		case R.id.set_dynamic_btn:
			onClickSetDynamicAvatar();
			break;
		default:
			break;
		}

	}

    private void onClickSetAvatar() {
        if (MainActivity.ready(AvatarActivity.this)) {
            Intent intent = new Intent();
            // 开启Pictures画面Type设定为image
            intent.setType("image/*");
            // 使用Intent.ACTION_GET_CONTENT这个Action
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // 取得相片后返回本画面
            startActivityForResult(intent, REQUEST_SET_AVATAR);
            // 在 onActivityResult 中调用 doSetAvatar
        }
    }

	private void onClickSetAvatar2() {
		Intent intent = new Intent();
		// 开启Pictures画面Type设定为image
		intent.setType("image/*");
		// 使用Intent.ACTION_GET_CONTENT这个Action
		intent.setAction(Intent.ACTION_GET_CONTENT);
		// 取得相片后返回本画面
		startActivityForResult(intent, REQUEST_SET_AVATAR2);
		// 在 onActivityResult 中调用 doSetAvatar
	}

	private void onClickSetDynamicAvatar() {
		Intent intent = new Intent();
		// 开启Pictures画面Type设定为image
		intent.setType("video/*");
		// 使用Intent.ACTION_GET_CONTENT这个Action
		intent.setAction(Intent.ACTION_GET_CONTENT);
		// 取得相片后返回本画面
		startActivityForResult(intent, REQUEST_SET_DAYNMIC_AVATAR);
		// 在 onActivityResult 中调用 doSetAvatar

	}

    private void doSetAvatar(Uri uri) {
		Bundle params = new Bundle();
		params.putString(Constants.PARAM_AVATAR_URI,uri.toString());
		params.putInt("exitAnim",R.anim.zoomout);
		MainActivity.mTencent.setAvatar(this, params, new BaseUIListener(this));
    }

	private void doSetAvatar2(Uri uri) {
		MainActivity.mTencent.setAvatarByQQ(this, uri, new BaseUIListener(this));
	}

	private void doSetDynamicAvatar(Uri uri) {
		MainActivity.mTencent.setDynamicAvatar(this, uri, new BaseUIListener(this));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SET_AVATAR && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				doSetAvatar(data.getData());
			} else {
				Util.toastMessage(AvatarActivity.this, "数据为空,请重新选择");
			}
		}else if (requestCode == REQUEST_SET_AVATAR2 && resultCode == Activity.RESULT_OK) {
			Uri uri = getUri(data);
			if (uri != null) {
				doSetAvatar2(uri);
			} else {
				Util.toastMessage(AvatarActivity.this, "数据为空,请重新选择");
			}
		} else if (requestCode == REQUEST_SET_DAYNMIC_AVATAR) {
			Uri uri = getUri(data);
			if (uri != null) {
				doSetDynamicAvatar(uri);
			} else {
				Util.toastMessage(AvatarActivity.this, "数据为空,请重新选择");
			}
		} else if(requestCode == Constants.REQUEST_EDIT_AVATAR){
			Tencent.onActivityResultData(requestCode, resultCode, data, setAvatarListener);
		} else if (requestCode == Constants.REQUEST_EDIT_DYNAMIC_AVATAR) {
			Tencent.onActivityResultData(requestCode, resultCode, data, setAvatarListener);
		}
	}
	
	private Uri getUri(Intent data) {
		try {
			if (data == null) {
				Util.toastMessage(AvatarActivity.this, "data 为空, 请重新选择");
				return null;
			}

			if (Build.VERSION.SDK_INT < 24 /*android.os.Build.VERSION_CODES.N*/) {
				return data.getData(); // android7.0以下不支持content://
			}
			
			// 先转存到沙盒目录下
			String path = Util.getPath(this, data.getData());
			if (null == path) {
				Util.toastMessage(AvatarActivity.this, "转存失败, 请重新选择");
				return null;
			}
			
			return FileProvider.getUriForFile(this, AppConstants.APP_AUTHORITIES, new File(path));
		} catch (Exception e) {
			Log.e("AvatarActivity", "-->getUri Exception", e);
			return null;
		}
	}

	IUiListener setAvatarListener = new DefaultUiListener() {
		@Override
		public void onCancel() {
			Util.toastMessage(AvatarActivity.this, "设置取消");
		}
		@Override
		public void onComplete(Object response) {
			Util.toastMessage(AvatarActivity.this, "设置成功：" + response.toString());
		}
		@Override
		public void onError(UiError e) {
			Util.toastMessage(AvatarActivity.this, "设置失败");
		}
	};
	
}
