package com.hackerrank.github.controller;

import com.hackerrank.github.exceptions.ActorNotFoundException;
import com.hackerrank.github.exceptions.EventAlreadyExistsException;
import com.hackerrank.github.model.entities.Event;
import com.hackerrank.github.repository.ActorRepository;
import com.hackerrank.github.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
public class EventController {

    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ActorRepository actorRepository;

    //@DeleteMapping("/events") should be more appropriate
    @DeleteMapping("/erase")
    public void eraseEvents(){
        this.eventRepository.deleteAllInBatch();
    }


    //Note -> user and repo could be updated using this method!
    @PostMapping("/events")
    public ResponseEntity createEvent(@RequestBody @Valid Event event ){

        if(this.eventRepository.findOne(event.getId()) != null){
            LOG.error("The resource id: {} already exists", event.getId());
            throw new EventAlreadyExistsException();
        }

        this.eventRepository.save(event);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/events")
    public List<Event> findAllEvents(){
        return this.eventRepository.findAllByOrderByIdAsc();
    }

    @GetMapping("/events/actors/{actorID}")
    public List<Event> findEventsByActorId(@PathVariable Long actorID){

        if( this.actorRepository.findOne(actorID) == null) {
            LOG.error("Actor id: {} not found", actorID);
            throw  new ActorNotFoundException();
        }

        return eventRepository.findByActorIdOrderByIdAsc(actorID);
    }


}
