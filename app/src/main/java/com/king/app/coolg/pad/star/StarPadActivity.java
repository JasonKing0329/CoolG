package com.king.app.coolg.pad.star;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityStarPadBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.pad.record.list.RecordListPadFragment;
import com.king.app.coolg.phone.order.OrderPhoneActivity;
import com.king.app.coolg.phone.record.list.SortDialogContent;
import com.king.app.coolg.phone.star.StarOrdersAdapter;
import com.king.app.coolg.phone.star.StarRatingDialog;
import com.king.app.coolg.phone.star.StarRelationship;
import com.king.app.coolg.phone.star.StarRelationshipAdapter;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.phone.video.home.RecommendFragment;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.gdb.data.entity.FavorStarOrder;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;
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

    private RecordListPadFragment ftRecord;
    private RecommendBean mFilter;

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

        mBinding.rvStar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvStar.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = ScreenUtils.dp2px(10);
                outRect.bottom = ScreenUtils.dp2px(10);
            }
        });
        mBinding.rvOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvOrder.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(10);
            }
        });
        mBinding.rvRelation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvRelation.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(10);
            }
        });
        mBinding.tvRating.setOnClickListener(v -> showRatingDialog());
        mBinding.ivIconBack.setOnClickListener(v -> finish());
        mBinding.ivIconSort.setOnClickListener(v -> changeSortType());
        mBinding.ivIconFilter.setOnClickListener(v -> changeFilter());
        mBinding.tvAddOrder.setOnClickListener(v -> selectOrderToAddStar());
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
        mModel.ordersObserver.observe(this, list -> showOrders(list));
        mModel.addOrderObserver.observe(this, star -> mModel.loadStarOrders(mModel.getStar().getId()));

        mModel.loadStar(starId);
        mModel.loadStarOrders(starId);
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

    private void showImages(List<String> list) {
        StarImageAdapter adapter = new StarImageAdapter();
        adapter.setList(list);
        adapter.setOnItemClickListener((view, position, data) -> showEditPopup(view, data));
        mBinding.rvStar.setAdapter(adapter);
    }

    private void showEditPopup(View view, String path) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenuInflater().inflate(R.menu.popup_record_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.menu_add_to_order).setVisible(false);
        menu.getMenu().findItem(R.id.menu_add_to_play_order).setVisible(false);
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_set_cover:
                    selectOrderToSetCover(path);
                    break;
                case R.id.menu_delete:
                    mModel.deleteImage(path);
                    showImages(mModel.getStarImageList());
                    break;
            }
            return false;
        });
        menu.show();
    }

    private void showRelationships(List<StarRelationship> list) {
        mBinding.tvRelation.setText("Relationships(" + list.size() + "人)");
        StarRelationshipAdapter adapter = new StarRelationshipAdapter();
        adapter.setList(list);
        adapter.setOnItemClickListener((view, position, data) -> goToStarPage(data.getStar().getId()));
        mBinding.rvRelation.setAdapter(adapter);
    }

    private void showOrders(List<FavorStarOrder> list) {
        if (ListUtil.isEmpty(list)) {
            mBinding.tvAddOrder.setText("Orders");
            mBinding.rvOrder.setVisibility(View.GONE);
        }
        else {
            mBinding.tvAddOrder.setText("Orders\n" + list.size());
            mBinding.rvOrder.setVisibility(View.VISIBLE);
            StarOrdersAdapter adapter = new StarOrdersAdapter();
            adapter.setList(list);
            adapter.setOnDeleteListener(order -> {
                mModel.deleteOrderOfStar(order.getId(), mModel.getStar().getId());
                mModel.loadStarOrders(mModel.getStar().getId());
            });
            mBinding.rvOrder.setAdapter(adapter);
        }
    }

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
