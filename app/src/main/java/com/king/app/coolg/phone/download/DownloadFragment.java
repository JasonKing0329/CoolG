package com.king.app.coolg.phone.download;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentDownloadBinding;
import com.king.app.coolg.model.bean.DownloadDialogBean;
import com.king.app.coolg.model.http.bean.data.DownloadItem;
import com.king.app.coolg.view.dialog.DraggableContentFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 13:56
 */
public class DownloadFragment extends DraggableContentFragment<FragmentDownloadBinding>
    implements IContentHolder {

    private OnDownloadListener onDownloadListener;

    private PreviewFragment ftPreview;

    private ListFragment ftList;

    private DownloadDialogBean bean;

    private DownloadViewModel mModel;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_download;
    }

    @Override
    protected void initView() {
        mModel = ViewModelProviders.of(this).get(DownloadViewModel.class);

        mModel.setSavePath(bean.getSavePath());
        mModel.setOnDownloadListener(onDownloadListener);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.fl_ft, getContentViewFragment(), "Download")
                .commit();
    }

    public void setBean(DownloadDialogBean bean) {
        this.bean = bean;
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    protected Fragment getContentViewFragment() {

        if (bean.isShowPreview()) {
            ftPreview = new PreviewFragment();
            return ftPreview;
        }
        else {
            ftList = new ListFragment();
            return ftList;
        }
    }

    @Override
    public List<DownloadItem> getDownloadList() {
        return bean.getDownloadList();
    }

    @Override
    public List<DownloadItem> getExistedList() {
        return bean.getExistedList();
    }

    @Override
    public void dismissDialog() {
        dismissAllowingStateLoss();
    }

    @Override
    public void showListPage() {
        ftList = new ListFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fl_ft, ftList, "ListFragment")
                .commit();
    }

    @Override
    public void addDownloadItems(List<DownloadItem> checkedItems) {
        if (bean.getDownloadList() == null) {
            bean.setDownloadList(new ArrayList<>());
        }
        bean.getDownloadList().addAll(checkedItems);
    }

    @Override
    public DownloadViewModel getViewModel() {
        return mModel;
    }
}
