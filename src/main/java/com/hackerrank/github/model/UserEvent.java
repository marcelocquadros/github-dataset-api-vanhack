package com.hackerrank.github.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class UserEvent {

    private Long id;
    private String login;
    @JsonProperty("avatar_url")
    private String avatar;
    @JsonIgnore
    private Date createdAt;
    @JsonIgnore
    private Long count;

    public UserEvent(){

    }
    public UserEvent(Long id, String login, String avatar, Date createdAt) {
        this.id = id;
        this.login = login;
        this.avatar = avatar;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", avatar='" + avatar + '\'' +
                ", createdAt=" + createdAt +
                ", count=" + count +
                '}';
    }
}
