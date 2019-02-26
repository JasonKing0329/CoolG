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
