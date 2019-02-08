package com.king.app.coolg.phone.star.category;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.base.adapter.BaseRecyclerAdapter;
import com.king.app.coolg.databinding.ActivityCategoryBinding;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.jactionbar.OnConfirmListener;

@Route("Category")
public class CategoryActivity extends MvvmActivity<ActivityCategoryBinding, CategoryViewModel> {

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
                    addCategory();
                    break;
                case R.id.menu_delete:
                    mBinding.actionbar.showConfirmStatus(R.id.menu_delete);
                    adapter.setSelectMode(true);
                    break;
            }
        });
        mBinding.actionbar.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public boolean disableInstantDismissConfirm() {
                return true;
            }

            @Override
            public boolean disableInstantDismissCancel() {
                return false;
            }

            @Override
            public boolean onConfirm(int actionId) {
                showConfirmCancelMessage("Are you sure to delete?"
                        , (dialogInterface, i) -> mModel.deleteSelected(), null);
                return false;
            }

            @Override
            public boolean onCancel(int actionId) {
                adapter.setSelectMode(false);
                return true;
            }
        });
    }

    private void addCategory() {
        AddCategoryFragment fragment = new AddCategoryFragment();
        fragment.setOnAddCategoryListener((name, type) -> mModel.addCategory(name, type));
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
                adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<CategoryViewItem>() {
                    @Override
                    public void onClickItem(View view, int position, CategoryViewItem data) {

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
}
