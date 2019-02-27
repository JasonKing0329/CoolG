package com.king.app.coolg.phone.video.home;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.app.coolg.BR;

public class RecommendBean extends BaseObservable {

    private String sql;

    private boolean withFkType;

    private boolean isFkType1;

    private boolean isFkType2;

    private boolean isFkType3;

    private boolean isFkType4;

    private boolean isFkType5;

    private boolean isFkType6;

    private boolean isTypeAll;

    private boolean isType1v1;

    private boolean isType3w;

    private boolean isTypeMulti;

    private boolean isTypeTogether;

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

    @Bindable
    public boolean isWithFkType() {
        return withFkType;
    }

    public void setWithFkType(boolean withFkType) {
        this.withFkType = withFkType;
        notifyPropertyChanged(BR.withFkType);
    }

    public boolean isFkType1() {
        return isFkType1;
    }

    public void setFkType1(boolean fkType1) {
        isFkType1 = fkType1;
    }

    public boolean isFkType2() {
        return isFkType2;
    }

    public void setFkType2(boolean fkType2) {
        isFkType2 = fkType2;
    }

    public boolean isFkType3() {
        return isFkType3;
    }

    public void setFkType3(boolean fkType3) {
        isFkType3 = fkType3;
    }

    public boolean isFkType4() {
        return isFkType4;
    }

    public void setFkType4(boolean fkType4) {
        isFkType4 = fkType4;
    }

    public boolean isFkType5() {
        return isFkType5;
    }

    public void setFkType5(boolean fkType5) {
        isFkType5 = fkType5;
    }

    public boolean isFkType6() {
        return isFkType6;
    }

    public void setFkType6(boolean fkType6) {
        isFkType6 = fkType6;
    }
}
