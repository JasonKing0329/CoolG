package com.king.app.coolg.phone.star.tag;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.PopupMenu;

import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.star.StarRatingDialog;
import com.king.app.coolg.phone.star.list.StarGridAdapter;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.jactionbar.JActionbar;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/6/29 14:04
 */
public abstract class AbsTagStarActivity<T extends ViewDataBinding> extends MvvmActivity<T, TagStarViewModel> {

    private StarGridAdapter starAdapter;

    @Override
    protected TagStarViewModel createViewModel() {
        return generateViewModel(TagStarViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.tagsObserver.observe(this, tags -> showTags(tags));
        mModel.starsObserver.observe(this, list -> showStars(list));
        mModel.focusTagPosition.observe(this, position -> focusOnTag(position));

        mModel.loadTags();
    }

    protected void initActionBar(JActionbar actionbar) {
        actionbar.setOnBackListener(() -> onBackPressed());
        actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_classic:
                    goToClassicPage();
                    break;
                case R.id.menu_tag_sort_mode:
                    setTagSortMode();
                    break;
            }
        });
        actionbar.registerPopupMenu(R.id.menu_sort);
        actionbar.setPopupMenuProvider((iconMenuId, anchorView) -> {
            if (iconMenuId == R.id.menu_sort) {
                return createSortPopup(anchorView);
            }
            return null;
        });
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

    protected abstract RecyclerView getStarRecyclerView();

    protected abstract void showTags(List<Tag> tags);

    protected abstract void focusOnTag(Integer position);

    protected abstract void goToClassicPage();

    protected abstract void goToStarPage(long starId);

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
            getStarRecyclerView().setAdapter(starAdapter);
        }
        else {
            starAdapter.setList(list);
            starAdapter.notifyDataSetChanged();
        }
    }
}
