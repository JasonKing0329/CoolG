package com.king.app.coolg.view.dialog.content;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentBannerSettingBinding;
import com.king.app.coolg.model.bean.BannerParams;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.DraggableContentFragment;
import com.king.lib.banner.BannerFlipStyleProvider;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/8 15:34
 */
public class BannerSettingFragment extends DraggableContentFragment<FragmentBannerSettingBinding> {

    private OnAnimSettingListener onAnimSettingListener;

    private boolean isHideAnimType;

    private BannerParams params;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_banner_setting;
    }

    @Override
    protected void initView() {
        if (params == null) {
            params = new BannerParams();
        }

        mBinding.rbFixed.setOnClickListener(v -> showAnimationSelector());
        mBinding.rbRandom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            params.setRandom(isChecked);
            if (isChecked) {
                onAnimSettingListener.onParamsUpdated(params);
            }
        });
        mBinding.rbFixed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            params.setRandom(false);
            if (isChecked) {
                onAnimSettingListener.onParamsUpdated(params);
            }
        });

        if (params.isRandom()) {
            mBinding.rbRandom.setChecked(true);
            mBinding.rbFixed.setText("Fixed");
        }
        else {
            mBinding.rbFixed.setChecked(true);
            try {
                mBinding.rbFixed.setText(formatFixedText(BannerFlipStyleProvider.ANIM_TYPES[params.getType()]));
            } catch (Exception e) {
                e.printStackTrace();
                mBinding.rbFixed.setText(formatFixedText(BannerFlipStyleProvider.ANIM_TYPES[0]));
            }
        }

        mBinding.etTime.setText(String.valueOf(params.getDuration()));

        mBinding.tvOk.setOnClickListener(v -> onSave());

        if (isHideAnimType) {
            mBinding.groupAnim.setVisibility(View.GONE);
            mBinding.tvAnimTitle.setVisibility(View.GONE);
        }
    }

    public void setParams(BannerParams params) {
        this.params = params;
    }

    public void setHideAnimType(boolean hideAnimType) {
        isHideAnimType = hideAnimType;
    }

    private String formatFixedText(String type) {
        return "Fixed (" + type + ")";
    }

    private void showAnimationSelector() {
        new AlertDialogFragment()
                .setTitle(null)
                .setItems(BannerFlipStyleProvider.ANIM_TYPES, (dialog, which) -> {
                    mBinding.rbFixed.setText(formatFixedText(BannerFlipStyleProvider.ANIM_TYPES[which]));
                    params.setType(which);
                    onAnimSettingListener.onParamsUpdated(params);
                }).show(getChildFragmentManager(), "AlertDialogFragment");
    }

    public void setOnAnimSettingListener(OnAnimSettingListener onAnimSettingListener) {
        this.onAnimSettingListener = onAnimSettingListener;
    }

    public void onSave() {
        int time;
        try {
            time = Integer.parseInt(mBinding.etTime.getText().toString());
        } catch (Exception e) {
            time = 0;
        }
        // 至少2S
        if (time < 2000) {
            time = 2000;
        }
        params.setDuration(time);
        onAnimSettingListener.onParamsSaved(params);

        dismissAllowingStateLoss();
    }

    public interface OnAnimSettingListener {
        void onParamsUpdated(BannerParams params);
        void onParamsSaved(BannerParams params);
    }
}
