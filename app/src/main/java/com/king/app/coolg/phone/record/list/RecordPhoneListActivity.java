package com.king.app.coolg.phone.record.list;

import android.content.Intent;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityRecordListPhoneBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.scene.SceneActivity;
import com.king.app.coolg.phone.record.scene.SceneFragment;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.phone.video.home.RecommendFragment;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;

/**
 * Created by Administrator on 2018/8/11 0011.
 */
@Route("RecordListPhone")
public class RecordPhoneListActivity extends MvvmActivity<ActivityRecordListPhoneBinding, BaseViewModel> {

    public static final int REQUEST_SCENE = 501;

    private RecommendBean mFilter;

    private RecordListTabFragment ftRecords;

    private SceneFragment ftScene;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_list_phone;
    }

    @Override
    protected void initView() {
        initActionbar();

        mBinding.tvSceneAll.setOnClickListener(v -> selectScene());

        ftRecords = new RecordListTabFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ft_page, ftRecords, "RecordListTabFragment")
                .commit();

        ftScene = SceneFragment.newInstance(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ft_scene, ftScene, "SceneFragment")
                .commit();
        ftScene.setOnSceneSelectedListener(scene -> ftRecords.onSceneChanged(scene));
    }

    private void initActionbar() {
        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnSearchListener(words -> ftRecords.onKeywordChanged(words));
        mBinding.actionbar.registerPopupMenu(R.id.menu_sort);
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_sort:
                    changeSortType();
                    break;
                case R.id.menu_filter:
                    changeFilter();
                    break;
                case R.id.menu_scene:
                    if (mBinding.ftScene.getVisibility() == View.VISIBLE) {
                        mBinding.ftScene.setVisibility(View.GONE);
                    }
                    else {
                        mBinding.ftScene.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.menu_offset:
                    ftRecords.showSetOffset();
                    break;
                case R.id.menu_count:
                    showCount();
                    break;
            }
        });
    }

    private void showCount() {
        RecordCountDialog content = new RecordCountDialog();
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Statistics");
        dialogFragment.show(getSupportFragmentManager(), "RecordCountDialog");
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void initData() {
    }

    public void changeSortType() {
        SortDialogContent content = new SortDialogContent();
        content.setDesc(SettingProperty.isRecordSortDesc());
        content.setSortType(SettingProperty.getRecordSortType());
        content.setOnSortListener((desc, sortMode) -> {
            SettingProperty.setRecordSortType(sortMode);
            SettingProperty.setRecordSortDesc(desc);
            ftRecords.onSortChanged();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Sort");
        dialogFragment.show(getSupportFragmentManager(), "SortDialogContent");
    }

    public void changeFilter() {
        RecommendFragment content = new RecommendFragment();
        content.setBean(mFilter);
        content.setFixedType(ftRecords.getCurrentItem());
        content.setOnRecommendListener(bean -> {
            mFilter = bean;
            ftRecords.onFilterChanged(mFilter);
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Recommend Setting");
        dialogFragment.setContentFragment(content);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 2 / 3);
        dialogFragment.show(getSupportFragmentManager(), "RecommendFragment");
    }

    private void selectScene() {
        Router.build("ScenePhone")
                .with(SceneActivity.EXTRA_SCENE, ftScene.getFocusScene())
                .requestCode(REQUEST_SCENE)
                .go(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCENE) {
            if (resultCode == RESULT_OK) {
                String scene = data.getStringExtra(SceneActivity.RESP_SCENE);
                boolean isSortChanged = data.getBooleanExtra(SceneActivity.RESP_SORT_CHANGED, false);
                if (isSortChanged) {
                    ftScene.onSortChanged(scene);
                }
                else {
                    ftScene.focusToScene(scene);
                }
                ftRecords.onSceneChanged(scene);
            }
        }
    }
}
