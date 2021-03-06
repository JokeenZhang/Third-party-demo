package com.tencent.sample.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UnionInfo;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.AuthAgent;
import com.tencent.connect.common.Constants;
import com.tencent.open.apireq.BaseResp;
import com.tencent.open.apireq.IApiCallback;
import com.tencent.open.im.IM;
import com.tencent.open.log.SLog;
import com.tencent.open.miniapp.MiniApp;
import com.tencent.sample.AppConstants;
import com.tencent.sample.PermissionMgr;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.tauth.DefaultUiListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.tencent.connect.common.Constants.KEY_ENABLE_SHOW_DOWNLOAD_URL;
import static com.tencent.connect.common.Constants.KEY_PROXY_APPID;
import static com.tencent.connect.common.Constants.KEY_QRCODE;
import static com.tencent.connect.common.Constants.KEY_RESTORE_LANDSCAPE;
import static com.tencent.connect.common.Constants.KEY_SCOPE;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();
    public static String mAppid = AppConstants.APP_ID;

	private static final String SHARE_PREF_NAME = "openSdk.pref";
	private static final String KEY_TARGET_QQ_UIN = "target.uin";
	private static final String KEY_TARGET_QQ_MINIAPP_ID = "target.miniappid";

	private static final String OPEN_CONNECT_DEMO_MINI_APP_ID = "1108108864";
	private static final String OPEN_CONNECT_DEMO_MINI_APP_PATH = "pages/tabBar/index/index";

	private Button mNewLoginButton;
    private Button mServerSideLoginBtn;
	private TextView mUserInfo;
	private ImageView mUserLogo;
    private EditText mEtTargetUin = null;
    private EditText mEtTargetMiniAppId = null;
    private EditText mEtTargetMiniAppPath = null;
    private EditText mEtTargetMiniAppVersion = null;
    public static Tencent mTencent;
    private static Intent mPrizeIntent = null;
    private static boolean isServerSideLogin = false;
    private CheckBox mCheckPermissionGranted;
    private CheckBox mQrCk;
    private CheckBox mCheckForceQr;
    private CheckBox mOEMLogin;
    private CheckBox mShowWebDownloadUi;
    private int mChosenIMType;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "-->onCreate");
		setContentView(R.layout.activity_main_new);
		initViews();

		mTencent = Tencent.createInstance(mAppid, MainActivity.this, AppConstants.APP_AUTHORITIES);
		if (mTencent == null) {
			SLog.e(TAG, "Tencent instance create fail!");
			finish();
		}

        // ?????????????????????intent??????
        if (null != getIntent()) {
            mPrizeIntent = getIntent();
        }
		// ??????????????????
		handlePrizeShare();

		Map<String, String> params = Tencent.parseMiniParameters(getIntent());
		if (!params.isEmpty()) {
			Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();

			StringBuffer sBuf = new StringBuffer();
			while(iter.hasNext()) {
				Map.Entry<String, String> entry= iter.next();
				sBuf.append(entry.getKey() + "=" + entry.getValue()).append(" ");
			}

			Toast.makeText(this, sBuf.toString(), Toast.LENGTH_LONG).show();
		}

		PermissionMgr.getInstance().requestPermissions(this);
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		PermissionMgr.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}

    /**
     * ?????????????????????????????????????????????????????????
     */
    private void handlePrizeShare() {
        
    }


    

	@Override
	protected void onStart() {
		Log.d(TAG, "-->onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "-->onResume");
        // ??????????????????
        handlePrizeShare();
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "-->onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "-->onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "-->onDestroy");
		super.onDestroy();
	}

	private void initViews() {
		mNewLoginButton = (Button) findViewById(R.id.new_login_btn);
		mServerSideLoginBtn = (Button) findViewById(R.id.server_side_login_btn);

		mCheckPermissionGranted = findViewById(R.id.check_permission_granted);
		mCheckPermissionGranted.setChecked(!Tencent.isPermissionNotGranted());
		mCheckPermissionGranted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Tencent.setIsPermissionGranted(isChecked);
			}
		});

		mQrCk = (CheckBox) findViewById(R.id.ck_qr);
        mCheckForceQr = (CheckBox) findViewById(R.id.check_force_qr);
        mCheckForceQr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.getId() == R.id.check_force_qr) {
                    mQrCk.setChecked(b);
                }
            }
        });
        mOEMLogin = (CheckBox)findViewById(R.id.check_oem_login);
		mShowWebDownloadUi = (CheckBox)findViewById(R.id.show_web_download_ui);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_container);
		OnClickListener listener = new NewClickListener();
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			View view = linearLayout.getChildAt(i);
			if (view instanceof Button) {
				view.setOnClickListener(listener);
			}
		}
		mNewLoginButton.setOnClickListener(listener);
		mQrCk.setOnClickListener(listener);
		mUserInfo = (TextView) findViewById(R.id.user_nickname);
		mUserLogo = (ImageView) findViewById(R.id.user_logo);
		updateLoginButton();
	}

	private void updateLoginButton() {
		if (mTencent != null && mTencent.isSessionValid()) {
            if (isServerSideLogin) {
                mNewLoginButton.setTextColor(Color.BLUE);
                mNewLoginButton.setText("??????");
                mServerSideLoginBtn.setTextColor(Color.RED);
                mServerSideLoginBtn.setText("??????Server-Side??????");
            } else {
                mNewLoginButton.setTextColor(Color.RED);
                mNewLoginButton.setText("????????????");
                mServerSideLoginBtn.setTextColor(Color.BLUE);
                mServerSideLoginBtn.setText("Server-Side??????");
            }
		} else {
			mNewLoginButton.setTextColor(Color.BLUE);
			mNewLoginButton.setText("??????");
            mServerSideLoginBtn.setTextColor(Color.BLUE);
            mServerSideLoginBtn.setText("Server-Side??????");
		}
	}

	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new DefaultUiListener() {

				@Override
				public void onError(UiError e) {

				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					mHandler.sendMessage(msg);
					new Thread(){

						@Override
						public void run() {
							JSONObject json = (JSONObject)response;
							if(json.has("figureurl")){
								Bitmap bitmap = null;
								try {
									bitmap = Util.getbitmap(json.getString("figureurl_qq_2"));
								} catch (JSONException e) {
									SLog.e(TAG, "Util.getBitmap() jsonException : " + e.getMessage());
								}
								Message msg = new Message();
								msg.obj = bitmap;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}

					}.start();
				}

				@Override
				public void onCancel() {

				}
			};
			UserInfo info = new UserInfo(this, mTencent.getQQToken());
			info.getUserInfo(listener);

		} else {
			mUserInfo.setText("");
			mUserInfo.setVisibility(android.view.View.GONE);
			mUserLogo.setVisibility(android.view.View.GONE);
		}
	}

	private void getUnionId() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new DefaultUiListener() {
				@Override
				public void onError(UiError e) {
					Toast.makeText(MainActivity.this,"onError",Toast.LENGTH_LONG).show();
				}

				@Override
				public void onComplete(final Object response) {
					if(response != null){
						JSONObject jsonObject = (JSONObject)response;
						try {
							String unionid = jsonObject.getString("unionid");
							Util.showResultDialog(MainActivity.this, "unionid:\n"+unionid, "onComplete");
							Util.dismissDialog();
						}catch (Exception e){
							Toast.makeText(MainActivity.this,"no unionid",Toast.LENGTH_LONG).show();
						}
					}else {
						Toast.makeText(MainActivity.this,"no unionid",Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onCancel() {
					Toast.makeText(MainActivity.this,"onCancel",Toast.LENGTH_LONG).show();
				}
			};
			UnionInfo unionInfo = new UnionInfo(this, mTencent.getQQToken());
			unionInfo.getUnionId(listener);
		} else {
			Toast.makeText(this,"please login frist!",Toast.LENGTH_LONG).show();
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						mUserInfo.setVisibility(android.view.View.VISIBLE);
						mUserInfo.setText(response.getString("nickname"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else if(msg.what == 1){
				Bitmap bitmap = (Bitmap)msg.obj;
				mUserLogo.setImageBitmap(bitmap);
				mUserLogo.setVisibility(android.view.View.VISIBLE);
			}
		}

	};

	private void onClickLogin() {
		if (!mTencent.isSessionValid()) {
			// ??????????????????
		    this.getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, mCheckForceQr.isChecked());

		    if (mOEMLogin.isChecked()) {
				mTencent.loginWithOEM(this, "all", loginListener, mQrCk.isChecked(), "10000144","10000144","xxxx");
			} else {
				HashMap<String, Object> params = new HashMap<String, Object>();
				if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
					params.put(KEY_RESTORE_LANDSCAPE, true);
				}

				

				params.put(KEY_SCOPE, "all");
				params.put(KEY_QRCODE, mQrCk.isChecked());
				params.put(KEY_ENABLE_SHOW_DOWNLOAD_URL, mShowWebDownloadUi.isChecked());
				mTencent.login(this, loginListener, params);
			}
            isServerSideLogin = false;
			Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
		} else {
            if (isServerSideLogin) { // Server-Side ???????????????, ?????????????????????SSO??????
                mTencent.logout(this);
                mTencent.login(this, "all", loginListener);
                isServerSideLogin = false;
                Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
		    mTencent.logout(this);
            // ???????????????????????????????????????????????????????????????targetUin/targetMiniAppId
            saveTargetUin("");
            saveTargetMiniAppId("");
			updateUserInfo();
			updateLoginButton();
		}
	}

	/**
	 * 1.?????????????????????????????????/??????/???????????????token?????????
	 * 2.??????????????????????????????????????????
	 * 3.?????????????????????????????????????????????
	 */
	private void onClickIm() {
		// ????????????????????????mTencent.isSessionValid()?????????????????????
		// ?????????????????????????????????????????????mTencent.checkLogin()
		if (mTencent.isSessionValid()) {
			// ??????AIO
			// ??????????????????,?????????token????????????????????????????????????????????????/??????????????????????????????????????????????????????(????????????QQ??????????????????????????????)
            // jumpIMWithType(mChosenIMType);
			buildUinDialog();
		} else {
			// ????????????????????????????????????????????????
			Toast.makeText(this, R.string.please_click_login_btn, Toast.LENGTH_LONG).show();
		}
	}

	private void buildUinDialog() {
		mEtTargetUin = new EditText(MainActivity.this);
		AlertDialog.Builder targetUinBuilder = new AlertDialog.Builder(MainActivity.this).setTitle("?????????Target???QQ???")
				.setCancelable(false)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(mEtTargetUin)
				.setPositiveButton("Commit", mTargetUinCommitListener);
		targetUinBuilder.show();
	}

    /**
     * ???????????????????????????IM??????
     *
     * @param type
     */
	private void jumpIMWithType(int type) {
	    int ret = IM.IM_UNKNOWN_TYPE;
	    if (type == Constants.IM_AIO) {
            ret = mTencent.startIMAio(this, getTargetUin(), getPackageName());
        } else if (type == Constants.IM_AUDIO_CHAT) {
	        ret = mTencent.startIMAudio(this, getTargetUin(), getPackageName());
        } else if (type == Constants.IM_VIDEO_CHAT) {
            ret = mTencent.startIMVideo(this, getTargetUin(), getPackageName());
        }
	    if (ret != IM.IM_SUCCESS) {
            Toast.makeText(getApplicationContext(),
                    "start IM conversation failed. error:" + ret,
                    Toast.LENGTH_LONG).show();
        }
    }

	/**
	 * ???????????????????????????
	 */
	private void onClickMiniApp() {
		// ???????????????/?????????
		buildMiniAppIdDialog();
	}

	private void buildMiniAppIdDialog() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View miniAppContentView = layoutInflater.inflate(R.layout.dialog_qqminiapp, null);
		mEtTargetMiniAppId = (EditText) miniAppContentView.findViewById(R.id.mini_app_id_edit);
		mEtTargetMiniAppPath = (EditText) miniAppContentView.findViewById(R.id.mini_app_path_edit);
		mEtTargetMiniAppVersion = (EditText) miniAppContentView.findViewById(R.id.mini_app_version_edit);
		// MiniAppId 1108108864 ?????????AppId 222222??????
		mEtTargetMiniAppId.setText(OPEN_CONNECT_DEMO_MINI_APP_ID);
		mEtTargetMiniAppPath.setText(OPEN_CONNECT_DEMO_MINI_APP_PATH);
		mEtTargetMiniAppVersion.setText(MiniApp.MINIAPP_VERSION_RELEASE);
		AlertDialog.Builder targetMiniAppIdBuilder = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.qqconnect_enter_tartget_mini_app_id_tip)
				.setCancelable(false)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(miniAppContentView)
				.setPositiveButton("Commit", mTargetMiniAppIdCommitListener);
		targetMiniAppIdBuilder.show();
	}

	/**
	 * ???????????????/?????????
	 * appid?????????????????????????????????????????????apptype???????????????????????????
	 * ????????????????????????MiniApp?????????
	 */
	private void launchMiniApp() {
		int ret = mTencent.startMiniApp(this, getTargetMiniAppId(), mEtTargetMiniAppPath.getText().toString(),
				mEtTargetMiniAppVersion.getText().toString());
		if (ret != MiniApp.MINIAPP_SUCCESS) {
			// ??????demo????????????????????????????????????????????????
			String errorStr = "";
			if (ret == MiniApp.MINIAPP_ID_EMPTY) {
				errorStr = getString(R.string.qqconnect_mini_app_id_empty);
			} else if (ret == MiniApp.MINIAPP_ID_NOT_DIGIT) {
				errorStr = getString(R.string.qqconnect_mini_app_id_not_digit);
			}
			StringBuilder builder = new StringBuilder();
			builder.append("start miniapp failed. error:")
					.append(ret)
					.append(" ")
					.append(errorStr);
			Toast.makeText(getApplicationContext(),
					builder.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

    private void onClickServerSideLogin() {
        if (!mTencent.isSessionValid()) {
            mTencent.loginServerSide(this, "all", loginListener);
            isServerSideLogin = true;
            Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
        } else {
            if (!isServerSideLogin) { // SSO???????????????????????????????????????Server-Side????????????
                mTencent.logout(this);
				updateUserInfo();
				updateLoginButton();
                mTencent.loginServerSide(this, "all", loginListener);
                isServerSideLogin = true;
                Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
            mTencent.logout(this);
            isServerSideLogin = false;
            updateUserInfo();
            updateLoginButton();
        }
    }

	private void gotoAuthPage() {
		mTencent.startAuthManagePage(MainActivity.this, new IApiCallback() {
			@Override
			public void onResp(BaseResp baseResp) {
				if (baseResp.isSuccess()) {
					// ????????????
					return;
				}
				String showMsg;
				switch (baseResp.getCode()) {
					case BaseResp.CODE_QQ_NOT_INSTALLED:
						showMsg = getString(R.string.qq_not_install);
						break;
					case BaseResp.CODE_UNSUPPORTED_BRANCH:
						showMsg = getString(R.string.qq_branch_not_support);
						break;
					case BaseResp.CODE_QQ_LOW_VERSION:
						showMsg = getString(R.string.upgrade_qq);
						break;
					case BaseResp.CODE_NOT_LOGIN:
						showMsg = getString(R.string.need_login);
						break;
					default:
						showMsg = baseResp.toString();
						break;
				}
				Toast.makeText(MainActivity.this, showMsg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static boolean ready(Context context) {
		if (mTencent == null) {
			return false;
		}
		boolean ready = mTencent.isSessionValid() && mTencent.getQQToken().getOpenId() != null;
		if (!ready) {
            Toast.makeText(context, "login and get openId first, please!", Toast.LENGTH_SHORT).show();
        }
		return ready;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    Log.d(TAG, "-->onActivityResult " + requestCode  + " resultCode=" + resultCode);
		switch (requestCode) {
			case Constants.REQUEST_LOGIN:
			case Constants.REQUEST_APPBAR: {
				Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
			}
				break;
			case Constants.REQUEST_COMMON_CHANNEL:{
				Tencent.onActivityResultData(requestCode, resultCode, data, commonChannelApiListener);
			}
			break;
			default:
				break;
		}


	    super.onActivityResult(requestCode, resultCode, data);
	}

	public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }

	IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
        	Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
            updateUserInfo();
            updateLoginButton();
        }
    };

	IUiListener aioLoginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
			// sdk???QQToken??????????????????,???????????????????????????
			initOpenidAndToken(values);
			updateUserInfo();
			// ?????????????????????????????????????????????????????????????????????????????????
			updateLoginButton();
			// ??????target???QQ??????????????????
			// ????????????????????????????????????????????????
			buildUinDialog();
		}
	};

	IUiListener commonChannelApiListener = new BaseUiListener();

	private class BaseUiListener extends DefaultUiListener {

		@Override
		public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(MainActivity.this, "????????????", "????????????");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (jsonResponse.length() == 0) {
                Util.showResultDialog(MainActivity.this, "????????????", "????????????");
                return;
            }
			Util.showResultDialog(MainActivity.this, response.toString(), "????????????");
            // ??????????????????
            handlePrizeShare();
			doComplete((JSONObject)response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			Util.toastMessage(MainActivity.this, "onError: " + e.errorDetail);
			Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			Util.toastMessage(MainActivity.this, "onCancel: ");
			Util.dismissDialog();
            if (isServerSideLogin) {
                isServerSideLogin = false;
            }
		}
	}

	private DialogInterface.OnClickListener mTargetUinCommitListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// ????????????targetUin
					String editTextContent = mEtTargetUin.getText().toString().trim();
					if (!TextUtils.isEmpty(editTextContent)) {
						saveTargetUin(editTextContent);
					} else {
						Toast.makeText(MainActivity.this, "targetUin????????????????????????", Toast.LENGTH_LONG).show();
						return;
					}
					break;
			}
			// ????????????
            jumpIMWithType(mChosenIMType);
		}
	};

	private DialogInterface.OnClickListener mTargetMiniAppIdCommitListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// ????????????targetMiniAppId
					String editTextId = mEtTargetMiniAppId.getText().toString().trim();
					if (!TextUtils.isEmpty(editTextId)) {
						saveTargetMiniAppId(editTextId);
					} else {
						Toast.makeText(MainActivity.this, getString(R.string.qqconnect_mini_app_id_empty), Toast.LENGTH_LONG).show();
						return;
					}
					// ??????????????????
					String editTextVersion = mEtTargetMiniAppVersion.getText().toString();
					if (!MiniApp.OPEN_CONNECT_DEMO_MINI_APP_VERSIONS.contains(editTextVersion)) {
						Toast.makeText(MainActivity.this, getString(R.string.qqconnect_mini_app_version_wrong), Toast.LENGTH_LONG).show();
						return;
					}
					break;
			}
			launchMiniApp();
		}
	};

	/**
	 * ???????????????????????????????????????????????????
	 * @param targetUin
	 */
	private void saveTargetUin(String targetUin) {
		if (targetUin != null) {
			SharedPreferences share = this.getSharedPreferences(SHARE_PREF_NAME, 0);
			SharedPreferences.Editor editor = share.edit();
			editor.putString(KEY_TARGET_QQ_UIN, targetUin);
			editor.commit();
		}
	}

	private String getTargetUin() {
		SharedPreferences share = this.getSharedPreferences(SHARE_PREF_NAME, 0);
		return share.getString(KEY_TARGET_QQ_UIN, null);
	}

	/**
	 * ??????????????????????????????????????????demo???????????????
	 * @param miniAppId
	 */
	private void saveTargetMiniAppId(String miniAppId) {
		if (miniAppId != null) {
			SharedPreferences share = this.getSharedPreferences(SHARE_PREF_NAME, 0);
			SharedPreferences.Editor editor = share.edit();
			editor.putString(KEY_TARGET_QQ_MINIAPP_ID, miniAppId);
			editor.commit();
		}
	}

	private String getTargetMiniAppId() {
		SharedPreferences share = this.getSharedPreferences(SHARE_PREF_NAME, 0);
		return share.getString(KEY_TARGET_QQ_MINIAPP_ID, null);
	}

	

	class NewClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Context context = v.getContext();
			Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
			Class<?> cls = null;
			boolean isAppbar = false;
			switch (v.getId()) {
				case R.id.new_login_btn:
				onClickLogin();
				v.startAnimation(shake);
				return;
			case R.id.main_aio_btn:
				mChosenIMType = Constants.IM_AIO;
				onClickIm();
				v.startAnimation(shake);
				return;
			case R.id.main_audio_chat_btn:
				mChosenIMType = Constants.IM_AUDIO_CHAT;
				onClickIm();
				v.startAnimation(shake);
				return;
			case R.id.main_video_chat_btn:
				mChosenIMType = Constants.IM_VIDEO_CHAT;
				onClickIm();
				v.startAnimation(shake);
				return;
			case R.id.main_mini_app_btn:
				onClickMiniApp();
				v.startAnimation(shake);
				return;
			case R.id.ck_qr:
				if(mQrCk.isChecked()){
					Toast.makeText(MainActivity.this,"????????????q??????????????????????????????????????????????????????",Toast.LENGTH_LONG).show();
				}
				return;
			case R.id.server_side_login_btn:
			    onClickServerSideLogin();
			    v.startAnimation(shake);
			    return;
			case R.id.main_sso_btn:
				{
					if (mTencent.isSupportSSOLogin(MainActivity.this)) {
						Toast.makeText(MainActivity.this, "??????SSO??????", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(MainActivity.this, "?????????SSO??????", Toast.LENGTH_SHORT).show();
					}
				}
			    return;
			case R.id.main_getInfo_btn:
				cls = AccountInfoActivity.class;
				break;
			case R.id.app_get_unionid:
				getUnionId();
				break;
			case R.id.main_qqShare_btn:
				cls = QQShareActivity.class;
				break;
			case R.id.main_qzoneShare_btn:
				cls = QZoneShareActivity.class;
				break;
			case R.id.main_is_qq_installed_btn:
				Toast.makeText(MainActivity.this, mTencent.isQQInstalled(MainActivity.this) + "" , Toast.LENGTH_SHORT).show();
				break;
			case R.id.check_token_valid:
				if (TextUtils.isEmpty(mTencent.getAccessToken())) {
					mTencent.initSessionCache(mTencent.loadSession(mAppid));
				}
				if (MainActivity.ready(MainActivity.this)) {
					mTencent.checkLogin(new DefaultUiListener() {
						@Override
						public void onComplete(Object response) {
							JSONObject jsonResp = (JSONObject)response;
							if (jsonResp.optInt("ret", -1) == 0) {
								JSONObject jsonObject = mTencent.loadSession(mAppid);
								mTencent.initSessionCache(jsonObject);
								if (jsonObject == null) {
									Util.showResultDialog(MainActivity.this, "jsonObject is null", "????????????");
								} else {
									Util.showResultDialog(MainActivity.this, jsonObject.toString(), "????????????");
								}
								updateUserInfo();
								updateLoginButton();
							} else {
								Util.showResultDialog(MainActivity.this, "token???????????????????????????????????????Q????????????", "????????????");
							}
						}

						@Override
						public void onError(UiError e) {
							Util.showResultDialog(MainActivity.this, "token???????????????????????????????????????Q????????????", "????????????");
						}

						@Override
						public void onCancel() {
							Util.toastMessage(MainActivity.this, "onCancel");
						}
					});
				}
				break;

			case R.id.main_avatar_btn:
				cls = AvatarActivity.class;
				break;
			case R.id.main_emotion_btn:
				cls = EmotionActivity.class;
				break;
			case R.id.main_others_btn:
				cls = OtherApiActivity.class;
				break;
			case R.id.open_auth_page_btn:
				gotoAuthPage();
				break;
			case R.id.main_qqgroup_btn:
				cls = QQGroupActivity.class;
				break;
				
			}
			v.startAnimation(shake);
			if (cls != null) {
				Intent intent = new Intent(context, cls);
				if (isAppbar) { //APP???????????????????????????????????????
					startActivityForResult(intent, Constants.REQUEST_APPBAR);
				} else {
					context.startActivity(intent);
				}
			}
		}
	}
}
