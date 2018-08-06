package com.king.app.coolg.phone.login;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.view.View;

import com.chenenyu.router.Router;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityLoginBinding;
import com.king.app.coolg.model.FingerPrintController;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.view.dialog.SimpleDialogs;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class LoginActivity extends MvvmActivity<ActivityLoginBinding, LoginViewModel> {

    private FingerPrintController fingerPrint;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);
        mModel.fingerprintObserver.observe(this, aBoolean -> checkFingerprint());
        mModel.loginObserver.observe(this, success -> {
            if (success) {
                superUser();
            }
        });
        mModel.extendObserver.observe(this, hasExtendPref -> {
            new SimpleDialogs().showWarningActionDialog(LoginActivity.this
                    , getResources().getString(R.string.login_extend_pref_exist)
                    , getResources().getString(R.string.yes)
                    , null
                    , (dialog, which) -> {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            mModel.loadExtendPref();
                        }
                        else {
                            if (SettingProperty.isEnableFingerPrint()) {
                                checkFingerprint();
                            }
                            else {
                                mBinding.groupLogin.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        });
    }

    private void checkFingerprint() {
        fingerPrint = new FingerPrintController(LoginActivity.this);
        if (fingerPrint.isSupported()) {
            if (fingerPrint.hasRegistered()) {
                startFingerPrintDialog();
            } else {
                showMessageLong("设备未注册指纹");
            }
            return;
        } else {
            showMessageLong("设备不支持指纹识别");
        }
    }

    @Override
    protected LoginViewModel createViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    protected void initData() {
        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isGranted -> {
                    if (isGranted) {
                        initCreate();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    finish();
                });
    }

    private void initCreate() {
        mModel.initCreate();
    }

    private void startFingerPrintDialog() {
        if (fingerPrint.hasRegistered()) {
            boolean withPW = false;
            fingerPrint.showIdentifyDialog(withPW, new FingerPrintController.SimpleIdentifyListener() {

                @Override
                public void onSuccess() {
                    superUser();
                }

                @Override
                public void onFail() {

                }

                @Override
                public void onCancel() {
                    finish();
                }
            });
        } else {
            showMessageLong(getString(R.string.login_finger_not_register));
        }
    }

    private void superUser() {
        new SimpleDialogs().showWarningActionDialog(this
                , getResources().getString(R.string.login_start_service_insert)
                , getResources().getString(R.string.yes)
                , getResources().getString(R.string.no)
                , getResources().getString(R.string.title_activity_settings)
                , (dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        goToManage();
                    }
                    else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        goToSetting();
                    }
                    else {
                        goToHome();
                    }
                });
//        Intent intent = new Intent(this, SeasonListActivity.class);
//        startActivity(intent);
//        finish();
    }

    private void goToManage() {
    }

    private void goToSetting() {
    }

    private void goToHome() {
        Router.build("Home").go(this);
    }
}
