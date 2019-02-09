package com.king.app.coolg.phone.star.category;

import com.king.app.gdb.data.entity.TopStar;

public class CategoryStar {

    private TopStar star;

    private String url;

    private String name;

    private SelectObserver<CategoryStar> observer;

    public TopStar getStar() {
        return star;
    }

    public void setStar(TopStar star) {
        this.star = star;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SelectObserver<CategoryStar> getObserver() {
        return observer;
    }

    public void setObserver(SelectObserver<CategoryStar> observer) {
        this.observer = observer;
    }
}
