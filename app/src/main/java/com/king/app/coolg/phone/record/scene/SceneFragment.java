package com.king.app.coolg.phone.record.scene;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.GridLayoutManager;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentRecordSceneBinding;
import com.king.app.coolg.model.bean.HsvColorBean;
import com.king.app.coolg.view.dialog.DraggableContentFragment;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/13 9:59
 */
public class SceneFragment extends DraggableContentFragment<FragmentRecordSceneBinding> {

    private SceneAdapter adapter;

    private SceneViewModel mModel;

    private String mScene;

    public OnSceneSelectedListener onSceneSelectedListener;
    private int mRecordType;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_record_scene;
    }

    @Override
    protected void initView() {

        mModel = ViewModelProviders.of(this).get(SceneViewModel.class);

        mBinding.rvScenes.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        mModel.sceneObserver.observe(this, list -> showScenes(list));
        mModel.setRecordType(mRecordType);
        mModel.loadScenes();
    }

    public void setScene(String mScene) {
        this.mScene = mScene;
    }

    public void setOnSceneSelectedListener(OnSceneSelectedListener onSceneSelectedListener) {
        this.onSceneSelectedListener = onSceneSelectedListener;
    }

    private void showScenes(List<SceneBean> list) {
        if (adapter == null) {
            adapter = new SceneAdapter();
            adapter.setList(list);
            adapter.setFocusScene(mScene);
            adapter.setOnItemClickListener((view, position, data) -> onSelectScene(data));
            mBinding.rvScenes.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

    private void onSelectScene(SceneBean data) {
        mScene = data.getScene();
        adapter.setFocusScene(mScene);
        adapter.notifyDataSetChanged();
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
        if (adapter != null) {
            adapter.setFocusScene(scene);
            adapter.notifyDataSetChanged();
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
}
