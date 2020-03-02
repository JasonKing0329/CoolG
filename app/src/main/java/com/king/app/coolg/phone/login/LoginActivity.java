package com.king.app.coolg.phone.login;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import com.chenenyu.router.Router;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityLoginBinding;
import com.king.app.coolg.model.fingerprint.samsung.SamsungFingerPrint;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.AppUtil;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.SimpleDialogs;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.com.tcsl.screenhelper.ScreenUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class LoginActivity extends MvvmActivity<ActivityLoginBinding, LoginViewModel> {

    private SamsungFingerPrint fingerPrint;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {

        DebugLog.e(ScreenUtil.getScreenInfor(this));

        mBinding.setModel(mModel);
        mModel.fingerprintObserver.observe(this, aBoolean -> checkFingerprint());
        mModel.loginObserver.observe(this, success -> {
            if (success) {
                // start background service after user pass
                startService(new Intent(LoginActivity.this, BackgroundService.class));
                superUser();
            }
        });
        mModel.extendObserver.observe(this, hasExtendPref -> {
            new SimpleDialogs().showConfirmCancelDialog(LoginActivity.this
                    , getResources().getString(R.string.login_extend_pref_exist)
                    , (dialog, which) -> mModel.loadExtendPref()
                    , (dialog, which) -> {
                        if (SettingProperty.isEnableFingerPrint()) {
                            checkFingerprint();
                        }
                        else {
                            mBinding.groupLogin.setVisibility(View.VISIBLE);
                        }
                    });
        });
    }

    private void checkFingerprint() {
        FingerprintManagerCompat compat = FingerprintManagerCompat.from(this);
        if (compat.isHardwareDetected()) {
            if (compat.hasEnrolledFingerprints()) {
                startFingerPrintDialog();
            }
            else {
                showMessageLong("设备未注册指纹");
            }
        }
        else {
            // 三星Tab S(Android6.0)有指纹识别，但是系统方法判断为没有，继续用三星的sdk操作
            checkSamsungFingerprint();
        }
    }

    private void checkSamsungFingerprint() {
        fingerPrint = new SamsungFingerPrint(LoginActivity.this);
        if (fingerPrint.isSupported()) {
            if (fingerPrint.hasRegistered()) {
                startSamsungFingerPrintDialog();
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

        if (AppUtil.isAndroidP()) {
            closeAndroidPDialog();
        }

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

    /**
     * android 9.0开始，部分机型如小米，Android P 后谷歌限制了开发者调用非官方公开API 方法或接口，也就是说，
     * 用反射直接调用源码就会有这样的提示弹窗出现，非 SDK 接口指的是 Android 系统内部使用、
     * 并未提供在 SDK 中的接口，开发者可能通过 Java 反射、JNI 等技术来调用这些接口
     * 用此方法去掉该弹框
     */
    private void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCreate() {
        mModel.initCreate();
    }

    /**
     * 通用指纹识别对话框
     */
    private void startFingerPrintDialog() {
        FingerprintDialog dialog = new FingerprintDialog();
        dialog.setOnFingerPrintListener(() -> superUser());
        dialog.show(getSupportFragmentManager(), "FingerprintDialog");
    }

    /**
     * 三星指纹识别对话框
     */
    private void startSamsungFingerPrintDialog() {
        boolean withPW = false;
        fingerPrint.showIdentifyDialog(withPW, new SamsungFingerPrint.SimpleIdentifyListener() {

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
    }

    private void superUser() {
        mModel.onUserPass();
        mBinding.groupLogin.setVisibility(View.GONE);
        mBinding.groupPass.setVisibility(View.VISIBLE);
        mBinding.groupPass.startAnimation(appearNextStep());
        mBinding.tvHome.setOnClickListener(v -> {
            goToHome();
            finish();
        });
        mBinding.tvSetting.setOnClickListener(v -> goToSetting());
        mBinding.tvManage.setOnClickListener(v -> goToManage());
    }

    private void goToManage() {
        Router.build("Manage").go(this);
    }

    private void goToSetting() {
        Router.build("Setting").go(this);
    }

    private void goToHome() {
        if (ScreenUtils.isTablet()) {
            Router.build("HomePad").go(this);
            finish();
        }
        else {
            Router.build("Home").go(this);
            finish();
        }
    }

    private Animation appearNextStep() {
        AnimationSet set = new AnimationSet(true);
        set.setDuration(500);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        set.addAnimation(alpha);
        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, 0.5f, 0.5f);
        set.addAnimation(scale);
        return set;
    }

}
