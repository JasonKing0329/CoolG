package com.king.app.coolg.phone.record.scene;

import android.content.Intent;
import android.view.View;
import android.widget.PopupMenu;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityScenePhoneBinding;
import com.king.app.coolg.model.bean.HsvColorBean;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/13 14:54
 */
@Route("ScenePhone")
public class SceneActivity extends MvvmActivity<ActivityScenePhoneBinding, BaseViewModel> {

    public static final String EXTRA_SCENE = "scene";
    public static final String RESP_SCENE = "resp_scene";

    private SceneFragment ftScene;

    @Override
    protected int getContentView() {
        return R.layout.activity_scene_phone;
    }

    @Override
    protected void initView() {

        mBinding.actionbar.setOnBackListener(() -> finish());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_color:
                    selectColor();
                    break;
            }
        });
        mBinding.actionbar.registerPopupMenu(R.id.menu_sort);
        mBinding.actionbar.setPopupMenuProvider((iconMenuId, anchorView) -> {
            switch (iconMenuId) {
                case R.id.menu_sort:
                    return getSortPopup(anchorView);
            }
            return null;
        });
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void initData() {

        ftScene = new SceneFragment();
        ftScene.setOnSceneSelectedListener(scene -> {
            Intent intent = new Intent();
            intent.putExtra(RESP_SCENE, scene);
            setResult(RESULT_OK, intent);
            finish();
        });
        ftScene.setScene(getIntent().getStringExtra(EXTRA_SCENE));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_ft, ftScene, "SceneFragment")
                .commit();
    }

    public PopupMenu getSortPopup(View anchorView) {
        PopupMenu menu = new PopupMenu(this, anchorView);
        menu.getMenuInflater().inflate(R.menu.sort_scene, menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_sort_by_name:
                    ftScene.sortScene(AppConstants.SCENE_SORT_NAME);
                    break;
                case R.id.menu_sort_by_avg:
                    ftScene.sortScene(AppConstants.SCENE_SORT_AVG);
                    break;
                case R.id.menu_sort_by_max:
                    ftScene.sortScene(AppConstants.SCENE_SORT_MAX);
                    break;
                case R.id.menu_sort_by_number:
                    ftScene.sortScene(AppConstants.SCENE_SORT_NUMBER);
                    break;
            }
            return true;
        });
        return menu;
    }

    private void selectColor() {
        HsvColorDialogContent content = new HsvColorDialogContent();
        content.setHsvColorBean(SettingProperty.getSceneHsvColor());
        content.setOnHsvColorListener(new HsvColorDialogContent.OnHsvColorListener() {
            @Override
            public void onPreviewHsvColor(HsvColorBean hsvColorBean) {
                ftScene.updateTempColor(hsvColorBean);
            }

            @Override
            public void onSaveColor(HsvColorBean hsvColorBean) {
                SettingProperty.setSceneHsvColor(hsvColorBean);
            }
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Color");
        dialogFragment.setContentFragment(content);
        dialogFragment.setOnDismissListener(dialog -> ftScene.onColorChanged());
        dialogFragment.show(getSupportFragmentManager(), "HsvColorDialogContent");
    }

}
