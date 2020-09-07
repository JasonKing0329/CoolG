package com.king.app.coolg.phone.star.random;

import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentDialogRandomSettingBinding;
import com.king.app.coolg.view.dialog.DraggableContentFragment;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/9/7 15:32
 */
public class RandomSettingFragment extends DraggableContentFragment<FragmentDialogRandomSettingBinding> {

    private RandomRule randomRule;

    private OnSettingListener onSettingListener;

    public void setOnSettingListener(OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
    }

    public void setRandomRule(RandomRule randomRule) {
        this.randomRule = randomRule;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_dialog_random_setting;
    }

    @Override
    protected void initView() {
        mBinding.cbRating.setOnCheckedChangeListener((buttonView, isChecked) -> mBinding.groupRating.setVisibility(isChecked ? View.VISIBLE:View.GONE));

        if (randomRule != null) {
            mBinding.cbExclude.setChecked(randomRule.isExcludeFromMarked());
            mBinding.rbAll.setChecked(randomRule.getStarType() == 0);
            mBinding.rbTop.setChecked(randomRule.getStarType() == 1);
            mBinding.rbBottom.setChecked(randomRule.getStarType() == 2);
            mBinding.rbHalf.setChecked(randomRule.getStarType() == 3);
            boolean isJoinRating = !TextUtils.isEmpty(randomRule.getSqlRating());
            mBinding.cbRating.setChecked(isJoinRating);
            mBinding.groupRating.setVisibility(isJoinRating ? View.VISIBLE:View.GONE);
            if (isJoinRating) {
                mBinding.etSqlRating.setText(randomRule.getSqlRating());
            }
        }

        mBinding.tvOk.setOnClickListener(v -> saveSetting());
    }

    private void saveSetting() {
        if (randomRule == null) {
            randomRule = new RandomRule();
        }
        randomRule.setExcludeFromMarked(mBinding.cbExclude.isChecked());
        if (mBinding.rbAll.isChecked()) {
            randomRule.setStarType(0);
        }
        else if (mBinding.rbTop.isChecked()) {
            randomRule.setStarType(1);
        }
        else if (mBinding.rbBottom.isChecked()) {
            randomRule.setStarType(2);
        }
        else if (mBinding.rbHalf.isChecked()) {
            randomRule.setStarType(3);
        }
        if (mBinding.cbRating.isChecked()) {
            randomRule.setSqlRating(mBinding.etSqlRating.getText().toString());
        }
        else {
            randomRule.setSqlRating(null);
        }
        onSettingListener.onSetRule(randomRule);
        dismissAllowingStateLoss();
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    public interface OnSettingListener {
        void onSetRule(RandomRule randomRule);
    }
}
