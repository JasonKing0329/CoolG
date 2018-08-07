package com.king.app.coolg.model.http.bean.response;

import com.king.app.gdb.data.entity.StarRating;

import java.util.List;

public class GetStarRatingResponse {

    private long starId;

    private List<StarRating> ratingList;

    public long getStarId() {
        return starId;
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    public List<StarRating> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<StarRating> ratingList) {
        this.ratingList = ratingList;
    }
}
