package com.king.app.coolg.view.dialog.content;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentBannerSettingBinding;
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

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_banner_setting;
    }

    @Override
    protected void initView() {
        mBinding.rbFixed.setOnClickListener(v -> showAnimationSelector());
        mBinding.rbRandom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                onAnimSettingListener.onRandomAnim(true);
            }
        });
        mBinding.rbFixed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                onAnimSettingListener.onRandomAnim(false);
            }
        });

        boolean isRandom = onAnimSettingListener.isRandomAnim();
        if (isRandom) {
            mBinding.rbRandom.setChecked(true);
            mBinding.rbFixed.setText("Fixed");
        }
        else {
            mBinding.rbFixed.setChecked(true);
            try {
                mBinding.rbFixed.setText(formatFixedText(BannerFlipStyleProvider.ANIM_TYPES[onAnimSettingListener.getAnimType()]));
            } catch (Exception e) {
                e.printStackTrace();
                mBinding.rbFixed.setText(formatFixedText(BannerFlipStyleProvider.ANIM_TYPES[0]));
            }
        }

        mBinding.etTime.setText(String.valueOf(onAnimSettingListener.getAnimTime()));

        mBinding.tvOk.setOnClickListener(v -> onSave());
    }

    private String formatFixedText(String type) {
        return "Fixed (" + type + ")";
    }

    private void showAnimationSelector() {
        new AlertDialogFragment()
                .setTitle(null)
                .setItems(BannerFlipStyleProvider.ANIM_TYPES, (dialog, which) -> {
                    mBinding.rbFixed.setText(formatFixedText(BannerFlipStyleProvider.ANIM_TYPES[which]));
                    onAnimSettingListener.onSaveAnimType(which);
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
        onAnimSettingListener.onSaveAnimTime(time);
        onAnimSettingListener.onParamsSaved();

        dismissAllowingStateLoss();
    }

    public interface OnAnimSettingListener {
        boolean isRandomAnim();
        void onRandomAnim(boolean random);
        int getAnimType();
        void onSaveAnimType(int type);
        int getAnimTime();
        void onSaveAnimTime(int time);
        void onParamsSaved();
    }
}
