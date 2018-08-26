package com.king.app.coolg.phone.login;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.king.app.coolg.BuildConfig;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.conf.AppConfig;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.DBExporter;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.MD5Util;
import com.king.lib.resmanager.JPrefManager;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/2 14:05
 */
public class LoginViewModel extends BaseViewModel {

    public ObservableField<String> etPwdText;

    public ObservableInt groupLoginVisibility;

    public MutableLiveData<Boolean> fingerprintObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> loginObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> extendObserver = new MutableLiveData<>();

    private String mPwd;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        etPwdText = new ObservableField<>();
        groupLoginVisibility = new ObservableInt(View.INVISIBLE);
    }

    public void onClickLogin(View view) {
        checkPassword(mPwd);
    }

    public void initCreate() {
        // 每次进入导出一次数据库
        DBExporter.execute();
        prepare();
    }

    public void prepare() {
        loadingObserver.setValue(true);
        prepareData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean hasExtendPref) {
                        loadingObserver.setValue(false);

                        if (!BuildConfig.checkUser) {
                            loginObserver.setValue(true);
                            return;
                        }

                        if (hasExtendPref) {
                            extendObserver.setValue(true);
                        }
                        else {
                            if (SettingProperty.isEnableFingerPrint()) {
                                fingerprintObserver.setValue(true);
                            }
                            else {
                                groupLoginVisibility.set(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Boolean> prepareData() {
        return Observable.create(e -> {

            DBExporter.execute();

            // 创建base目录
            for (String path: AppConfig.DIRS) {
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
            }

            // init server url
//                BaseUrl.getInstance().setBaseUrl(SettingProperty.getServerBaseUrl());

            CoolApplication.getInstance().createGreenDao();

            // 检查扩展配置
            boolean hasPref = checkExtendConf();

            e.onNext(hasPref);
        });
    }

    public TextWatcher getPwdTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPwd = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public void checkPassword(String pwd) {
        if ("38D08341D686315F".equals(MD5Util.get16MD5Capital(pwd))) {
            loginObserver.setValue(true);
        }
        else {
            loginObserver.setValue(false);
            messageObserver.setValue("密码错误");
        }
    }

    /**
     * 检查配置目录是否存在默认配置文件
     * @return
     */
    private boolean checkExtendConf() {
        File[] files = new File(AppConfig.APP_DIR_CONF_PREF_DEF).listFiles();

        if (files.length > 0) {
            for (File file:files) {
                if (file.getName().startsWith(AppConfig.PREF_NAME)) {
                    try {
                        String[] arr = file.getName().split("__");
                        String version = arr[1].split("\\.")[0];

                        String curVersion = SettingProperty.getPrefVersion();
                        DebugLog.e("checkExtendConf version:" + version + " curVersion:" + curVersion);
                        if (!version.equals(curVersion)) {
                            AppConfig.DISK_PREF_DEFAULT_PATH = file.getPath();
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        return false;
    }

    public void loadExtendPref() {
        replacePref()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (SettingProperty.isEnableFingerPrint()) {
                            fingerprintObserver.setValue(true);
                        }
                        else {
                            groupLoginVisibility.set(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Boolean> replacePref() {
        return Observable.create(observer -> {
            // 采用解析外部xml文件，以key value的方式重新put进preference中的方法
            new JPrefManager().loadExtendPreference(AppConfig.DISK_PREF_DEFAULT_PATH, PreferenceManager.getDefaultSharedPreferences(CoolApplication.getInstance()));
            observer.onNext(true);
        });
    }

}
