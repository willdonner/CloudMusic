package com.dongxun.lichunkai.cloudmusic.Bean;

/**
 * 评论类
 */
public class Comment {
    private User user;//评论用户
    private String commentId;//评论id
    private String content;//评论内容
    private String time;//评论时间
    private String likedCount;//点赞数

    private Boolean showHot;//显示热评标题
    private Boolean showAllHot;//显示所有热评按钮
    private Boolean showNew;//显示最新评论标题

    public Boolean getShowHot() {
        return showHot;
    }

    public void setShowHot(Boolean showHot) {
        this.showHot = showHot;
    }

    public Boolean getShowAllHot() {
        return showAllHot;
    }

    public void setShowAllHot(Boolean showAllHot) {
        this.showAllHot = showAllHot;
    }

    public Boolean getShowNew() {
        return showNew;
    }

    public void setShowNew(Boolean showNew) {
        this.showNew = showNew;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(String likedCount) {
        this.likedCount = likedCount;
    }
}
