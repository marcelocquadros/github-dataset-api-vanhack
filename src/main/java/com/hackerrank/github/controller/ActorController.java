package com.hackerrank.github.controller;

import com.hackerrank.github.exceptions.ActorNotFoundException;
import com.hackerrank.github.model.entities.Actor;
import com.hackerrank.github.model.ActorStatistics;
import com.hackerrank.github.model.entities.Event;
import com.hackerrank.github.model.StreakInfo;
import com.hackerrank.github.repository.ActorRepository;
import com.hackerrank.github.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/actors")
public class ActorController {

    private static final Logger LOG = LoggerFactory.getLogger(ActorController.class);

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private EventRepository eventRepository;

    @PutMapping
    public ResponseEntity updateActorAvatar(@RequestBody @Valid Actor actor){

        Actor actorDB =  this.actorRepository.findOne(actor.getId());

        if(actorDB == null){
            LOG.error("Actor id {} not found", actor.getId());
            throw new ActorNotFoundException();
        }

        if(! actorDB.getLogin().equals(actor.getLogin())){
            LOG.error("Login cannot be updated");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        actorDB.setAvatar(actor.getAvatar());

        this.actorRepository.save(actor);

        return ResponseEntity.ok().build();
    }



    @GetMapping
    public List<Actor> listActorsOrderedByEventNumbers(){

        List<ActorStatistics> actorStatistics = eventRepository.findActorStatistics();

        List<Actor> sortedActors =  ActorStatistics.sort(actorStatistics);

        return sortedActors;
    }

    @GetMapping("/streak")
    public List<Actor> findActorStreak(){

        List<Event> events = this.eventRepository.findAll();

        //Events grouped by actor
        Map<Actor, List<Event>> userEventsMap =
                 events.stream()
                .collect(Collectors.groupingBy(Event::getActor));

        //Actor events sorted by date asc
        Map<String,List<Event>> userEventsSortedByDate = new HashMap<>();

        for (Map.Entry<Actor, List<Event>> entry : userEventsMap.entrySet()) {

            List<Event> listSortedEvents = entry.getValue()
                    .stream()
                    .sorted(Comparator.comparing(Event::getCreatedAt))
                    .collect(Collectors.toList());

            userEventsSortedByDate.put(entry.getKey().getLogin(), listSortedEvents);

        }

        // count consecutive days pushed events (Streak)
        Map<String, StreakInfo> userStreakInfo = StreakInfo.buildStreakInfo(userEventsSortedByDate);


        List<StreakInfo> sortedStreakInfo =  StreakInfo.sort(userStreakInfo);

        return sortedStreakInfo.stream()
                .map(s -> new Actor(s.getId(), s.getLogin(), s.getAvatar()))
                .collect(Collectors.toList());

    }

}
