package com.king.app.coolg.phone.download;

import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentDownloadPreviewBinding;
import com.king.app.coolg.model.http.bean.data.DownloadItem;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.AlertDialogFragment;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 11:51
 */
public class PreviewFragment extends MvvmFragment<FragmentDownloadPreviewBinding, DownloadViewModel> {

    private IContentHolder holder;
    
    private PreviewAdapter adapter;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IContentHolder) holder;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_download_preview;
    }

    @Override
    protected DownloadViewModel createViewModel() {
        return null;
    }

    @Override
    protected void onCreate(View view) {
        int col = 2;
        if (ScreenUtils.isTablet()) {
            col = 3;
        }
        GridLayoutManager gridManager = new GridLayoutManager(getActivity(), col);
        mBinding.rvExisted.setLayoutManager(gridManager);

        mBinding.tvContinue.setOnClickListener(v -> showReadyDialog());

        if (!ListUtil.isEmpty(holder.getExistedList())) {
            mBinding.rvExisted.setVisibility(View.VISIBLE);
            adapter = new PreviewAdapter();
            adapter.setList(holder.getExistedList());
            mBinding.rvExisted.setAdapter(adapter);

            mBinding.cbSelectAll.setVisibility(View.VISIBLE);
            mBinding.cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    adapter.selectAll();
                } else {
                    adapter.unSelectAll();
                }
                adapter.notifyDataSetChanged();
            });

        } else {
            showReadyDialog();
            mBinding.cbSelectAll.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreateData() {
    }

    private void showReadyDialog() {

        int items = holder.getDownloadList().size();
        if (adapter != null) {
            items += adapter.getCheckedItems().size();
        }
        String message = String.format(getString(R.string.gdb_option_download), items);
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.setMessage(message);
        dialog.setPositiveText(getString(R.string.ok));
        dialog.setPositiveListener((dialog1, which) -> {
            if (adapter != null) {
                List<DownloadItem> list = adapter.getCheckedItems();
                if (list.size() > 0) {
                    holder.addDownloadItems(list);
                }
            }
            holder.showListPage();
        });
        dialog.setNegativeText(getString(R.string.cancel));
        dialog.setNegativeListener((dialog12, which) -> {
            // 重新选择
            if (holder.getExistedList() != null && holder.getExistedList().size() > 0) {
            }
            // 没有重复的item，直接关闭对话框
            else {
                holder.dismissDialog();
            }
        });
        dialog.show(getChildFragmentManager(), "AlertDialogFragmentV4");
    }

}
