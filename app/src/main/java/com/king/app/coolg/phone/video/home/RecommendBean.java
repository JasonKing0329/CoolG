package com.king.app.coolg.phone.video.home;

import android.databinding.BaseObservable;

public class RecommendBean extends BaseObservable {

    private String sql;

    private String sql1v1;

    private String sql3w;

    private int number;

    private boolean isTypeAll = true;

    private boolean isType1v1;

    private boolean isType3w;

    private boolean isTypeMulti;

    private boolean isTypeTogether;

    private boolean isOnline;

    public boolean isTypeAll() {
        return isTypeAll;
    }

    public void setTypeAll(boolean typeAll) {
        isTypeAll = typeAll;
    }

    public boolean isType1v1() {
        return isType1v1;
    }

    public void setType1v1(boolean type1v1) {
        isType1v1 = type1v1;
    }

    public boolean isType3w() {
        return isType3w;
    }

    public void setType3w(boolean type3w) {
        isType3w = type3w;
    }

    public boolean isTypeMulti() {
        return isTypeMulti;
    }

    public void setTypeMulti(boolean typeMulti) {
        isTypeMulti = typeMulti;
    }

    public boolean isTypeTogether() {
        return isTypeTogether;
    }

    public void setTypeTogether(boolean typeTogether) {
        isTypeTogether = typeTogether;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSql1v1() {
        return sql1v1;
    }

    public void setSql1v1(String sql1v1) {
        this.sql1v1 = sql1v1;
    }

    public String getSql3w() {
        return sql3w;
    }

    public void setSql3w(String sql3w) {
        this.sql3w = sql3w;
    }

    public boolean isOnlyType1v1() {
        return isType1v1 && !isTypeAll && !isTypeMulti && !isType3w && !isTypeTogether;
    }

    public boolean isOnlyType3w() {
        return isType3w && !isTypeAll && !isTypeMulti && !isType1v1 && !isTypeTogether;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
