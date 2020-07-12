package com.king.app.coolg.pad.gallery;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentImagePagerBinding;
import com.king.app.coolg.utils.TouchToClickListener;

import java.util.List;

/**
 * @description:
 * @author：Jing
 * @date: 2020/7/11 12:14
 */
public class GalleryFragment extends MvvmFragment<FragmentImagePagerBinding, GalleryViewModel> {

    private GalleryAdapter galleryAdapter;
    private ThumbAdapter thumbAdapter;

    private List<String> imageList;
    private int initPosition;

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public void setInitPosition(int initPosition) {
        this.initPosition = initPosition;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_image_pager;
    }

    @Override
    protected GalleryViewModel createViewModel() {
        return ViewModelProviders.of(this).get(GalleryViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        mBinding.setModel(mModel);
        mModel.setSelection(initPosition);
        mBinding.rvThumb.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                thumbAdapter.setSelection(i);
                thumbAdapter.notifyDataSetChanged();
                mBinding.rvThumb.scrollToPosition(i);
                mModel.onSelectionChanged(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        new TouchToClickListener(mBinding.viewpager).setOnClickListener(v -> {
            if (mBinding.rvThumb.getVisibility() == View.VISIBLE) {
                mBinding.rvThumb.setVisibility(View.GONE);
            }
            else {
                mBinding.rvThumb.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onCreateData() {
        mModel.itemsObserver.observe(this, list -> showList(list));
        mModel.setPosition.observe(this, position -> {
            mBinding.viewpager.setCurrentItem(position, false);
            thumbAdapter.setSelection(position);
            thumbAdapter.notifyDataSetChanged();
            mBinding.rvThumb.scrollToPosition(position);
        });
        mModel.convertImages(imageList);
    }

    private void showList(List<ThumbBean> list) {
        galleryAdapter = new GalleryAdapter();
        galleryAdapter.setList(list);
        mBinding.viewpager.setAdapter(galleryAdapter);

        thumbAdapter = new ThumbAdapter();
        thumbAdapter.setList(list);
        thumbAdapter.setOnItemClickListener((view, position, data) -> {
            mBinding.viewpager.setCurrentItem(position, false);
            mModel.onSelectionChanged(position);
        });
        mBinding.rvThumb.setAdapter(thumbAdapter);
    }
}
