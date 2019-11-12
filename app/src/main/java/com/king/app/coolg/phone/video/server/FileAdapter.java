package com.king.app.coolg.phone.video.server;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterServerFilesBinding;
import com.king.app.coolg.model.http.bean.data.FileBean;
import com.king.app.coolg.utils.ScreenUtils;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/11/11 9:50
 */
public class FileAdapter extends BaseBindingAdapter<AdapterServerFilesBinding, FileBean> {

    private boolean isTablet;

    private OnActionListener onActionListener;

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
        isTablet = ScreenUtils.isTablet();
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_server_files;
    }

    @Override
    protected void onBindItem(AdapterServerFilesBinding binding, int position, FileBean bean) {
        binding.setBean(bean);
        binding.setIsTablet(isTablet);
        if (bean.isFolder()) {
            binding.ivFolder.setImageResource(R.drawable.ic_folder_yellow_700_36dp);
        }
        else {
            binding.ivFolder.setImageResource(R.drawable.ic_play_circle_outline_3f51b5_36dp);
        }
        binding.ivOpen.setOnClickListener(v -> onActionListener.onOpenServer(bean));
    }

    public interface OnActionListener {
        void onOpenServer(FileBean bean);
    }
}
