package com.king.app.coolg.phone.download;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterDownloadListBinding;
import com.king.app.coolg.model.bean.DownloadItemProxy;
import com.king.app.coolg.utils.FileSizeUtil;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 13:28
 */
public class ListAdapter extends BaseBindingAdapter<AdapterDownloadListBinding, DownloadItemProxy> {
    
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_download_list;
    }

    @Override
    protected void onBindItem(AdapterDownloadListBinding binding, int position, DownloadItemProxy bean) {

        if (list.get(position).getItem().getKey() != null) {
            binding.tvName.setText(list.get(position).getItem().getKey() + "/" + list.get(position).getItem().getName());
        }
        else {
            binding.tvName.setText(list.get(position).getItem().getName());
        }
        binding.tvSize.setText(FileSizeUtil.convertFileSize(list.get(position).getItem().getSize()));
        binding.progress.setProgress(list.get(position).getProgress());
    }
}
