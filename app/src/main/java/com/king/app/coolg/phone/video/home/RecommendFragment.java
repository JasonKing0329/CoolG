package com.king.app.coolg.phone.video.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentVideoRecommendBinding;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.DraggableContentFragment;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/26 17:47
 */
public class RecommendFragment extends DraggableContentFragment<FragmentVideoRecommendBinding> {

    private RecommendViewModel mModel;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_video_recommend;
    }

    @Override
    protected void initView() {
        mModel = ViewModelProviders.of(this).get(RecommendViewModel.class);
        mBinding.setModel(mModel);

        mBinding.btnOften.setOnClickListener(v -> {
            new AlertDialogFragment()
                    .setItems(getResources().getStringArray(R.array.recommend_often_use)
                            , (dialog, which) -> {

                            })
                    .show(getChildFragmentManager(), "AlertDialogFragment");
        });
    }

}
