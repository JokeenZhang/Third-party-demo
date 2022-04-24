package com.tencent.sample.activitys;

import com.tencent.sample.AppConstants;
import com.tencent.sample.BindGroupParamsDialog;
import com.tencent.sample.R;
import com.tencent.sample.ThreadManager;
import com.tencent.tauth.DefaultUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class QQGroupActivity extends Activity implements OnClickListener {
    private AlertDialog mQQGroupDialog;
    private EditText mKeyEdit;
    private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqgroup);
        findViewById(R.id.qq_group_btn).setOnClickListener(this);
        findViewById(R.id.bind_group_btn).setOnClickListener(this);
        findViewById(R.id.unbind_group_btn).setOnClickListener(this);

        mTencent = Tencent.createInstance(MainActivity.mAppid, this, AppConstants.APP_AUTHORITIES);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qq_group_btn:
                showQQGroupDialog();
                break;
            case R.id.bind_group_btn:
                onClickBindGameGroup();
                break;
            case R.id.unbind_group_btn:
                showUnbindGroupDialog();
                break;
            default:
                break;
        }
    }

    private void onClickBindGameGroup() {
        new BindGroupParamsDialog(this, new BindGroupParamsDialog.OnGetParamsCompleteListener() {
            @Override
            public void onGetParamsComplete(String organizationId, String organizationName) {
                mTencent.bindQQGroup(QQGroupActivity.this, organizationId, organizationName, new DefaultUiListener() {
                    @Override
                    public void onComplete(Object response) {

                    }

                    @Override
                    public void onError(UiError e) {
                        final String errMsg = null == e ? "操作失败" : e.errorCode + ":" + e.errorMessage;
                        if(isMainThread()) {
                            Toast.makeText(QQGroupActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                        } else {
                            ThreadManager.getMainHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(QQGroupActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        }).show();
    }


    private void showUnbindGroupDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_qqgroup, null);
        mKeyEdit = (EditText) textEntryView.findViewById(R.id.key_edit);
        mKeyEdit.setText("1");
        mQQGroupDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.qq_group_dialog_title)
                .setView(textEntryView)
                .setPositiveButton(R.string.app_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String key = mKeyEdit.getText().toString();
                        if (TextUtils.isEmpty(key)) {
                            Toast.makeText(QQGroupActivity.this, "组织ID不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            mTencent.unBindQQGroup(QQGroupActivity.this, key, new DefaultUiListener() {
                                @Override
                                public void onComplete(final Object response) {
                                    if(isMainThread()) {
                                        Toast.makeText(QQGroupActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        ThreadManager.getMainHandler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(QQGroupActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(UiError e) {
                                    final String errMsg = null == e ? "操作失败" : e.errorCode + ":" + e.errorMessage;
                                    if(isMainThread()) {
                                        Toast.makeText(QQGroupActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                    } else {
                                        ThreadManager.getMainHandler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(QQGroupActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }
                    }
                })
                .setNegativeButton(R.string.app_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mQQGroupDialog.dismiss();
                    }
                })
                .create();
        mQQGroupDialog.show();
    }


    private void showQQGroupDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_qqgroup, null);
        mKeyEdit = (EditText) textEntryView.findViewById(R.id.key_edit);
        mKeyEdit.setText("1");
        mQQGroupDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.qq_group_dialog_title)
            .setView(textEntryView)
            .setPositiveButton(R.string.app_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    String key = mKeyEdit.getText().toString();
                    if (TextUtils.isEmpty(key)) {
                        Toast.makeText(QQGroupActivity.this, "组织ID不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        mTencent.joinQQGroup(QQGroupActivity.this, key, new DefaultUiListener() {
                            @Override
                            public void onComplete(Object response) {

                            }

                            @Override
                            public void onError(UiError e) {
                                final String errMsg = null == e ? "操作失败" : e.errorCode + ":" + e.errorMessage;
                                if(isMainThread()) {
                                    Toast.makeText(QQGroupActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                } else {
                                    ThreadManager.getMainHandler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(QQGroupActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                }
            })
            .setNegativeButton(R.string.app_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    mQQGroupDialog.dismiss();
                }
            })
            .create();
        mQQGroupDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isMainThread() {
        return Looper.myLooper() == getMainLooper();
    }
}
