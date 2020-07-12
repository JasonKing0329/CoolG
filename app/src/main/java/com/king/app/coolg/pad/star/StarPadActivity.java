package com.king.app.coolg.pad.star;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityStarPadBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.pad.gallery.GalleryFragment;
import com.king.app.coolg.pad.record.list.RecordListPadFragment;
import com.king.app.coolg.phone.order.OrderPhoneActivity;
import com.king.app.coolg.phone.record.TagAdapter;
import com.king.app.coolg.phone.record.list.SortDialogContent;
import com.king.app.coolg.phone.star.StarRatingDialog;
import com.king.app.coolg.phone.star.StarRelationship;
import com.king.app.coolg.phone.star.StarRelationshipAdapter;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.phone.video.home.RecommendFragment;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.content.TagFragment;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.param.DataConstants;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/20 16:41
 */
@Route("StarPad")
public class StarPadActivity extends MvvmActivity<ActivityStarPadBinding, StarPadViewModel> {

    public static final String EXTRA_STAR_ID = "key_star_id";
    private final int REQUEST_ADD_ORDER = 1602;

    private TagAdapter tagAdapter;

    private RecordListPadFragment ftRecord;
    private RecommendBean mFilter;

    private StarImagesDecoration imagesDecoration;
    private StarImageAdapter starImageAdapter;

    private GalleryFragment galleryFragment;

    @Override
    protected int getContentView() {
        return R.layout.activity_star_pad;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mBinding.tvTagTop.setSelected(false);
        mBinding.tvTagBottom.setSelected(false);
        mBinding.tvTagVideo.setSelected(true);
        initData();
    }

    @Override
    protected void initView() {

//        mBinding.rvOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        mBinding.rvOrder.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                outRect.left = ScreenUtils.dp2px(10);
//            }
//        });
        mBinding.rvRelation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvRelation.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(10);
            }
        });
        StaggeredGridLayoutManager tagManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mBinding.rvTags.setLayoutManager(tagManager);
        mBinding.rvTags.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.right = ScreenUtils.dp2px(10);
                outRect.top = ScreenUtils.dp2px(5);
                outRect.bottom = ScreenUtils.dp2px(5);
            }
        });
        mBinding.tvRating.setOnClickListener(v -> showRatingDialog());
        mBinding.ivIconBack.setOnClickListener(v -> finish());
        mBinding.ivIconSort.setOnClickListener(v -> changeSortType());
        mBinding.ivIconFilter.setOnClickListener(v -> changeFilter());
        mBinding.ivAddTag.setOnClickListener(v -> addTag());
