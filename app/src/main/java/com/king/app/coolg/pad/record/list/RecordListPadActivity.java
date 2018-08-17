package com.king.app.coolg.pad.record.list;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityRecordListPadBinding;
import com.king.app.coolg.model.bean.HsvColorBean;
import com.king.app.coolg.model.bean.RecordListFilterBean;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.list.FilterDialogContent;
import com.king.app.coolg.phone.record.list.SortDialogContent;
import com.king.app.coolg.phone.record.scene.HsvColorDialogContent;
import com.king.app.coolg.phone.record.scene.SceneFragment;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.param.DataConstants;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 16:03
 */
@Route("RecordListPad")
public class RecordListPadActivity extends MvvmActivity<ActivityRecordListPadBinding, BaseViewModel>
    implements View.OnClickListener, IRecordListHolder {

    public static final String KEY_SCENE_NAME = "key_scene_name";

    private SceneFragment ftScene;
    private RecordListPadFragment ftRecords;

    private String mTitle;

    private RecordListFilterBean mFilter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_list_pad;
    }

    @Override
    protected void initView() {
        String scene = getIntent().getStringExtra(KEY_SCENE_NAME);
        mBinding.groupSearch.setVisibility(View.INVISIBLE);

        ftScene = new SceneFragment();
        ftScene.setOnSceneSelectedListener(scene1 -> onSceneChanged(scene1));
        if (!TextUtils.isEmpty(scene)) {
            mBinding.tvScene.setText(scene);
            ftScene.setScene(scene);
        } else {
            mBinding.tvScene.setText(AppConstants.KEY_SCENE_ALL);
        }
        mTitle = mBinding.tvScene.getText().toString();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_scenes, ftScene, "RecordSceneFragment")
                .commit();

        ftRecords = RecordListPadFragment.newInstance(0, scene);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_records, ftRecords, "RecordListPadFragment")
                .commit();

        mBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                ftRecords.onKeywordChanged(text.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.rgTag.setOnCheckedChangeListener((radioGroup, id) -> {
            switch (id) {
                case R.id.rb_tag_all:
                    ftScene.setRecordType(0);
                    ftScene.reload();
                    ftRecords.onRecordTypeChanged(0);
                    break;
                case R.id.rb_tag_1v1:
                    ftScene.setRecordType(DataConstants.VALUE_RECORD_TYPE_1V1);
                    ftScene.reload();
                    ftRecords.onRecordTypeChanged(DataConstants.VALUE_RECORD_TYPE_1V1);
                    break;
                case R.id.rb_tag_3w:
                    ftScene.setRecordType(DataConstants.VALUE_RECORD_TYPE_3W);
                    ftScene.reload();
                    ftRecords.onRecordTypeChanged(DataConstants.VALUE_RECORD_TYPE_3W);
                    break;
                case R.id.rb_tag_multi:
                    ftScene.setRecordType(DataConstants.VALUE_RECORD_TYPE_MULTI);
                    ftScene.reload();
                    ftRecords.onRecordTypeChanged(DataConstants.VALUE_RECORD_TYPE_MULTI);
                    break;
                case R.id.rb_tag_together:
                    ftScene.setRecordType(DataConstants.VALUE_RECORD_TYPE_LONG);
                    ftScene.reload();
                    ftRecords.onRecordTypeChanged(DataConstants.VALUE_RECORD_TYPE_LONG);
                    break;
            }
            ftRecords.loadNewRecords();
        });

        initIcons();
    }

    private void onSceneChanged(String scene) {
        mTitle = scene;
        mBinding.tvScene.setText(scene);
        ftRecords.onSceneChanged(scene);
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String scene = intent.getStringExtra(KEY_SCENE_NAME);
        if (!TextUtils.isEmpty(scene)) {
            mBinding.tvScene.setText(scene);
            ftScene.focusToScene(scene);
            ftRecords.onSceneChanged(scene);
        }
    }

    private void initIcons() {
        mBinding.ivIconBack.setOnClickListener(this);
        mBinding.ivIconClose.setOnClickListener(this);
        mBinding.ivIconSearch.setOnClickListener(this);
        mBinding.ivIconSort.setOnClickListener(this);
        mBinding.ivIconSortScene.setOnClickListener(this);
        mBinding.ivIconColor.setOnClickListener(this);
        mBinding.ivIconFilter.setOnClickListener(this);
        mBinding.ivIconFavor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_back:
                finish();
                break;
            case R.id.iv_icon_close:
                closeSearch();
                break;
            case R.id.iv_icon_search:
                if (mBinding.groupSearch.getVisibility() != View.VISIBLE) {
                    showSearchLayout();
                }
                break;
            case R.id.iv_icon_sort:
                changeSortType();
                break;
            case R.id.iv_icon_sort_scene:
                showSortPopup(view);
                break;
            case R.id.iv_icon_color:
                selectColor();
                break;
            case R.id.iv_icon_filter:
                changeFilter();
                break;
            case R.id.iv_icon_favor:
                goToOrderPage();
                break;
        }
    }

    private void goToOrderPage() {
//        Router.bui
        finish();
    }

    /**
     * hide search layout
     */
    public void closeSearch() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.disappear);
        mBinding.groupSearch.startAnimation(animation);
        mBinding.groupSearch.setVisibility(View.INVISIBLE);
    }

    /**
     * show search layout
     */
    private void showSearchLayout() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.appear);
        mBinding.groupSearch.startAnimation(animation);
        mBinding.groupSearch.setVisibility(View.VISIBLE);
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

    public void changeSortType() {
        SortDialogContent content = new SortDialogContent();
        content.setDesc(SettingProperty.isRecordOrderModeDesc());
        content.setSortType(SettingProperty.getRecordOrderMode());
        content.setOnSortListener((desc, sortMode) -> {
            SettingProperty.setRecordOrderMode(sortMode);
            SettingProperty.setRecordOrderModeDesc(desc);
            ftRecords.onSortChanged();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Sort");
        dialogFragment.show(getSupportFragmentManager(), "SortDialogContent");
    }

    public void changeFilter() {
        FilterDialogContent content = new FilterDialogContent();
        content.setFilterBean(mFilter);
        content.setOnFilterListener(bean -> {
            mFilter = bean;
            ftRecords.onFilterChanged(mFilter);
            updateFilter(bean);
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Sort");
        dialogFragment.show(getSupportFragmentManager(), "SortDialogContent");
    }

    public void updateFilter(RecordListFilterBean bean) {
        if (bean != null) {
            StringBuffer buffer = new StringBuffer();
            if (bean.isBareback()) {
                buffer.append(", ").append("Bareback");
            }
            if (bean.isInnerCum()) {
                buffer.append(", ").append("Inner cum");
            }
            if (bean.isNotDeprecated()) {
                buffer.append(", ").append("Not deprecated");
            }
            String title = buffer.toString();
            if (title.length() > 2) {
                title = title.substring(2);
                mBinding.tvScene.setText(mTitle + " (" + title + ")");
            } else {
                mBinding.tvScene.setText(mTitle);
            }
        } else {
            mBinding.tvScene.setText(mTitle);
        }
    }
    public void showSortPopup(View anchor) {
        PopupMenu menu = new PopupMenu(this, anchor);
        menu.getMenuInflater().inflate(R.menu.sort_scene, menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(sortListener);
    }

    PopupMenu.OnMenuItemClickListener sortListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_sort_by_name:
                    ftScene.sortScene(AppConstants.SCENE_SORT_NAME);
                    break;
                case R.id.menu_sort_by_avg:
                    ftScene.sortScene(AppConstants.SCENE_SORT_AVG);
                    break;
                case R.id.menu_sort_by_number:
                    ftScene.sortScene(AppConstants.SCENE_SORT_NUMBER);
                    break;
                case R.id.menu_sort_by_max:
                    ftScene.sortScene(AppConstants.SCENE_SORT_MAX);
                    break;
            }
            return true;
        }
    };

    @Override
    public void showRecordPopup(View v, Record record) {

    }
}
