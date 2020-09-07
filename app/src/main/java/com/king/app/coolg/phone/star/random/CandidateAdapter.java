package com.king.app.coolg.phone.star.random;

import android.view.View;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterCandidateStarBinding;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.gdb.data.entity.Star;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/9/7 11:31
 */
public class CandidateAdapter extends BaseBindingAdapter<AdapterCandidateStarBinding, Star> {

    private boolean isDeleteMode;

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public void setDeleteMode(boolean deleteMode) {
        isDeleteMode = deleteMode;
    }

    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_candidate_star;
    }

    @Override
    protected void onBindItem(AdapterCandidateStarBinding binding, int position, Star bean) {
        binding.tvName.setText(bean.getName());
        GlideApp.with(binding.ivHead.getContext())
                .load(ImageProvider.getStarRandomPath(bean.getName(), null))
                .error(R.drawable.def_person_square)
                .into(binding.ivHead);
        binding.ivDelete.setVisibility(isDeleteMode ? View.VISIBLE:View.GONE);
        binding.ivDelete.setOnClickListener(view -> onDeleteListener.onDeleteCandidate(position, bean));
    }

    public interface OnDeleteListener {
        void onDeleteCandidate(int position, Star star);
    }
}
