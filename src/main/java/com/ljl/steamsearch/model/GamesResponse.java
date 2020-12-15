package com.ljl.steamsearch.model;

public class GamesResponse {
    private String results_html;
    private int start;
    private int success;
    private int total_count;

    public String getResults_html() {
        return results_html;
    }

    public void setResults_html(String results_html) {
        this.results_html = results_html;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }
}
