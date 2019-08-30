package com.king.app.coolg.phone.record.scene;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentRecordSceneBinding;
import com.king.app.coolg.model.bean.HsvColorBean;
import com.king.app.coolg.model.setting.SettingProperty;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/13 9:59
 */
public class SceneFragment extends MvvmFragment<FragmentRecordSceneBinding, BaseViewModel> {

    private SceneAdapter adapter;

    private SceneVerAdapter verAdapter;

    private SceneViewModel mModel;

    private String mScene;

    public OnSceneSelectedListener onSceneSelectedListener;
    private int mRecordType;

    private static final String ARG_VER = "arg_ver";

    public static SceneFragment newInstance(boolean isVerList) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_VER, isVerList);
        SceneFragment fragment = new SceneFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_record_scene;
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void onCreate(View view) {
        mModel = ViewModelProviders.of(this).get(SceneViewModel.class);
        if (isVerList()) {
            mBinding.rvScenes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        }
        else {
            mBinding.rvScenes.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        }
    }

    @Override
    protected void onCreateData() {
        mModel.sceneObserver.observe(this, list -> showScenes(list));
        mModel.focusSceneObserver.observe(this, scene -> focusToScene(scene));
        mModel.setRecordType(mRecordType);
        mModel.loadScenes();
    }

    private boolean isVerList() {
        return getArguments().getBoolean(ARG_VER);
    }

    public void setScene(String mScene) {
        this.mScene = mScene;
    }

    public void setOnSceneSelectedListener(OnSceneSelectedListener onSceneSelectedListener) {
        this.onSceneSelectedListener = onSceneSelectedListener;
    }

    private void showScenes(List<SceneBean> list) {
        if (isVerList()) {
            if (verAdapter == null) {
                verAdapter = new SceneVerAdapter();
                verAdapter.setList(list);
                verAdapter.setOnItemClickListener((view, position, data) -> onSelectListScene(data));
                mBinding.rvScenes.setAdapter(verAdapter);
            }
            else {
                verAdapter.setList(list);
                verAdapter.notifyDataSetChanged();
            }
        }
        else {
            if (adapter == null) {
                adapter = new SceneAdapter();
                adapter.setList(list);
                adapter.setFocusScene(mScene);
                adapter.setOnItemClickListener((view, position, data) -> onSelectGridScene(data));
                mBinding.rvScenes.setAdapter(adapter);
            }
            else {
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void onSelectGridScene(SceneBean data) {
        mScene = data.getScene();
        adapter.setFocusScene(mScene);
        adapter.notifyDataSetChanged();
        if (onSceneSelectedListener != null) {
            onSceneSelectedListener.onSelectScene(mScene);
        }
    }

    private void onSelectListScene(SceneBean data) {
        mScene = data.getScene();
        verAdapter.notifyDataSetChanged();
        if (onSceneSelectedListener != null) {
            onSceneSelectedListener.onSelectScene(mScene);
        }
    }

    public void sortScene(int sortType) {
        mModel.sort(sortType);
    }

    public void onColorChanged() {
        mModel.loadScenes();
    }

    public void updateTempColor(HsvColorBean hsvColorBean) {
        mModel.updateTempColor(hsvColorBean);
    }

    public void focusToScene(String scene) {
        int position = mModel.setFocusScene(scene);
        if (isVerList()) {
            if (verAdapter != null) {
                verAdapter.setSelection(position);
                verAdapter.notifyDataSetChanged();
            }
        }
        else {
            if (adapter != null) {
                adapter.setFocusScene(scene);
                adapter.notifyDataSetChanged();
            }
        }
        scrollToPosition(position);
    }

    private void scrollToPosition(int position) {
        if (isVerList()) {
            LinearLayoutManager manager = (LinearLayoutManager) mBinding.rvScenes.getLayoutManager();
            manager.scrollToPositionWithOffset(position, 0);
        }
        else {
            GridLayoutManager manager = (GridLayoutManager) mBinding.rvScenes.getLayoutManager();
            manager.scrollToPositionWithOffset(position, 0);
        }
    }

    public void setRecordType(int recordType) {
        this.mRecordType = recordType;
    }

    public interface OnSceneSelectedListener {
        void onSelectScene(String scene);
    }

    public void reload() {
        if (mModel != null) {
            mModel.loadScenes();
        }
    }

    public void onSortChanged(String initScene) {
        if (mModel != null) {
            mModel.loadSortType();
            mModel.loadScenes(initScene);
        }
    }

    public String getFocusScene() {
        return mScene;
    }
}
