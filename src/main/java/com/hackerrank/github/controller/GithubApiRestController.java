package com.hackerrank.github.controller;

import com.hackerrank.github.exceptions.ActorNotFoundException;
import com.hackerrank.github.model.Actor;
import com.hackerrank.github.model.ActorStatistics;
import com.hackerrank.github.model.Event;

import com.hackerrank.github.repository.ActorRepository;
import com.hackerrank.github.repository.EventRepository;
import com.hackerrank.github.repository.RepoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GithubApiRestController {

    private static final Logger LOG = LoggerFactory.getLogger(GithubApiRestController.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private RepoRepository repoRepository;


    @DeleteMapping("/erase")
    public void eraseEvents(){
        this.eventRepository.deleteAll();
    }

    @PostMapping("/events")
    public ResponseEntity createEvent(@RequestBody Event event ){
        //bug... file 00 and 05 using the same id and both expects 201 response
        if(this.eventRepository.findOne(event.getId()) != null){
            LOG.error("The resource id: {} already exists", event.getId());
            //throw new EventAlreadyExistsException();
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


    @PutMapping("/actors")
    public ResponseEntity updateActorAvatar(@RequestBody Actor actor){

       Actor actorDB =  this.actorRepository.findOne(actor.getId());
       if(actorDB == null){
           LOG.error("Actor id {} not found", actor.getId());
           throw new ActorNotFoundException();
       }

       if(actor.getLogin() != null){
           LOG.error("Login can not be updated");
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
       }

       actorDB.setAvatar(actor.getAvatar());

       this.actorRepository.save(actor);

       return ResponseEntity.ok().build();
    }

    @GetMapping("/actors")
    public List<ActorStatistics> listActorRecordsByNumberEvents(){

        List<ActorStatistics> eventStatistics = eventRepository.findActorStatistics();

       // eventStatistics.stream().forEach(System.out::println);

        eventStatistics =  eventStatistics.stream().sorted(new Comparator<ActorStatistics>() {
            @Override
            public int compare(ActorStatistics o1, ActorStatistics o2) {
                return o2.getCount().compareTo(o1.getCount());
            }
        }).collect(Collectors.toList()).stream().sorted(new Comparator<ActorStatistics>() {
            @Override
            public int compare(ActorStatistics o1, ActorStatistics o2) {
                if(o1.getCount() == o2.getCount()){
                    return o2.getLastEventDate().compareTo(o1.getLastEventDate());
                }
                return 0;
            }
        }).collect(Collectors.toList()).stream().sorted(new Comparator<ActorStatistics>() {
            @Override
            public int compare(ActorStatistics o1, ActorStatistics o2) {
                if((o2.getCount() == o1.getCount())
                        && o1.getLastEventDate() == o2.getLastEventDate() ){
                    o1.getLogin().compareTo(o2.getLogin());
                }
                return 0;
            }
        }).collect(Collectors.toList());


        return eventStatistics;
    }

}
