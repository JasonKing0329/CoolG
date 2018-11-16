package com.king.app.coolg.phone.video.player;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.base.adapter.BaseRecyclerAdapter;
import com.king.app.coolg.databinding.FragmentVideoPlayListBinding;
import com.king.app.coolg.phone.video.PlayItemViewBean;

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

        mBinding.rvList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mBinding.ivClose.setOnClickListener(v -> playerViewModel.closeListObserver.setValue(true));

        playerViewModel.playIndexObserver.observe(this, index -> {
            if (adapter != null) {
                adapter.setPlayIndex(index);
                adapter.notifyDataSetChanged();
            }
        });
        playerViewModel.itemsObserver.observe(this, list -> showList(list));
    }

    @Override
    protected void onCreateData() {

    }

    private void showList(List<PlayItemViewBean> list) {
        mBinding.tvTitle.setText("Play List (" + list.size() + ")");
        if (adapter == null) {
            adapter = new PlayListAdapter();
            adapter.setList(list);
//            adapter.setPlayIndex(playerViewModel.getPlayIndex());
            adapter.setOnItemClickListener((view, position, data) -> playerViewModel.playVideoAt(position));
            mBinding.rvList.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }
}
