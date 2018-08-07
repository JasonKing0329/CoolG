package com.king.app.coolg.phone.download;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentDownloadListBinding;
import com.king.app.coolg.model.bean.DownloadItemProxy;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 13:26
 */
public class ListFragment extends MvvmFragment<FragmentDownloadListBinding, DownloadViewModel> {

    private IContentHolder holder;

    private ListAdapter adapter;

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_download_list;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IContentHolder) holder;
    }

    @Override
    protected DownloadViewModel createViewModel() {
        return null;
    }

    @Override
    protected void onCreate(View view) {
        mBinding.rvList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onCreateData() {
        holder.getViewModel().itemsObserver.observe(this, list -> showList(list));
        holder.getViewModel().progressObserver.observe(this, position -> adapter.notifyItemChanged(position));

        holder.getViewModel().initDownloadItems(holder.getDownloadList());
    }

    private void showList(List<DownloadItemProxy> list) {
        if (list.size() == 0) {
            mBinding.tvEmpty.setVisibility(View.VISIBLE);
            mBinding.rvList.setVisibility(View.GONE);
            return;
        }
        adapter = new ListAdapter();
        adapter.setList(list);
        mBinding.rvList.setAdapter(adapter);

        holder.getViewModel().startDownload();
    }
}
