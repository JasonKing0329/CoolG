package com.king.app.coolg.phone.studio;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.PopupMenu;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentStudioListBinding;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.gdb.data.entity.FavorRecordOrder;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/18 11:21
 */
public class StudioListFragment extends MvvmFragment<FragmentStudioListBinding, StudioViewModel> {

    private static final String ARG_SELECT_MODE = "select_mode";

    private StudioHolder holder;

    private StudioSimpleAdapter simpleAdapter;
    private StudioRichAdapter richAdapter;

    public static StudioListFragment newInstance(boolean selectMode) {
        StudioListFragment fragment = new StudioListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_SELECT_MODE, selectMode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (StudioHolder) holder;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_studio_list;
    }

    @Override
    protected StudioViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StudioViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        if (isSelectMode() && ScreenUtils.isTablet()) {
            mBinding.rvList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        }
        else {
            mBinding.rvList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        }

        holder.getJActionBar().setOnBackListener(() -> getActivity().finish());

        initMenu();
    }

    private boolean isSelectMode() {
        return getArguments().getBoolean(ARG_SELECT_MODE);
    }

    private void initMenu() {
        holder.getJActionBar().setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_mode:
                    mModel.toggleListType();
                    break;
            }
        });
        holder.getJActionBar().registerPopupMenu(R.id.menu_sort);
        holder.getJActionBar().setPopupMenuProvider((iconMenuId, anchorView) -> {
            switch (iconMenuId) {
                case R.id.menu_sort:
                    return getSortPopup(anchorView);
            }
            return null;
        });
    }

    public void resetMenu() {
        holder.getJActionBar().setMenu(R.menu.studios);
        initMenu();
    }

    private PopupMenu getSortPopup(View anchorView) {
        PopupMenu menu = new PopupMenu(getActivity(), anchorView);
        menu.getMenuInflater().inflate(R.menu.studios_sort, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_sort_name:
                    mModel.onSortTypeChanged(PreferenceValue.STUDIO_LIST_SORT_NAME);
                    break;
                case R.id.menu_sort_items:
                    mModel.onSortTypeChanged(PreferenceValue.STUDIO_LIST_SORT_NUM);
                    break;
                case R.id.menu_sort_create_time:
                    mModel.onSortTypeChanged(PreferenceValue.STUDIO_LIST_SORT_CREATE_TIME);
                    break;
                case R.id.menu_sort_update_time:
                    mModel.onSortTypeChanged(PreferenceValue.STUDIO_LIST_SORT_UPDATE_TIME);
                    break;
            }
            return true;
        });
        return menu;
    }

    @Override
    protected void onCreateData() {
        mModel.listTypeMenuObserver.observe(this, text -> holder.getJActionBar().updateMenuText(R.id.menu_mode, text));
        mModel.simpleObserver.observe(this, list -> showSimpleList(list));
        mModel.richObserver.observe(this, list -> showRichList(list));

        mModel.loadStudios();
    }

    private void showSimpleList(List<StudioSimpleItem> list) {
        if (simpleAdapter == null) {
            simpleAdapter = new StudioSimpleAdapter();
            simpleAdapter.setList(list);
            simpleAdapter.setOnItemClickListener((view, position, data) -> onClickOrder(data.getOrder()));
            mBinding.rvList.setAdapter(simpleAdapter);
        }
        else {
            simpleAdapter.setList(list);
            mBinding.rvList.setAdapter(simpleAdapter);
        }
    }

    private void showRichList(List<StudioRichItem> list) {
        if (richAdapter == null) {
            richAdapter = new StudioRichAdapter();
            richAdapter.setList(list);
            richAdapter.setOnItemClickListener((view, position, data) -> onClickOrder(data.getOrder()));
            mBinding.rvList.setAdapter(richAdapter);
        }
        else {
            richAdapter.setList(list);
            mBinding.rvList.setAdapter(richAdapter);
        }
    }

    private void onClickOrder(FavorRecordOrder order) {
        if (isSelectMode()) {
            holder.sendSelectedOrderResult(order.getId());
        }
        else {
            holder.showStudioPage(order.getId(), order.getName());
        }
    }

}
