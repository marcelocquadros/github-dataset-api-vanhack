package com.hackerrank.github.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class Actor {

    @Id
    private Long id;

    @NotNull
    @Column(unique = true)
    private String login;

    @JsonProperty("avatar_url")
    private String avatar;

    public Actor() {
    }

    public Actor(Long id, String login, String avatar) {
        this.id = id;
        this.login = login;
        this.avatar = avatar;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Actor actor = (Actor) o;
        return Objects.equals(id, actor.id) &&
                Objects.equals(login, actor.login) &&
                Objects.equals(avatar, actor.avatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, avatar);
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
