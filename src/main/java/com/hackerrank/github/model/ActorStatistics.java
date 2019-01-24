package com.hackerrank.github.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ActorStatistics {

    private Long id;
    private String login;

    @JsonIgnore
    private Long count;

    @JsonProperty("avatar_url")
    private String avatar;

    @JsonIgnore
    private Date lastEventDate;

    public ActorStatistics(Long id, String login, Long count, String avatar, Date lastEventDate) {
        this.id = id;
        this.login = login;
        this.count = count;
        this.avatar = avatar;
        this.lastEventDate = lastEventDate;
    }

    @Override
    public String toString() {
        return "ActorStatistics{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", count=" + count +
                ", avatar='" + avatar + '\'' +
                ", lastEventDate=" + lastEventDate +
                '}';
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getLastEventDate() {
        return lastEventDate;
    }

    public void setLastEventDate(Date lastEventDate) {
        this.lastEventDate = lastEventDate;
    }
}
