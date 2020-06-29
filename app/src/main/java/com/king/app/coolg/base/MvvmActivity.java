package com.king.app.coolg.base;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/2 15:50
 */
public abstract class MvvmActivity<T extends ViewDataBinding, VM extends BaseViewModel> extends BaseActivity {

    protected T mBinding;

    protected VM mModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getContentView());
        mModel = createViewModel();
        if (mModel != null) {
            mModel.loadingObserver.observe(this, show -> {
                if (show) {
                    showProgress("loading...");
                }
                else {
                    dismissProgress();
                }
            });
            mModel.messageObserver.observe(this, message -> showMessageLong(message));
        }

        initView();
        initData();
    }

    /**
     * 仅LoginActivity不应用，单独覆写
     * @return
     */
    protected boolean updateStatusBarColor() {
        return true;
    }

    protected abstract VM createViewModel();

    protected VM generateViewModel(Class<VM> vm) {
        return ViewModelProviders.of(this).get(vm);
    }

    protected abstract void initData();

    @Override
    protected void onDestroy() {
        if (mModel != null) {
            mModel.onDestroy();
        }
        super.onDestroy();
    }
}
