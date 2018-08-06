package com.king.app.gdb.data;

/**
 * 描述: 控制查询record的条件
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/23 10:48
 */
public class RecordCursor {

    /**
     * 从record表中的偏移量
     */
    public int offset;
    /**
     * 总共查询的数量
     */
    public int number;


    /**
     * 关键词对应的最小值
     * 不限制则为-1
     */
    public int min = -1;
    /**
     * 关键词对应的最大值
     * 不限制则为-1
     */
    public int max = -1;
}