//        mBinding.ivAddOrder.setOnClickListener(v -> selectOrderToAddStar());
        mBinding.tvTagVideo.setOnClickListener(v -> {
            if (!mBinding.tvTagVideo.isSelected()) {
                mBinding.tvTagVideo.setSelected(true);
                mBinding.tvTagTop.setSelected(false);
                mBinding.tvTagBottom.setSelected(false);
                ftRecord.filterByStarType(0);
            }
        });
        mBinding.tvTagTop.setOnClickListener(v -> {
            if (!mBinding.tvTagTop.isSelected()) {
                mBinding.tvTagVideo.setSelected(false);
                mBinding.tvTagTop.setSelected(true);
                mBinding.tvTagBottom.setSelected(false);
                ftRecord.filterByStarType(DataConstants.VALUE_RELATION_TOP);
            }
        });
        mBinding.tvTagBottom.setOnClickListener(v -> {
            if (!mBinding.tvTagBottom.isSelected()) {
                mBinding.tvTagVideo.setSelected(false);
                mBinding.tvTagTop.setSelected(false);
                mBinding.tvTagBottom.setSelected(true);
                ftRecord.filterByStarType(DataConstants.VALUE_RELATION_BOTTOM);
            }
        });
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

    private void showRatingDialog() {
        StarRatingDialog dialog = new StarRatingDialog();
        dialog.setStarId(mModel.getStar().getId());
        dialog.setOnDismissListener(dialog1 -> mModel.loadRating());
        dialog.show(getSupportFragmentManager(), "StarRatingDialog");
    }

    public void changeSortType() {
        SortDialogContent content = new SortDialogContent();
        content.setDesc(SettingProperty.isRecordSortDesc());
        content.setSortType(SettingProperty.getRecordSortType());
        content.setOnSortListener((desc, sortMode) -> {
            SettingProperty.setStarRecordsSortType(sortMode);
            SettingProperty.setStarRecordsSortDesc(desc);
            ftRecord.onStarRecordsSortChanged();
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
            ftRecord.onFilterChanged(mFilter);
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Recommend Setting");
        dialogFragment.setContentFragment(content);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 2 / 3);
        dialogFragment.show(getSupportFragmentManager(), "RecommendFragment");
    }

    @Override
    protected StarPadViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StarPadViewModel.class);
    }

    @Override
    protected void initData() {
        long starId = getIntent().getLongExtra(EXTRA_STAR_ID, -1);
        ftRecord = RecordListPadFragment.newInstance(starId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_records, ftRecord, "RecordListPadFragment")
                .commit();

        mModel.starObserver.observe(this, star -> showStar(star));
        mModel.ratingObserver.observe(this, rating -> showRating(rating));
        mModel.imagesObserver.observe(this, list -> showImages(list));
        mModel.relationshipsObserver.observe(this, list -> showRelationships(list));
//        mModel.ordersObserver.observe(this, list -> showOrders(list));
        mModel.addOrderObserver.observe(this, star -> mModel.loadStarOrders(mModel.getStar().getId()));
        mModel.tagsObserver.observe(this, list -> showTags(list));

        mModel.loadStar(starId);
//        mModel.loadStarOrders(starId);
    }

    private void showTags(List<Tag> list) {
        if (list.size() == 0) {
            mBinding.rvTags.setVisibility(View.GONE);
            mBinding.tvNoTag.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.rvTags.setVisibility(View.VISIBLE);
            mBinding.tvNoTag.setVisibility(View.GONE);
        }
        if (tagAdapter == null) {
            tagAdapter = new TagAdapter();
            tagAdapter.setOnDeleteListener((position, bean) -> mModel.deleteTag(bean));
            tagAdapter.setOnItemLongClickListener((view, position, data) -> {
                tagAdapter.toggleDelete();
                tagAdapter.notifyDataSetChanged();
            });
            tagAdapter.setList(list);
            mBinding.rvTags.setAdapter(tagAdapter);
        }
        else {
            tagAdapter.setList(list);
            tagAdapter.notifyDataSetChanged();
        }
    }

    private void showStar(Star star) {
        mBinding.tvName.setText(star.getName());
        mBinding.tvTagVideo.setText("Videos  " + star.getRecords());
        if (star.getBetop() > 0) {
            mBinding.tvTagTop.setText("Top  " + star.getBetop());
            mBinding.tvTagTop.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.tvTagTop.setVisibility(View.GONE);
        }
        if (star.getBebottom() > 0) {
            mBinding.tvTagBottom.setText("Bottom  " + star.getBebottom());
            mBinding.tvTagBottom.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.tvTagBottom.setVisibility(View.GONE);
        }
        mBinding.tvTagVideo.setSelected(true);
    }

    private void showRating(StarRating rating) {
        if (rating == null) {
            mBinding.tvRating.setText(StarRatingUtil.NON_RATING);
        }
        else {
            mBinding.tvRating.setText(StarRatingUtil.getRatingValue(rating.getComplex()));
        }
    }

    private class StarImagesDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;

        private StarImagesDecoration(int spanCount) {
            this.spanCount = spanCount;
        }

        public int getSpanCount() {
            return spanCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view);
            // 1列，设置上间距
            if (spanCount == 1) {
                if (position < 1) {
                    outRect.top = 0;
                }
                else {
                    outRect.top = mModel.getImgMarginSingle();
                }
            }
            // 2列，设置上间距、左右间距
            else {
                if (position < 2) {
                    outRect.top = 0;
                }
                else {
                    outRect.top = mModel.getImgMarginSingle();
                }
                // 由于采用了瀑布流，不能简单的以为第一列是偶数，第二列是奇数，只能左右都设置为相同
                outRect.right = mModel.getImgMarginSingle() / 2;
                outRect.left = mModel.getImgMarginSingle() / 2;
            }
        }
    }

    private void showImages(List<StarImageBean> list) {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(mModel.getStarImgSpan(), StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mBinding.rvStar.setLayoutManager(manager);
        if (imagesDecoration == null || mModel.getStarImgSpan() != imagesDecoration.getSpanCount()) {
            mBinding.rvStar.removeItemDecoration(imagesDecoration);
            imagesDecoration = new StarImagesDecoration(mModel.getStarImgSpan());
            mBinding.rvStar.addItemDecoration(imagesDecoration);
        }
        if (starImageAdapter == null) {
            starImageAdapter = new StarImageAdapter();
            starImageAdapter.setList(list);
            starImageAdapter.setOnItemClickListener((view, position, data) -> showEditPopup(view, position, data));
            mBinding.rvStar.setAdapter(starImageAdapter);
        }
        else {
            starImageAdapter.setList(list);
            starImageAdapter.notifyDataSetChanged();
        }
    }

    private void showEditPopup(View view, int position, StarImageBean bean) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenuInflater().inflate(R.menu.popup_record_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.menu_add_to_order).setVisible(false);
        menu.getMenu().findItem(R.id.menu_add_to_play_order).setVisible(false);
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_set_cover:
                    selectOrderToSetCover(bean.getPath());
                    break;
                case R.id.menu_delete:
                    mModel.deleteImage(bean.getPath());
                    mModel.reloadImages();
                    break;
                case R.id.menu_gallery:
                    viewInGallery(position, bean);
                    break;
            }
            return false;
        });
        menu.show();
    }

    private void viewInGallery(int position, StarImageBean bean) {
        galleryFragment = new GalleryFragment();
        galleryFragment.setImageList(mModel.getStarImageList());
        galleryFragment.setInitPosition(position);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_ft, galleryFragment, "GalleryFragment")
                .commit();
        mBinding.flFt.setVisibility(View.VISIBLE);
    }

    private void showRelationships(List<StarRelationship> list) {
        mBinding.tvRelation.setText("Relationships(" + list.size() + "人)");
        StarRelationshipAdapter adapter = new StarRelationshipAdapter();
        adapter.setList(list);
        adapter.setOnItemClickListener((view, position, data) -> goToStarPage(data.getStar().getId()));
        mBinding.rvRelation.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (galleryFragment != null && galleryFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .remove(galleryFragment)
                    .commit();
            mBinding.flFt.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }
    }

