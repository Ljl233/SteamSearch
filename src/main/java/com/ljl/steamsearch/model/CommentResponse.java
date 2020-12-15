package com.ljl.steamsearch.model;

import java.util.List;

public class CommentResponse {
    private String cursor;
    private int dayrange;
    private int end_date;
    private String html;
    private String[] recommendationids;
    private String review_score;
    private int start_date;
    private int success;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public int getDayrange() {
        return dayrange;
    }

    public void setDayrange(int dayrange) {
        this.dayrange = dayrange;
    }

    public int getEnd_date() {
        return end_date;
    }

    public void setEnd_date(int end_date) {
        this.end_date = end_date;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String[] getRecommendationids() {
        return recommendationids;
    }

    public void setRecommendationids(String[] recommendationids) {
        this.recommendationids = recommendationids;
    }

    public String getReview_score() {
        return review_score;
    }

    public void setReview_score(String review_score) {
        this.review_score = review_score;
    }

    public int getStart_date() {
        return start_date;
    }

    public void setStart_date(int start_date) {
        this.start_date = start_date;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
