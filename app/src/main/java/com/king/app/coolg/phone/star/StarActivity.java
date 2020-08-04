package com.king.app.coolg.phone.star;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityStarPhoneBinding;
import com.king.app.coolg.model.bean.BannerParams;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.model.setting.ViewProperty;
import com.king.app.coolg.phone.image.ImageManagerActivity;
import com.king.app.coolg.phone.order.OrderPhoneActivity;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.phone.record.list.SortDialogContent;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.phone.video.home.RecommendFragment;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.content.BannerSettingFragment;
import com.king.app.coolg.view.dialog.content.TagFragment;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.param.DataConstants;

import java.util.List;

/**
 * Created by Administrator on 2018/8/12 0012.
 */
@Route("StarPhone")
public class StarActivity extends MvvmActivity<ActivityStarPhoneBinding, StarViewModel> {

    public static final String EXTRA_STAR_ID = "key_star_id";

    private final int REQUEST_ADD_ORDER = 1602;

    private StarAdapter adapter;

    private RecommendBean mFilter;

    @Override
    protected int getContentView() {
        return R.layout.activity_star_phone;
    }

    @Override
    protected void initView() {

        mBinding.actionbar.setOnBackListener(() -> finish());

        mBinding.rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                // items 与 header之间的间隔
                if (position == 1) {
                    outRect.top = ScreenUtils.dp2px(10);
                }
            }
        });

        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_banner_setting:
                    showSettings();
                    break;
                case R.id.menu_sort:
                    changeSortType();
                    break;
                case R.id.menu_filter:
                    changeFilter();
                    break;
            }
        });

        mBinding.ivMore.setOnClickListener(v -> {
            Router.build("ImageManager")
                    .with(ImageManagerActivity.EXTRA_TYPE, ImageManagerActivity.TYPE_STAR)
                    .with(ImageManagerActivity.EXTRA_DATA, mModel.getStar().getId())
                    .go(StarActivity.this);
        });
    }

    @Override
    protected StarViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StarViewModel.class);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        mModel.loadStar(getIntent().getLongExtra(EXTRA_STAR_ID, -1));
    }

    @Override
    protected void initData() {
        mModel.starObserver.observe(this, star -> showStar(star));
        mModel.recordsObserver.observe(this, list -> showRecords(list));
        mModel.addOrderObserver.observe(this, star -> mModel.loadStarOrders(mModel.getStar().getId()));
        mModel.onlyRecordsObserver.observe(this, list -> {
            if (adapter != null) {
                int oldCount = adapter.getItemCount() - 2;
                // size发生变化（变少）用notifyItemRangeChanged会抛出error导致崩溃，必须全部刷新
                if (oldCount != list.size()) {
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                }
                // size无变化才能用notifyItemRangeChanged
                else {
                    adapter.setList(list);
                    adapter.notifyItemRangeChanged(1, adapter.getItemCount() - 2);
                }
            }
        });
        mModel.tagsObserver.observe(this, tags -> {
            if (adapter != null) {
                adapter.setTagList(tags);
                adapter.notifyDataSetChanged();
            }
        });

        mModel.setExpandImage(getStarId(), mBinding.ivStar);

        mModel.loadStar(getStarId());
    }

    private long getStarId() {
        return getIntent().getLongExtra(EXTRA_STAR_ID, -1);
    }

    private void showStar(Star star) {
        mBinding.actionbar.setTitle(star.getName());
    }

    private void showRecords(List<RecordProxy> list) {
        if (adapter == null) {
            adapter = new StarAdapter();
            adapter.setStar(mModel.getStar());
            adapter.setRelationships(mModel.getRelationList());
            adapter.setStudioList(mModel.getStudioList());
            adapter.setTagList(mModel.getTagList());
            adapter.setList(list);
            adapter.setSortMode(SettingProperty.getStarRecordsSortType());
            adapter.setOnListListener((view, record) -> goToRecordPage(record.getRecord().getId()));
            adapter.setOnHeadActionListener(new StarHeader.OnHeadActionListener() {
                @Override
                public void onClickRelationStar(StarRelationship relationship) {
                    goToStarPage(relationship.getStar().getId());
                }

                @Override
                public void onApplyImage(String path) {
                    selectOrderToSetCover(path);
                }

                @Override
                public void onDeleteImage(String path) {
                    showConfirmCancelMessage("Are you sure to delete this image on file system?",
                            (dialogInterface, i) -> {
                                mModel.deleteImage(path);
                                adapter.notifyItemChanged(0);
                            }, null);
                }

                @Override
                public void addStarToOrder(Star star) {
                    selectOrderToAddStar();
                }

                @Override
                public void onFilterStudio(long studioId) {
                    mModel.setStudioId(studioId);
                    mModel.loadStarRecords();
                }

                @Override
                public void onCancelFilterStudio(long studioId) {
                    // all records
                    mModel.setStudioId(0);
                    mModel.loadStarRecords();
                }

                @Override
                public void onAddTag() {
                    addTag();
                }

                @Override
                public void onDeleteTag(Tag bean) {
                    mModel.deleteTag(bean);
                }
            });
            mBinding.rvList.setAdapter(adapter);
        }
        else {
            adapter.setStar(mModel.getStar());
            adapter.setRelationships(mModel.getRelationList());
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

    private void addTag() {
        TagFragment fragment = new TagFragment();
        fragment.setOnTagSelectListener(tag -> mModel.addTag(tag));
        fragment.setTagType(DataConstants.TAG_TYPE_STAR);
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(fragment);
        dialogFragment.setTitle("Select tag");
        dialogFragment.setOnDismissListener(v -> mModel.refreshTags());
        dialogFragment.show(getSupportFragmentManager(), "TagFragment");
    }

    private void showSettings() {
        BannerSettingFragment content = new BannerSettingFragment();
        content.setParams(ViewProperty.getStarBannerParams());
        content.setOnAnimSettingListener(new BannerSettingFragment.OnAnimSettingListener() {
            @Override
            public void onParamsUpdated(BannerParams params) {

            }

            @Override
            public void onParamsSaved(BannerParams params) {
                ViewProperty.setStarBannerParams(params);
                // 只刷新头部
                adapter.notifyItemChanged(0);
            }
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Banner Setting");
        dialogFragment.show(getSupportFragmentManager(), "BannerSettingFragment");
    }

    public void changeSortType() {
        SortDialogContent content = new SortDialogContent();
        content.setDesc(SettingProperty.isStarRecordsSortDesc());
        content.setSortType(SettingProperty.getStarRecordsSortType());
        content.setOnSortListener((desc, sortMode) -> {
            SettingProperty.setStarRecordsSortType(sortMode);
            SettingProperty.setStarRecordsSortDesc(desc);
            if (adapter != null) {
                adapter.setSortMode(sortMode);
            }
            mModel.loadStarRecords();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Sort");
        dialogFragment.show(getSupportFragmentManager(), "SortDialogContent");
    }

    public void changeFilter() {
        RecommendFragment content = new RecommendFragment();
        content.setBean(mFilter);
        content.setOnRecommendListener(bean -> {
            mFilter = bean;
            mModel.setRecordFilter(bean);
            mModel.loadStarRecords();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Recommend Setting");
        dialogFragment.setContentFragment(content);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 2 / 3);
        dialogFragment.show(getSupportFragmentManager(), "RecommendFragment");
    }

    private void goToRecordPage(long recordId) {
        Router.build("RecordPhone")
                .with(RecordActivity.EXTRA_RECORD_ID, recordId)
                .go(this);
    }

    private void goToStarPage(long starId) {
        Router.build("StarPhone")
                .with(StarActivity.EXTRA_STAR_ID, starId)
                .go(this);
    }

    private void selectOrderToSetCover(String path) {
        Router.build("OrderPhone")
                .with(OrderPhoneActivity.EXTRA_SET_COVER, path)
                .go(this);
    }

    private void selectOrderToAddStar() {
        Router.build("OrderPhone")
                .with(OrderPhoneActivity.EXTRA_SELECT_MODE, true)
                .with(OrderPhoneActivity.EXTRA_SELECT_STAR, true)
                .requestCode(REQUEST_ADD_ORDER)
                .go(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_ORDER) {
            if (resultCode == RESULT_OK) {
                long orderId = data.getLongExtra(AppConstants.RESP_ORDER_ID, -1);
                mModel.addToOrder(orderId);
            }
        }
    }

    @Override
    protected void onResume() {
        if (adapter != null) {
            adapter.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (adapter != null) {
            adapter.onStop();
        }
        super.onStop();
    }

}