//    private void showOrders(List<FavorStarOrder> list) {
//        if (ListUtil.isEmpty(list)) {
//            mBinding.tvOrderTitle.setText("Orders");
//            mBinding.rvOrder.setVisibility(View.GONE);
//        }
//        else {
//            mBinding.tvOrderTitle.setText("Orders(" + list.size() + ")");
//            mBinding.rvOrder.setVisibility(View.VISIBLE);
//            StarOrdersAdapter adapter = new StarOrdersAdapter();
//            adapter.setList(list);
//            adapter.setOnDeleteListener(order -> {
//                mModel.deleteOrderOfStar(order.getId(), mModel.getStar().getId());
//                mModel.loadStarOrders(mModel.getStar().getId());
//            });
//            mBinding.rvOrder.setAdapter(adapter);
//        }
//    }

    private void goToStarPage(long starId) {
        Router.build("StarPad")
                .with(StarPadActivity.EXTRA_STAR_ID, starId)
                .go(this);
    }

    private void selectOrderToAddStar() {
        Router.build("OrderPhone")
                .with(OrderPhoneActivity.EXTRA_SELECT_MODE, true)
                .with(OrderPhoneActivity.EXTRA_SELECT_STAR, true)
                .requestCode(REQUEST_ADD_ORDER)
                .go(this);
    }

    private void selectOrderToSetCover(String path) {
        Router.build("OrderPhone")
                .with(OrderPhoneActivity.EXTRA_SET_COVER, path)
                .go(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ORDER) {
            if (resultCode == RESULT_OK) {
                long orderId = data.getLongExtra(AppConstants.RESP_ORDER_ID, -1);
                mModel.addToOrder(orderId);
            }
        }
    }
}
