package com.king.lib.jindicator;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/1 15:14
 */
public class IndicatorNode {

    private String name;
    private String path;
    private int index;
    private int indexInContainer;
    private int left;
    private int width;

    public IndicatorNode() {
        left = -1;
        width = -1;
        index = -1;
        indexInContainer = -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndexInContainer() {
        return indexInContainer;
    }

    public void setIndexInContainer(int indexInContainer) {
        this.indexInContainer = indexInContainer;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
