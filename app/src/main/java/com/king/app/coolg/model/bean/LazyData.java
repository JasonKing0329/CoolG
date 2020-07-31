package com.king.app.coolg.model.bean;

import java.util.List;

/**
 * Desc: 延迟加载list的详细数据，适用于先将全部list加载处理供界面展示，耗时的内部细节通过延迟加载通知刷新
 *
 * @author：Jing Yang
 * @date: 2020/7/31 17:03
 */
public class LazyData<T> {
    public int start;
    public int count;
    public List<T> list;

    public LazyData(int start, int count, List<T> list) {
        this.start = start;
        this.count = count;
        this.list = list;
    }
}
