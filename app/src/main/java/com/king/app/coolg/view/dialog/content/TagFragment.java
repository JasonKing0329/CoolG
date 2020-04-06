package com.king.app.coolg.view.dialog.content;

import android.content.DialogInterface;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.adapter.BaseTagAdapter;
import com.king.app.coolg.databinding.FragmentTagBinding;
import com.king.app.coolg.model.repository.TagRepository;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.view.dialog.DraggableContentFragment;
import com.king.app.coolg.view.dialog.SimpleDialogs;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.entity.TagRecordDao;
import com.king.app.gdb.data.entity.TagStarDao;
import com.king.app.gdb.data.param.DataConstants;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TagFragment extends DraggableContentFragment<FragmentTagBinding> {

    private BaseTagAdapter<Tag> adapter;
    private List<Tag> tagList;

    private int tagType;
    private TagRepository repository;

    private OnTagSelectListener onTagSelectListener;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    public void setOnTagSelectListener(OnTagSelectListener onTagSelectListener) {
        this.onTagSelectListener = onTagSelectListener;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_tag;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    @Override
    protected void initView() {

        repository = new TagRepository();

        View view = getDialogHolder().inflateToolbar(R.layout.layout_toolbar_tag);
        view.findViewById(R.id.iv_add).setOnClickListener(v -> addTag());
        view.findViewById(R.id.iv_edit).setOnClickListener(v -> editMode());

        adapter = new BaseTagAdapter<Tag>() {
            @Override
            protected String getText(Tag data) {
                return data.getName();
            }

            @Override
            protected long getId(Tag data) {
                return data.getId();
            }

            @Override
            protected boolean isDisabled(Tag item) {
                return false;
            }
        };
        adapter.setOnItemSelectListener(new BaseTagAdapter.OnItemSelectListener<Tag>() {
            @Override
            public void onSelectItem(Tag data) {
                if (onTagSelectListener != null) {
                    onTagSelectListener.onSelectTag(data);
                    dismissAllowingStateLoss();
                }
            }

            @Override
            public void onUnSelectItem(Tag tag) {

            }
        });
        adapter.setOnItemLongClickListener(data -> preDelete(data));
        refreshTags();
    }

    private void editMode() {
    }

    private void preDelete(Tag data) {
        long count = 0;
        if (tagType == DataConstants.TAG_TYPE_STAR) {
            count = getTagStarDao().queryBuilder()
                    .where(TagStarDao.Properties.TagId.eq(data.getId()))
                    .buildCount().count();
        }
        else if (tagType == DataConstants.TAG_TYPE_RECORD){
            count = getTagRecordDao().queryBuilder()
                    .where(TagRecordDao.Properties.TagId.eq(data.getId()))
                    .buildCount().count();
        }
        String msg;
        DialogInterface.OnClickListener okListener;
        if (count > 0) {
            msg = "'" + data.getName() + "' is related to items, delete all related items too?";
            okListener = (dialog, which) -> {
                repository.deleteTagAndRelations(data);
                refreshTags();
                showMessageShort("success");
            };
        }
        else {
            msg = "'" + data.getName() + "' will be deleted by this operation, continue?";
            okListener = (dialog, which) -> {
                repository.deleteTag(data);
                refreshTags();
                showMessageShort("success");
            };
        }
        new SimpleDialogs().showConfirmCancelDialog(getContext(), msg
                , okListener, null);
    }

    private Observable<List<Tag>> loadTags() {
        tagList = repository.loadTags(tagType);
        return repository.sortTags(SettingProperty.getTagSortType(), tagList);
    }

    private TagRecordDao getTagRecordDao() {
        return CoolApplication.getInstance().getDaoSession().getTagRecordDao();
    }

    private TagStarDao getTagStarDao() {
        return CoolApplication.getInstance().getDaoSession().getTagStarDao();
    }

    private void addTag() {
        new SimpleDialogs().openInputDialog(getContext(), "Tag name", name -> {
            if (repository.addTag(name, tagType)) {
                refreshTags();
            }
        });
    }

    private void refreshTags() {
        loadTags()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Tag>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Tag> tagList) {
                        adapter.setData(tagList);
                        adapter.bindFlowLayout(mBinding.flowTags);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showMessageShort(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface OnTagSelectListener {
        void onSelectTag(Tag tag);
    }

}
