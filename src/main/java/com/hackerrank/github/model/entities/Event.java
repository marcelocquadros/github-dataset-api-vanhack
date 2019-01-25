package com.hackerrank.github.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Event {

    @Id
    private Long id;

    @NotNull
    private String type;

    @NotNull
    @ManyToOne( cascade = CascadeType.MERGE)
    private Actor actor;

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    private Repo repo;

    @NotNull
    @JsonProperty("created_at")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;

    public Event() {
    }

    public Event(Long id, String type, Actor actor, Repo repo, Timestamp createdAt) {
        this.id = id;
        this.type = type;
        this.actor = actor;
        this.repo = repo;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Repo getRepo() {
        return repo;
    }

    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) &&
                Objects.equals(type, event.type) &&
                Objects.equals(actor, event.actor) &&
                Objects.equals(repo, event.repo) &&
                Objects.equals(createdAt, event.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, actor, repo, createdAt);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", actor=" + actor +
                ", repo=" + repo +
                ", createdAt=" + createdAt +
                '}';
    }
}
