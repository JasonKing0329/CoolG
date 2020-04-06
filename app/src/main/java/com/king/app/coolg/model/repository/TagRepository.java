package com.king.app.coolg.model.repository;

import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.utils.PinyinUtil;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.entity.TagDao;
import com.king.app.gdb.data.entity.TagRecordDao;
import com.king.app.gdb.data.entity.TagStarDao;
import com.king.app.gdb.data.param.DataConstants;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;

/**
 * @description:
 * @author：Jing
 * @date: 2020/4/6 20:47
 */
public class TagRepository extends BaseRepository {

    public List<Tag> loadTags(int type) {
        return getDaoSession().getTagDao().queryBuilder().where(TagDao.Properties.Type.eq(type)).build().list();
    }

    public Observable<List<Tag>> sortTags(int sortType, List<Tag> list) {
        return Observable.create(e -> {
            if (sortType == AppConstants.TAG_SORT_NAME) {
                Collections.sort(list, new TagNameComparator());
            }
            else if (sortType == AppConstants.TAG_SORT_RANDOM) {
                Collections.shuffle(list);
            }
            e.onNext(list);
            e.onComplete();
        });
    }

    public void deleteTagAndRelations(Tag data) {
        // delete items
        if (data.getType() == DataConstants.TAG_TYPE_RECORD) {
            getDaoSession().getTagRecordDao().queryBuilder()
                    .where(TagRecordDao.Properties.TagId.eq(data.getId()))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTagRecordDao().detachAll();

        }
        else if (data.getType() == DataConstants.TAG_TYPE_STAR) {
            getDaoSession().getTagStarDao().queryBuilder()
                    .where(TagStarDao.Properties.TagId.eq(data.getId()))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTagStarDao().detachAll();
        }
        // delete tag
        deleteTag(data);
    }

    public void deleteTag(Tag data) {
        getDaoSession().getTagDao().delete(data);
        getDaoSession().getTagDao().detachAll();
    }

    public boolean addTag(String name, int tagType) {
        long count = getDaoSession().getTagDao().queryBuilder()
                .where(TagDao.Properties.Name.eq(name))
                .where(TagDao.Properties.Type.eq(tagType))
                .buildCount().count();
        if (count == 0) {
            Tag tag = new Tag();
            tag.setName(name);
            tag.setType(tagType);
            getDaoSession().getTagDao().insert(tag);
            getDaoSession().getTagDao().detachAll();
            return true;
        }
        return false;
    }

    private class TagNameComparator implements Comparator<Tag> {

        @Override
        public int compare(Tag lhs, Tag rhs) {
            return PinyinUtil.getPinyin(lhs.getName()).compareTo(PinyinUtil.getPinyin(rhs.getName()));
        }
    }

}
