package com.hackerrank.github.model;

import java.util.Date;

public class ActorEvent {

    private Actor actor;

    private Date createdAt;

    private Long count = 0L;

    public ActorEvent(Actor actor, Long id) {

        this.actor = actor;
        this.createdAt = createdAt;
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

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }
}
