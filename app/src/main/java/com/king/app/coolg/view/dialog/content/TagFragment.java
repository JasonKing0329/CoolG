package com.king.app.coolg.view.dialog.content;

import android.content.DialogInterface;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.adapter.BaseTagAdapter;
import com.king.app.coolg.databinding.FragmentTagBinding;
import com.king.app.coolg.view.dialog.DraggableContentFragment;
import com.king.app.coolg.view.dialog.SimpleDialogs;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.entity.TagDao;
import com.king.app.gdb.data.entity.TagRecordDao;
import com.king.app.gdb.data.entity.TagStarDao;
import com.king.app.gdb.data.param.DataConstants;

import java.util.List;

public class TagFragment extends DraggableContentFragment<FragmentTagBinding> {

    private BaseTagAdapter<Tag> adapter;
    private List<Tag> tagList;

    private int tagType;

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
                deleteTagAndRelations(data);
                refreshTags();
                showMessageShort("success");
            };
        }
        else {
            msg = "'" + data.getName() + "' will be deleted by this operation, continue?";
            okListener = (dialog, which) -> {
                deleteTag(data);
                refreshTags();
                showMessageShort("success");
            };
        }
        new SimpleDialogs().showConfirmCancelDialog(getContext(), msg
                , okListener, null);
    }

    private void deleteTagAndRelations(Tag data) {
        // delete items
        if (data.getType() == DataConstants.TAG_TYPE_RECORD) {
            getTagRecordDao().queryBuilder()
                    .where(TagRecordDao.Properties.TagId.eq(data.getId()))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getTagRecordDao().detachAll();
            
        }
        else if (data.getType() == DataConstants.TAG_TYPE_STAR) {
            getTagStarDao().queryBuilder()
                    .where(TagStarDao.Properties.TagId.eq(data.getId()))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getTagStarDao().detachAll();
        }
        // delete tag
        deleteTag(data);
    }

    private void deleteTag(Tag data) {
        getTagDao().delete(data);
        getTagDao().detachAll();
    }

    private void loadTags() {
        tagList = getTagDao().queryBuilder().where(TagDao.Properties.Type.eq(tagType)).build().list();
    }

    private TagDao getTagDao() {
        return CoolApplication.getInstance().getDaoSession().getTagDao();
    }

    private TagRecordDao getTagRecordDao() {
        return CoolApplication.getInstance().getDaoSession().getTagRecordDao();
    }

    private TagStarDao getTagStarDao() {
        return CoolApplication.getInstance().getDaoSession().getTagStarDao();
    }

    private void addTag() {
        new SimpleDialogs().openInputDialog(getContext(), "Tag name", name -> {
            long count = getTagDao().queryBuilder().where(TagDao.Properties.Name.eq(name)).buildCount().count();
            if (count == 0) {
                Tag tag = new Tag();
                tag.setName(name);
                tag.setType(tagType);
                getTagDao().insert(tag);
                getTagDao().detachAll();

                refreshTags();
            }
        });
    }

    private void refreshTags() {
        loadTags();
        adapter.setData(tagList);
        adapter.bindFlowLayout(mBinding.flowTags);
    }

    public interface OnTagSelectListener {
        void onSelectTag(Tag tag);
    }

}
