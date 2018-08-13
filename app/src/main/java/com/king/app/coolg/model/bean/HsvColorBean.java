package com.king.app.coolg.model.bean;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/23 9:52
 */
public class HsvColorBean {

    private int hStart = -1;
    private int hArg = -1;
    private float s = -1;
    private float v = -1;
    /**
     * 0: 随机
     * 1: 配合白色文字的背景颜色
     * 2: 配合深色文字的背景颜色
     */
    private int type;

    public int gethStart() {
        return hStart;
    }

    public void sethStart(int hStart) {
        this.hStart = hStart;
    }

    public int gethArg() {
        return hArg;
    }

    public void sethArg(int hArg) {
        this.hArg = hArg;
    }

    public float getS() {
        return s;
    }

    public void setS(float s) {
        this.s = s;
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
