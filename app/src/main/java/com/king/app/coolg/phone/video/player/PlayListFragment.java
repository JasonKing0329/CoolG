package com.king.app.coolg.phone.video.player;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentVideoPlayListBinding;
import com.king.app.coolg.model.bean.PlayList;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.video.home.RecommendFragment;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/16 11:34
 */
public class PlayListFragment extends MvvmFragment<FragmentVideoPlayListBinding, BaseViewModel> {

    private PlayerViewModel playerViewModel;

    private PlayListAdapter adapter;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_video_play_list;
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void onCreate(View view) {
        playerViewModel = ViewModelProviders.of(getActivity()).get(PlayerViewModel.class);
        mBinding.setModel(playerViewModel);

        mBinding.rvList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mBinding.ivClose.setOnClickListener(v -> playerViewModel.closeListObserver.setValue(true));

        mBinding.ivClear.setOnClickListener(v -> {
            showConfirmCancelMessage("This action will clear all play items, continue?"
                    , (dialog, which) -> playerViewModel.clearAll()
                    , null);
        });

        mBinding.tvPlayMode.setOnClickListener(v -> playerViewModel.switchPlayMode());

        mBinding.tvTitle.setSelected(true);
        mBinding.tvTitle.setOnClickListener(v -> {
            playerViewModel.setIsCustomRandomPlay(false);
            mBinding.tvTitle.setSelected(true);
            mBinding.tvRandom.setSelected(false);
            mBinding.ivRandomSetting.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        });
        mBinding.tvRandom.setOnClickListener(v -> {
            playerViewModel.setIsCustomRandomPlay(true);
            mBinding.tvRandom.setSelected(true);
            mBinding.tvTitle.setSelected(false);
            mBinding.ivRandomSetting.setColorFilter(getResources().getColor(R.color.yellowF7D23E), PorterDuff.Mode.SRC_IN);
        });
        mBinding.ivRandomSetting.setOnClickListener(v -> setRecommend());
    }

    private void setRecommend() {
        RecommendFragment content = new RecommendFragment();
        content.setHideOnline(true);
        content.setBean(SettingProperty.getVideoRecBean());
        content.setOnRecommendListener(bean -> playerViewModel.updateRecommend(bean));
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Recommend Setting");
        dialogFragment.setContentFragment(content);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight());
        dialogFragment.show(getChildFragmentManager(), "RecommendFragment");
    }

    @Override
    protected void onCreateData() {
        playerViewModel.playIndexObserver.observe(this, index -> {
            if (adapter != null) {
                LinearLayoutManager manager = (LinearLayoutManager) mBinding.rvList.getLayoutManager();
                manager.scrollToPosition(index);
                adapter.setPlayIndex(index);
                adapter.notifyDataSetChanged();
            }
        });
        playerViewModel.itemsObserver.observe(this, list -> showList(list));
    }

    private void showList(List<PlayList.PlayItem> list) {
        if (adapter == null) {
            adapter = new PlayListAdapter();
            adapter.setList(list);
            adapter.setPlayIndex(playerViewModel.getPlayIndex());
            adapter.enableDelete(true);
            adapter.setOnItemClickListener((view, position, data) -> playerViewModel.playVideoAt(position));
            adapter.setOnDeleteListener((position, bean) -> {
                playerViewModel.deletePlayItem(position, bean);
                adapter.notifyItemRemoved(position);
            });
            mBinding.rvList.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }
}
