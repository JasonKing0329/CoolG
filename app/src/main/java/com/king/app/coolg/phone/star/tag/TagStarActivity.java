package com.king.app.coolg.phone.star.tag;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.PopupMenu;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityStarTagBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.TagAdapter;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.star.StarRatingDialog;
import com.king.app.coolg.phone.star.list.OnStarRatingListener;
import com.king.app.coolg.phone.star.list.StarGridAdapter;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.gdb.data.entity.Tag;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/6/29 14:04
 */
@Route("TagStar")
public class TagStarActivity extends MvvmActivity<ActivityStarTagBinding, TagStarViewModel> {

    private TagAdapter tagAdapter;

    private StarGridAdapter starAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_star_tag;
    }

    @Override
    protected TagStarViewModel createViewModel() {
        return generateViewModel(TagStarViewModel.class);
    }

    @Override
    protected void initView() {

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        mBinding.rvTags.setLayoutManager(manager);
        mBinding.rvTags.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(10);
                outRect.top = ScreenUtils.dp2px(5);
                outRect.bottom = ScreenUtils.dp2px(5);
            }
        });

        mBinding.rvStars.setLayoutManager(new GridLayoutManager(this, 2));

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_classic:
                    goToClassicPage();
                    break;
                case R.id.menu_tag_sort_mode:
                    setTagSortMode();
                    break;
            }
        });
        mBinding.actionbar.registerPopupMenu(R.id.menu_sort);
        mBinding.actionbar.setPopupMenuProvider((iconMenuId, anchorView) -> {
            if (iconMenuId == R.id.menu_sort) {
                return createSortPopup(anchorView);
            }
            return null;
        });

        mBinding.fabTop.setOnClickListener(v -> mBinding.rvStars.scrollToPosition(0));
    }

    private PopupMenu createSortPopup(View anchorView) {
        PopupMenu menu = new PopupMenu(this, anchorView);
        menu.getMenuInflater().inflate(R.menu.player_sort, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_sort_name:
                    mModel.sortList(AppConstants.STAR_SORT_NAME);
                    break;
                case R.id.menu_sort_records:
                    mModel.sortList(AppConstants.STAR_SORT_RECORDS);
                    break;
                case R.id.menu_sort_rating:
                    mModel.sortList(AppConstants.STAR_SORT_RATING);
                    break;
                case R.id.menu_sort_rating_face:
                    mModel.sortList(AppConstants.STAR_SORT_RATING_FACE);
                    break;
                case R.id.menu_sort_rating_body:
                    mModel.sortList(AppConstants.STAR_SORT_RATING_BODY);
                    break;
                case R.id.menu_sort_rating_dk:
                    mModel.sortList(AppConstants.STAR_SORT_RATING_DK);
                    break;
                case R.id.menu_sort_rating_sexuality:
                    mModel.sortList(AppConstants.STAR_SORT_RATING_SEXUALITY);
                    break;
                case R.id.menu_sort_rating_passion:
                    mModel.sortList(AppConstants.STAR_SORT_RATING_PASSION);
                    break;
                case R.id.menu_sort_rating_video:
                    mModel.sortList(AppConstants.STAR_SORT_RATING_VIDEO);
                    break;
                case R.id.menu_sort_random:
                    mModel.sortList(AppConstants.STAR_SORT_RANDOM);
                    break;
            }
            return false;
        });
        return menu;
    }

    private void setTagSortMode() {
        new AlertDialogFragment()
                .setTitle(null)
                .setItems(AppConstants.TAG_SORT_MODE_TEXT, (dialog, which) -> {
                    SettingProperty.setTagSortType(which);
                    mModel.onTagSortChanged();
                    mModel.startSortTag(false);
                }).show(getSupportFragmentManager(), "AlertDialogFragment");
    }

    private void goToClassicPage() {
        Router.build("StarListPhone")
                .go(this);
    }

    @Override
    protected void initData() {
        mModel.tagsObserver.observe(this, tags -> showTags(tags));
        mModel.starsObserver.observe(this, list -> showStars(list));
        mModel.focusTagPosition.observe(this, position -> {
            tagAdapter.setSelection(position);
            tagAdapter.notifyDataSetChanged();
        });

        mModel.loadTags();
    }

    private void showTags(List<Tag> tags) {
        if (tagAdapter == null) {
            tagAdapter = new TagAdapter();
            tagAdapter.setList(tags);
            tagAdapter.setSelection(0);
            tagAdapter.setOnItemClickListener((view, position, data) -> mModel.loadTagStars(data.getId()));
            mBinding.rvTags.setAdapter(tagAdapter);
        }
        else {
            tagAdapter.setList(tags);
            tagAdapter.notifyDataSetChanged();
        }
    }

    private void showStars(List<StarProxy> list) {
        if (starAdapter == null) {
            starAdapter = new StarGridAdapter();
            starAdapter.setList(list);
            starAdapter.setOnStarRatingListener((position, starId) -> {
                StarRatingDialog dialog = new StarRatingDialog();
                dialog.setStarId(starId);
                dialog.setOnDismissListener(dialog1 -> {
                    starAdapter.notifyItemChanged(position);
                });
                dialog.show(getSupportFragmentManager(), "StarRatingDialog");
            });
            starAdapter.setOnItemClickListener((view, position, data) -> goToStarPage(data.getStar().getId()));
            mBinding.rvStars.setAdapter(starAdapter);
        }
        else {
            starAdapter.setList(list);
            starAdapter.notifyDataSetChanged();
        }
    }

    private void goToStarPage(long starId) {
        Router.build("StarPhone")
                .with(StarActivity.EXTRA_STAR_ID, starId)
                .go(this);
    }

}
