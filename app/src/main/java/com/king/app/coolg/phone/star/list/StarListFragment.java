package com.king.app.coolg.phone.star.list;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chenenyu.router.Router;
import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.FragmentStarRichBinding;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.star.StarRatingDialog;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.widget.FitSideBar;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 14:52
 */
public class StarListFragment extends MvvmFragment<FragmentStarRichBinding, StarListViewModel>
    implements OnStarRatingListener {

    private static final String ARG_STAR_TYPE = "star_type";

    private StarCircleAdapter mCircleAdapter;
    private StarRichAdapter mRichAdapter;
    private IStarListHolder holder;

    public static StarListFragment newInstance(String type) {
        StarListFragment fragment = new StarListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_STAR_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IStarListHolder) holder;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_star_rich;
    }

    @Override
    protected StarListViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StarListViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        mBinding.rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 按音序排列，在滑动过程中显示当前的详细index
                if (mModel.getSortType() == AppConstants.STAR_SORT_NAME) {
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            updateDetailIndex();
                            break;
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            break;
                        default:
                            holder.hideDetailIndex();
                            break;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //在这里进行第二次滚动（最后的距离）
                if (needMove) {
                    needMove = false;
                    //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                    int n = nSelection - ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (n >= 0 && n < recyclerView.getChildCount()) {
                        recyclerView.scrollBy(0, recyclerView.getChildAt(n).getTop()); //滚动到顶部
                    }
                }

                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    updateDetailIndex();
                }
            }
        });

        mBinding.sidebar.setOnSidebarStatusListener(new FitSideBar.OnSidebarStatusListener() {
            @Override
            public void onChangeFinished() {
                mBinding.tvIndexPopup.setVisibility(View.GONE);
            }

            @Override
            public void onSideIndexChanged(String index) {
                int selection = mModel.getLetterPosition(index);
                scrollToPosition(selection);

                mBinding.tvIndexPopup.setText(index);
                mBinding.tvIndexPopup.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean needMove;
    private int nSelection;

    private void scrollToPosition(int selection) {
        nSelection = selection;
        final LinearLayoutManager manager = (LinearLayoutManager) mBinding.rvList.getLayoutManager();
        int fir = manager.findFirstVisibleItemPosition();
        int end = manager.findLastVisibleItemPosition();
        if (selection <= fir) {
            mBinding.rvList.scrollToPosition(selection);
        } else if (selection <= end) {
            int top = mBinding.rvList.getChildAt(selection - fir).getTop();
            mBinding.rvList.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            mBinding.rvList.scrollToPosition(selection);
            //记录当前需要在RecyclerView滚动监听里面继续第二次滚动
            needMove = true;
        }
    }

    private void updateDetailIndex() {
        int position = -1;
        RecyclerView.LayoutManager manager = mBinding.rvList.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager lm = (LinearLayoutManager) manager;
            position = lm.findFirstVisibleItemPosition();
        } else if (manager instanceof GridLayoutManager) {
            GridLayoutManager gm = (GridLayoutManager) manager;
            position = gm.findFirstVisibleItemPosition();
        }
        String name = null;
        if (position != -1) {
            name = mModel.getDetailIndex(position);
        }
        if (!TextUtils.isEmpty(name)) {
            holder.updateDetailIndex(name);
        }
    }

    @Override
    protected void onCreateData() {

        mModel.indexObserver.observe(this, index -> mBinding.sidebar.addIndex(index));
        mModel.indexBarObserver.observe(this, result -> {
            mBinding.sidebar.build();
            mBinding.sidebar.setVisibility(View.VISIBLE);
        });
        mModel.circleListObserver.observe(this, list -> showCircleList(list));
        mModel.richListObserver.observe(this, list -> showRichList(list));
        mModel.circleUpdateObserver.observe(this, result -> {
            if (mCircleAdapter != null) {
                mCircleAdapter.notifyDataSetChanged();
            }
        });
        mModel.richListObserver.observe(this, list -> showRichList(list));
        mModel.richUpdateObserver.observe(this, result -> {
            if (mRichAdapter != null) {
                mRichAdapter.notifyDataSetChanged();
            }
        });

        DebugLog.e(getArguments().getString(ARG_STAR_TYPE));
        mBinding.sidebar.clear();
        mModel.setStarType(getArguments().getString(ARG_STAR_TYPE));
        mModel.loadStarList();
    }

    private RecyclerView.ItemDecoration richDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = ScreenUtils.dp2px(10);
        }
    };

    private void showRichList(List<StarProxy> list) {
        mBinding.rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.rvList.removeItemDecoration(richDecoration);
        mBinding.rvList.addItemDecoration(richDecoration);

        mRichAdapter = new StarRichAdapter();
        mRichAdapter.setList(list);
        mRichAdapter.setExpandMap(mModel.getExpandMap());
        mRichAdapter.setOnStarRatingListener(this);
        mRichAdapter.setOnItemClickListener((view, position, data) -> onStarClick(data));
        mBinding.rvList.setAdapter(mRichAdapter);
    }

    private void showCircleList(List<StarProxy> list) {
        mBinding.rvList.removeItemDecoration(richDecoration);
        int column = 2;
        mBinding.rvList.setLayoutManager(new GridLayoutManager(getActivity(), column));

        mCircleAdapter = new StarCircleAdapter();
        mCircleAdapter.setList(list);
        mCircleAdapter.setOnStarRatingListener(this);
        mCircleAdapter.setOnItemClickListener((view, position, data) -> onStarClick(data));
        mCircleAdapter.setOnItemLongClickListener((view, position, data) -> onStarLongClick(data));
        mBinding.rvList.setAdapter(mCircleAdapter);
    }

    public void onStarClick(StarProxy star) {
        if (holder != null && holder.dispatchClickStar(star.getStar())) {
            return;
        }
        Router.build("StarPhone")
                .with(StarActivity.EXTRA_STAR_ID, star.getStar().getId())
                .go(this);
    }

    public void onStarLongClick(StarProxy star) {

    }

    @Override
    public void onUpdateRating(Long starId) {
        StarRatingDialog dialog = new StarRatingDialog();
        dialog.setStarId(starId);
        dialog.setOnDismissListener(dialog1 -> {
            if (mCircleAdapter != null) {
                mCircleAdapter.notifyStarChanged(starId);
            }
            if (mRichAdapter != null) {
                mRichAdapter.notifyStarChanged(starId);
            }
        });
        dialog.show(getChildFragmentManager(), "StarRatingDialog");
    }

    public void onViewModeChanged() {
        if (mModel != null && !mModel.isLoading()) {
            DebugLog.e(getArguments().getString(ARG_STAR_TYPE));
            mBinding.sidebar.clear();
            mModel.loadStarList();
        }
    }

    public void setExpandAll(boolean expand) {
        if (mModel != null) {
            mModel.setExpandAll(expand);
            if (mCircleAdapter != null) {
                mCircleAdapter.notifyDataSetChanged();
            }
            if (mRichAdapter != null) {
                mRichAdapter.notifyDataSetChanged();
            }
        }
    }

    public void updateSortType(int sortMode) {
        mBinding.sidebar.clear();
        mModel.sortStarList(sortMode);
    }

    public void toggleSidebar() {
        mBinding.sidebar.setVisibility(mBinding.sidebar.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    public void filterStar(String text) {
        if (mModel.isKeywordChanged(text)) {
            mBinding.sidebar.clear();
            mModel.filter(text);
        }
    }

    public void onRefresh(int sortType) {
        if (mModel != null && !mModel.isLoading()) {
            DebugLog.e(getArguments().getString(ARG_STAR_TYPE) + " --> sortType" + sortType);
            mModel.setSortType(sortType);
            mBinding.sidebar.clear();
            mModel.loadStarList();
        }
    }

    @Override
    public void onResume() {
        DebugLog.e(getArguments().getString(ARG_STAR_TYPE));
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        DebugLog.e(getArguments().getString(ARG_STAR_TYPE) + " --> hidden" + hidden);
        super.onHiddenChanged(hidden);
    }

    public boolean isNotScrolling() {
        return mBinding.rvList.getScrollState() == RecyclerView.SCROLL_STATE_IDLE;
    }

    public void goTop() {
        mBinding.rvList.scrollToPosition(0);
    }
}
