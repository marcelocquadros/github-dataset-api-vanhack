package com.hackerrank.github.model;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ActorStatistics {

    private Actor actor;

    private Long count;

    private Date lastEventDate;

    public ActorStatistics(Actor actor, Long count, Date lastEventDate) {
        this.count = count;
        this.lastEventDate = lastEventDate;
        this.actor = actor;
    }

    public static List<Actor> sort(List<ActorStatistics> actorStatistics) {

        List<Actor> result = actorStatistics.stream()
                .sorted(Comparator.comparing(ActorStatistics::getCount).reversed())
                .collect(Collectors.toList())
                .stream()
                .sorted((e1, e2) -> {

                    if(e1.getCount() == e2.getCount()){
                        return e2.getLastEventDate().compareTo(e1.getLastEventDate());
                    }
                    return 0;
                })
                .collect(Collectors.toList())
                .stream()
                .sorted((e1, e2) -> {

                    if ((e2.getCount() == e1.getCount())
                            && e1.getLastEventDate().equals(e2.getLastEventDate())) {

                        e1.getActor().getLogin().compareTo(e2.getActor().getLogin());

                    }
                    return 0;

                })
                .map(e -> e.getActor())
                .collect(Collectors.toList());

        return result;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Date getLastEventDate() {
        return lastEventDate;
    }

    public void setLastEventDate(Date lastEventDate) {
        this.lastEventDate = lastEventDate;
    }
}
