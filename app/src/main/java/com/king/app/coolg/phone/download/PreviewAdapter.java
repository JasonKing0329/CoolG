package com.king.app.coolg.phone.download;

import android.util.SparseBooleanArray;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterDownloadPreviewItemBinding;
import com.king.app.coolg.model.http.bean.data.DownloadItem;
import com.king.app.coolg.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 11:54
 */
public class PreviewAdapter extends BaseBindingAdapter<AdapterDownloadPreviewItemBinding, DownloadItem> {

    private SparseBooleanArray checkMap;
    private RequestOptions requestOptions;

    public PreviewAdapter() {
        checkMap = new SparseBooleanArray();
        requestOptions = GlideUtil.getDownloadPreviewOptions();
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_download_preview_item;
    }

    @Override
    protected void onBindItem(AdapterDownloadPreviewItemBinding binding, int position, DownloadItem bean) {

        if (list.get(position).getKey() != null) {
            binding.tvName.setText(list.get(position).getKey() + "/" + list.get(position).getName());
        }
        else {
            binding.tvName.setText(list.get(position).getName());
        }
        binding.cbCheck.setChecked(checkMap.get(position));

        Glide.with(CoolApplication.getInstance())
                .load(list.get(position).getPath())
                .apply(requestOptions)
                .into(binding.ivImage);
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (checkMap.get(position)) {
            checkMap.put(position, false);
        }
        else {
            checkMap.put(position, true);
        }
        notifyItemChanged(position);
    }

    public List<DownloadItem> getCheckedItems() {
        List<DownloadItem> result = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i ++) {
                if (checkMap.get(i)) {
                    result.add(list.get(i));
                }
            }
        }
        return result;
    }

    public void selectAll() {
        if (list != null) {
            for (int i = 0; i < list.size(); i ++) {
                checkMap.put(i, true);
            }
        }
    }

    public void unSelectAll() {
        if (list != null) {
            for (int i = 0; i < list.size(); i ++) {
                checkMap.put(i, false);
            }
        }
    }

}
