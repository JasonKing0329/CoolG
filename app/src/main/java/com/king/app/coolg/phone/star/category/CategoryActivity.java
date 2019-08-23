package com.king.app.coolg.phone.star.category;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityCategoryBinding;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.jactionbar.OnConfirmListener;

@Route("Category")
public class CategoryActivity extends MvvmActivity<ActivityCategoryBinding, CategoryViewModel> {

    public static final int REQUEST_DETAIL = 401;

    private CategoryAdapter adapter;

    @Override
    protected CategoryViewModel createViewModel() {
        return ViewModelProviders.of(this).get(CategoryViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_category;
    }

    @Override
    protected void initView() {
        mBinding.rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_add:
                    updateCategory(null);
                    break;
                case R.id.menu_delete:
                    mBinding.actionbar.showConfirmStatus(R.id.menu_delete);
                    adapter.setSelectMode(true);
                    break;
                case R.id.menu_edit:
                    mBinding.actionbar.showConfirmStatus(R.id.menu_edit);
                    mModel.setEditMode(true);
                    break;
            }
        });
        mBinding.actionbar.setOnConfirmListener(actionId -> {
            switch (actionId) {
                case R.id.menu_delete:
                    showConfirmCancelMessage("Are you sure to delete?"
                            , (dialogInterface, i) -> mModel.deleteSelected(), null);
                    break;
                case R.id.menu_edit:
                    mBinding.actionbar.cancelConfirmStatus();
                    mModel.setEditMode(false);
                    break;
            }
            return false;
        });
        mBinding.actionbar.setOnCancelListener(actionId -> {
            switch (actionId) {
                case R.id.menu_delete:
                    adapter.setSelectMode(false);
                    break;
                case R.id.menu_edit:
                    mModel.setEditMode(false);
                    break;
            }
            return true;
        });
    }

    private void updateCategory(CategoryViewItem item) {
        AddCategoryFragment fragment = new AddCategoryFragment();
        fragment.setCategoryViewItem(item);
        fragment.setOnAddCategoryListener((name, type) -> {
            if (item == null) {
                mModel.addCategory(name, type);
            }
            else {
                mModel.updateCategory(item.getCategory(), name);
                adapter.notifyCategoryChanged(item);
            }
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(fragment);
        dialogFragment.setTitle("Add Category");
        dialogFragment.show(getSupportFragmentManager(), "AddCategoryFragment");
    }

    @Override
    protected void initData() {

        mModel.categoryObserver.observe(this, list -> {
            if (adapter == null) {
                adapter = new CategoryAdapter();
                adapter.setList(list);
                adapter.setOnItemClickListener((view, position, data) -> {
                    if (mModel.isEditMode()) {
                        updateCategory(data);
                    }
                    else {
                        goToDetail(data);
                    }
                });
                mBinding.rvList.setAdapter(adapter);
            }
            else {
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }
        });
        mModel.existedCategoryObserver.observe(this, category -> {
            showConfirmCancelMessage(category.getName() + " is already existed. Do you want to continue?"
                    , (dialogInterface, i) -> mModel.forceInsertCategory(category), null);
        });
        mModel.hideConfirmStatus.observe(this, hide -> mBinding.actionbar.cancelConfirmStatus());

        mModel.loadCategories();
    }

    private void goToDetail(CategoryViewItem data) {
        mModel.setDetailCategory(data);
        Router.build("CategoryDetail")
                .with(CategoryDetailActivity.EXTRA_CATEGORY_ID, data.getCategory().getId())
                .requestCode(REQUEST_DETAIL)
                .go(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DETAIL:
                if (resultCode == RESULT_OK) {
                    adapter.notifyCategoryChanged(mModel.refreshCategory());
                }
                break;
        }
    }
}
