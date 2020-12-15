package com.ljl.steamsearch.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Integer id;
    private String gameName;
    private String gameId;
    private int approveCount;
    private String postDate;

    @Column(length = 3000)
    private String content;
    private String publisher;
    private int publisherCommentCount;
    private int publisherGamesCount;

    public Comment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getApproveCount() {
        return approveCount;
    }

    public void setApproveCount(int approveCount) {
        this.approveCount = approveCount;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublisherCommentCount() {
        return publisherCommentCount;
    }

    public void setPublisherCommentCount(int publisherCommentCount) {
        this.publisherCommentCount = publisherCommentCount;
    }

    public int getPublisherGamesCount() {
        return publisherGamesCount;
    }

    public void setPublisherGamesCount(int publisherGamesCount) {
        this.publisherGamesCount = publisherGamesCount;
    }
}
