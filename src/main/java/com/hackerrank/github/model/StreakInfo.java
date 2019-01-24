package com.hackerrank.github.model;

import java.util.Date;

public class StreakInfo {

    private Long id;
    private String login;
    private Long count;
    private Date created_at;

    public StreakInfo(Long id, String login, Long count, Date created_at) {
        this.id = id;
        this.login = login;
        this.count = count;
        this.created_at = created_at;
    }

    public StreakInfo(){}


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

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
