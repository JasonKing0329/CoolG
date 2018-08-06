package com.king.app.coolg.phone.home;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.ImageView;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityHomeBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 15:14
 */
@Route("Home")
public class HomeActivity extends MvvmActivity<ActivityHomeBinding, HomeViewModel>
    implements NavigationView.OnNavigationItemSelectedListener{

    private ImageView navHeaderView;
    private ImageView ivFolder;
    private ImageView ivFace;

    private HomeAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        mBinding.navView.setNavigationItemSelectedListener(this);
        mBinding.navView.setItemIconTintList(null);
        navHeaderView = mBinding.navView.getHeaderView(0).findViewById(R.id.nav_header_bg);
        ivFolder = mBinding.navView.getHeaderView(0).findViewById(R.id.iv_folder);
        ivFace = mBinding.navView.getHeaderView(0).findViewById(R.id.iv_face);

        mBinding.rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvItems.setEnableLoadMore(true);
        mBinding.rvItems.setOnLoadMoreListener(() -> mModel.loadMore());
    }

    @Override
    protected HomeViewModel createViewModel() {
        return ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    @Override
    protected void initData() {

        mModel.homeObserver.observe(this, bean -> {
            adapter = new HomeAdapter();
            adapter.setList(bean.getRecordList());
            mBinding.rvItems.setAdapter(adapter);
        });
        mModel.newRecordsObserver.observe(this, number -> {
            int start = adapter.getItemCount() - number - 1;
            adapter.notifyItemRangeInserted(start, number);
        });

        mModel.loadData();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
