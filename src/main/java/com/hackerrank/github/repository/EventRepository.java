package com.hackerrank.github.repository;

import com.hackerrank.github.model.ActorStatistics;
import com.hackerrank.github.model.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrderByIdAsc();

    List<Event> findByActorIdOrderByIdAsc(Long actorID);

    @Query("SELECT " +
            "    new com.hackerrank.github.model.ActorStatistics(" +
            "            e.actor.id, e.actor.login, COUNT(e), e.actor.avatar, max(e.createdAt) " +
            "    ) " +
            "FROM " +
            "    Event e " +
            "GROUP BY " +
            "    e.actor.login")
    List<ActorStatistics> findActorStatistics();
}
