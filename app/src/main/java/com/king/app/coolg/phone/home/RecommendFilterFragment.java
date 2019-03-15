package com.king.app.coolg.phone.home;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentRecommendFilterBinding;
import com.king.app.coolg.model.FilterHelper;
import com.king.app.coolg.model.bean.RecordFilterModel;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.DraggableContentFragment;
import com.king.lib.banner.BannerFlipStyleProvider;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/8 10:52
 */
@Deprecated
public class RecommendFilterFragment extends DraggableContentFragment<FragmentRecommendFilterBinding> {

    private OnRecordFilterListener onRecordFilterListener;
    private RecommendFilterAdapter adapter;
    private RecordFilterModel filterModel;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_recommend_filter;
    }

    @Override
    protected void initView() {
        mBinding.rvItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (ScreenUtils.isTablet()) {
            mBinding.groupViewpager.setVisibility(View.GONE);
        }

        filterModel = new FilterHelper().getFilters();

        mBinding.cbNr.setOnCheckedChangeListener((buttonView, isChecked) -> filterModel.setSupportNR(isChecked));
        mBinding.rbFixed.setOnClickListener(v -> showAnimationSelector());
        mBinding.rbFixed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SettingProperty.setRandomRecommend(false);
            }
        });
        mBinding.rbRandom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SettingProperty.setRandomRecommend(true);
            }
        });
        if (SettingProperty.isRandomRecommend()) {
            mBinding.rbRandom.setChecked(true);
            mBinding.rbFixed.setText("Fixed");
        } else {
            mBinding.rbFixed.setChecked(true);
            try {
                mBinding.rbFixed.setText(formatFixedText(BannerFlipStyleProvider.ANIM_TYPES[SettingProperty.getRecommendAnimType()]));
            } catch (Exception e) {
                e.printStackTrace();
                mBinding.rbFixed.setText(formatFixedText(BannerFlipStyleProvider.ANIM_TYPES[0]));
            }
        }

        mBinding.etTime.setText(String.valueOf(SettingProperty.getRecommendAnimTime()));

        mBinding.cbNr.setChecked(filterModel.isSupportNR());

        refreshFilter();

        mBinding.tvDefault.setOnClickListener(view -> {
            filterModel = new FilterHelper().getFilters();
            refreshFilter();
        });

        mBinding.tvOk.setOnClickListener(view -> onSave());
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
        SettingProperty.setRecommendAnimTime(time);
        if (onRecordFilterListener != null) {
            onRecordFilterListener.onSaveFilterModel(filterModel);
        }
        dismissAllowingStateLoss();
    }

    public void setOnRecordFilterListener(OnRecordFilterListener onRecordFilterListener) {
        this.onRecordFilterListener = onRecordFilterListener;
    }

    public interface OnRecordFilterListener {
        void onSaveFilterModel(RecordFilterModel model);
    }
    private void showAnimationSelector() {
        new AlertDialogFragment()
                .setTitle(null)
                .setItems(BannerFlipStyleProvider.ANIM_TYPES, (dialog, which) -> {
                    mBinding.rbFixed.setText(formatFixedText(BannerFlipStyleProvider.ANIM_TYPES[which]));
                    SettingProperty.setRecommendAnimType(which);
                }).show(getChildFragmentManager(), "AlertDialogFragment");
    }

    private String formatFixedText(String type) {
        return "Fixed (" + type + ")";
    }

    private void refreshFilter() {
        if (adapter == null) {
            adapter = new RecommendFilterAdapter();
            adapter.setList(filterModel.getList());
            mBinding.rvItems.setAdapter(adapter);
        }
        else {
            adapter.setList(filterModel.getList());
            adapter.notifyDataSetChanged();
        }
    }

}
