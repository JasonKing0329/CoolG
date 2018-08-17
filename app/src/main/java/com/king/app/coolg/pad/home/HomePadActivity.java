package com.king.app.coolg.pad.home;

import android.app.ActivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityHomePadBinding;
import com.king.app.coolg.utils.GlideUtil;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 13:26
 */
@Route("HomePad")
public class HomePadActivity extends MvvmActivity<ActivityHomePadBinding, BaseViewModel> implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView navHeaderView;
    private ImageView ivFolder;
    private ImageView ivFace;

    private HomePadFragment homeFragment;

    @Override
    protected int getContentView() {
        return R.layout.activity_home_pad;
    }

    @Override
    protected void initView() {
        initDrawer();
        initContent();
    }
    
    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void initData() {

    }

    private void initContent() {
        homeFragment = new HomePadFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.group_ft_container, homeFragment, "HomePadFragment");
        ft.commit();
    }

    private void initDrawer() {
        mBinding.navView.setNavigationItemSelectedListener(this);
        mBinding.navView.setItemIconTintList(null);
        navHeaderView = mBinding.navView.getHeaderView(0).findViewById(R.id.nav_header_bg);
        ivFolder = mBinding.navView.getHeaderView(0).findViewById(R.id.iv_folder);
        ivFace = mBinding.navView.getHeaderView(0).findViewById(R.id.iv_face);
        ivFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SettingProperties.setGdbNavHeadRandom(true);
//                focusOnRandom();
            }
        });
        ivFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                selectImage();
            }
        });

//        if (SettingProperties.isGdbNavHeadRandom()) {
//            focusOnRandom();
//        }
//        else {
//            focusOnFolder();
//        }
    }

//    private void focusOnFolder() {
//        ivFolder.setSelected(true);
//        ivFace.setSelected(false);
//        String path = GdbImageProvider.getNavHeadImage();
//        Glide.with(this)
//                .load(path)
//                .apply(GlideUtil.getRecordOptions())
//                .into(navHeaderView);
//    }
//
//    private void focusOnRandom() {
//        ivFace.setSelected(true);
//        ivFolder.setSelected(false);
//        Glide.with(this)
//                .load(GdbImageProvider.getRandomNavHeadImage())
//                .apply(GlideUtil.getRecordOptions())
//                .into(navHeaderView);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_setting) {
//            ActivityManager.startSettingActivity(this);
//        }
//        else if (id == R.id.nav_exit) {
//            finish();
//        }
//        else if (id == R.id.nav_main) {
//            ActivityManager.startSurfLocalActivity(this);
//        }
//        else if (id == R.id.nav_swipe_star) {
//            ActivityManager.startStarSwipeActivity(this);
//        }
//        else if (id == R.id.nav_random) {
//            ActivityManager.startRandomActivity(this);
//        }
//        else if (id == R.id.nav_update) {
//            ActivityManager.startManageActivity(this);
//        }

        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
