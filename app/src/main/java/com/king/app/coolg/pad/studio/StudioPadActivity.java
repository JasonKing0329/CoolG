package com.king.app.coolg.pad.studio;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityRecordStudioPadBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.pad.record.list.RecordListPadFragment;
import com.king.app.coolg.phone.record.list.SortDialogContent;
import com.king.app.coolg.phone.studio.StudioHolder;
import com.king.app.coolg.phone.studio.StudioListFragment;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.phone.video.home.RecommendFragment;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.jactionbar.JActionbar;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/25 13:36
 */
@Route("StudioPad")
public class StudioPadActivity extends MvvmActivity<ActivityRecordStudioPadBinding, BaseViewModel> implements StudioHolder {

    public static final String EXTRA_SELECT_MODE = "select_mode";

    private StudioListFragment ftList;

    private RecordListPadFragment ftRecord;

    private RecommendBean mFilter;

    private String mCurrentOrderName;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_studio_pad;
    }

    @Override
    protected void initView() {
        boolean isSelectMode = getIntent().getBooleanExtra(EXTRA_SELECT_MODE, false);
        if (isSelectMode) {
            mBinding.flFtRecord.setVisibility(View.GONE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mBinding.flFtStudio.getLayoutParams();
            params.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
            mBinding.flFtStudio.setLayoutParams(params);

            mBinding.actionbarRecord.setVisibility(View.GONE);
            params = (ConstraintLayout.LayoutParams) mBinding.actionbar.getLayoutParams();
            params.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
            mBinding.actionbar.setLayoutParams(params);
        }
        else {
            mBinding.actionbarRecord.setOnMenuItemListener(menuId -> {
                switch (menuId) {
                    case R.id.menu_sort:
                        changeSortType();
                        break;
                    case R.id.menu_filter:
                        changeFilter();
                        break;
                }
            });
        }

        ftList = StudioListFragment.newInstance(isSelectMode);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_ft_studio, ftList, "StudioListFragment")
                .commit();
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @Override
    public JActionbar getJActionBar() {
        return mBinding.actionbar;
    }

    @Override
    public void showStudioPage(long studioId, String name) {
        ftRecord = RecordListPadFragment.newOrderInstance(studioId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_ft_record, ftRecord, "RecordListPadFragment")
                .commit();
        mCurrentOrderName = name;
        mFilter = null;
        mBinding.actionbarRecord.setTitle(name);
    }

    public void changeSortType() {
        SortDialogContent content = new SortDialogContent();
        content.setDesc(SettingProperty.isRecordSortDesc());
        content.setSortType(SettingProperty.getRecordSortType());
        content.setOnSortListener((desc, sortMode) -> {
            SettingProperty.setStarRecordsSortType(sortMode);
            SettingProperty.setStarRecordsSortDesc(desc);
            ftRecord.onStarRecordsSortChanged();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Sort");
        dialogFragment.show(getSupportFragmentManager(), "SortDialogContent");
    }

    public void changeFilter() {
        RecommendFragment content = new RecommendFragment();
        content.setBean(mFilter);
        content.setOnRecommendListener(bean -> {
            mFilter = bean;
            ftRecord.onFilterChanged(mFilter);
            updateFilter();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Recommend Setting");
        dialogFragment.setContentFragment(content);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 2 / 3);
        dialogFragment.show(getSupportFragmentManager(), "RecommendFragment");
    }

    public void updateFilter() {
        mBinding.actionbarRecord.setTitle(mCurrentOrderName);
    }

    @Override
    public void backToList() {

    }

    @Override
    public void sendSelectedOrderResult(Long orderId) {
        Intent intent = new Intent();
        intent.putExtra(AppConstants.RESP_ORDER_ID, orderId);
        setResult(RESULT_OK, intent);
        finish();
    }
}
