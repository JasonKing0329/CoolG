package com.king.app.coolg.phone.studio.page;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentStudioPageBinding;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.studio.StudioHolder;
import com.king.app.coolg.phone.studio.StudioViewModel;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/18 17:53
 */
public class StudioPageFragment extends MvvmFragment<FragmentStudioPageBinding, StudioViewModel> {

    private static final String ARG_STUDIO_ID = "studio_id";

    public static StudioPageFragment newInstance(long studioId) {
        StudioPageFragment fragment = new StudioPageFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_STUDIO_ID, studioId);
        fragment.setArguments(bundle);
        return fragment;
    }

    private StudioHolder holder;

    private StudioPageAdapter adapter;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (StudioHolder) holder;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_studio_page;
    }

    @Override
    protected StudioViewModel createViewModel() {
        return ViewModelProviders.of(getActivity()).get(StudioViewModel.class);
    }

    @Override
    protected void onCreate(View view) {

        holder.getJActionBar().setOnBackListener(() -> holder.backToList());
        holder.getJActionBar().removeMenu();

        GridLayoutManager glm = new GridLayoutManager(getContext(), 3);
        adapter = new StudioPageAdapter();
        adapter.setOnClickStarListener(star -> showStar(star));
        adapter.setOnClickRecordListener(record -> showRecord(record));
        adapter.setSpanCount(3);
        glm.setSpanSizeLookup(adapter.getSpanSizeLookup());
        mBinding.rvPage.addItemDecoration(adapter.getItemDecorator());
        mBinding.rvPage.setLayoutManager(glm);
    }

    private void showStar(StarNumberItem star) {
        Router.build("StarPhone")
                .with(StarActivity.EXTRA_STAR_ID, star.getStar().getId())
                .go(this);
    }

    private void showRecord(RecordProxy record) {
        Router.build("RecordPhone")
                .with(RecordActivity.EXTRA_RECORD_ID, record.getRecord().getId())
                .go(this);
    }

    @Override
    protected void onCreateData() {
        mModel.pageLoadingObserver.observe(this, show -> onLoadingChanged(show));
        mModel.pageMessageObserver.observe(this, message -> onMessageObserved(message));

        mModel.pageObserver.observe(this, pageItem -> showPageItem(pageItem));

        mModel.loadPageData(getArguments().getLong(ARG_STUDIO_ID));
    }

    private void showPageItem(StudioPageItem pageItem) {
        holder.getJActionBar().setTitle(pageItem.getOrder().getName());
        mBinding.tvVideos.setText(pageItem.getStrCount());
        mBinding.tvHigh.setText(pageItem.getStrHighCount());
        mBinding.tvTime.setText(pageItem.getUpdateTime());

        adapter.setPageItem(pageItem);
        mBinding.rvPage.setAdapter(adapter);
    }
}
