package com.hackerrank.github.controller;

import com.hackerrank.github.exceptions.ActorNotFoundException;
import com.hackerrank.github.exceptions.EventAlreadyExistsException;
import com.hackerrank.github.model.*;
import com.hackerrank.github.repository.ActorRepository;
import com.hackerrank.github.repository.EventRepository;
import com.hackerrank.github.repository.RepoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/*
Returning the actor records ordered by the maximum streak:
The service should be able to return the JSON array of all the actors sorted by the maximum streak
 (i.e., the total number of consecutive days actor has pushed an event to the system)
  in descending order by the GET request at /actors/streak.
  If there are more than one actors with the same maximum streak,
  then order them by the timestamp of the latest event in the descending order.
   If more than one actors have the same timestamp for the latest event,
   then order them by the alphabetical order of login.
    The HTTP response code should be 200.

 */

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


    //note -> user and repo could be updated using this method!
    @PostMapping("/events")
    public ResponseEntity createEvent(@RequestBody Event event ){

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


    @PutMapping("/actors")
    public ResponseEntity updateActorAvatar(@RequestBody Actor actor){

        Actor actorDB =  this.actorRepository.findOne(actor.getId());
        if(actorDB == null){
            LOG.error("Actor id {} not found", actor.getId());
            throw new ActorNotFoundException();
        }

        if(!actorDB.getLogin().equals(actor.getLogin())){
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
                        && o1.getLastEventDate().equals(o2.getLastEventDate())){

                    o1.getLogin().compareTo(o2.getLogin());

                }
                return 0;
            }
        }).collect(Collectors.toList());


        return eventStatistics;
    }

    @GetMapping("/actors/streak")
    public List<UserEvent> findActorStreakStatics(){

        List<UserEvent> eventStatistics = this.eventRepository.findUserEventsGroupByUser(); //builActorStatiscts();//eventRepository.findActorStatistics();

        //events grouped by user
        Map<String, List<UserEvent>> userEventsMap =
                eventStatistics.stream().collect(Collectors.groupingBy(UserEvent::getLogin));

        Map<String,List<UserEvent>> userEventsSortedByDate = new HashMap<>();


        userEventsMap.entrySet().forEach( entry -> {

            List<UserEvent> listSortedEvents = entry.getValue().stream()
                    .sorted(new Comparator<UserEvent>() {

                        @Override
                        public int compare(UserEvent o1, UserEvent o2) {
                            return o1.getCreatedAt().compareTo(o2.getCreatedAt());

                        }
                    }).collect(Collectors.toList());

            userEventsSortedByDate.put(entry.getKey(), listSortedEvents);

        });

        Date dateTemp = null;
        Long consecutiveEvents = 0L;
        String currentUser = null;
        Long maxConsecutiveEvents = 0L;
        Calendar calendarTemp = Calendar.getInstance();

        Map<String, UserEvent> userEventCount = new HashMap<>();

        for(Map.Entry<String, List<UserEvent>> entry : userEventsSortedByDate.entrySet()){

            for(UserEvent e : entry.getValue()){
                if(! e.getLogin().equals(currentUser) ){
                    dateTemp = e.getCreatedAt();
                    currentUser = e.getLogin();
                    consecutiveEvents = 0L; //reset consecutive days
                    maxConsecutiveEvents = 0L;
                    userEventCount.put(e.getLogin(), e);
                    calendarTemp.setTime(dateTemp);
                    continue;
                }




                Calendar created = Calendar.getInstance();
                created.setTime(e.getCreatedAt());

                //long diffInMillies = Math.abs(e.getCreatedAt().getTime() - dateTemp.getTime());
                //long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.HOURS);

               // if(diff < 1  ){
                if(((created.get(Calendar.DAY_OF_MONTH) - calendarTemp.get(Calendar.DAY_OF_MONTH))) == 1){

                    consecutiveEvents++;

                } else {
                    consecutiveEvents = 0L; //reset consecutive events
                }

                if(maxConsecutiveEvents < consecutiveEvents){
                    maxConsecutiveEvents = consecutiveEvents;
                }
                userEventCount.put(e.getLogin(), e);
                userEventCount.get(currentUser).setCount(maxConsecutiveEvents);
                dateTemp = e.getCreatedAt();
            }

        }

        List<UserEvent> result =
                userEventCount.entrySet()
                        .stream()
                        .map(e -> e.getValue())
                        .sorted(new Comparator<UserEvent>() {
                            @Override
                            public int compare(UserEvent o1, UserEvent o2) {
                                return o2.getCount().compareTo(o1.getCount());
                            }
                        })
                        .sorted(new Comparator<UserEvent>() {

                            @Override
                            public int compare(UserEvent o1, UserEvent o2) {
                                if(o1.getCount() == o2.getCount()){
                                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                                }
                                return 0;
                            }
                        })
                        .sorted(new Comparator<UserEvent>() {

                            @Override
                            public int compare(UserEvent o1, UserEvent o2) {
                                if(o2.getCreatedAt().equals(o1.getCreatedAt())
                                        && o2.getCount() == (o1.getCount()) ){

                                    return o1.getLogin().compareTo(o2.getLogin());
                                }
                                return 0;
                            }
                        }).collect(Collectors.toList());

        return result;

    }


    private List<UserEvent> builActorStatiscts(){
        Instant agora = Instant.now();

        List<UserEvent> as = new ArrayList<>();

        as.add(new UserEvent(12L, "marcelo", "", Date.from(agora.plusSeconds(3600 * 200))));

        as.add(new UserEvent(12L, "marcelo", "",Date.from(agora.plusSeconds(0))));
        as.add(new UserEvent(12L, "marcelo","", Date.from(agora.plusSeconds(3600))));
        as.add(new UserEvent(12L, "marcelo","", Date.from(agora.plusSeconds(3610))));
        as.add(new UserEvent(12L, "marcelo","", Date.from(agora.plusSeconds(3600 * 70))));
        as.add(new UserEvent(12L, "marcelo","", Date.from(agora.plusSeconds(3600 * 71))));

        //as.add(new UserEvent(12L, "marcelo","", Date.from(agora.plusSeconds(3600 * 70))));

        //as.add(new UserEvent(12L, "marcelo","", Date.from(agora.plusSeconds(3600 * 60))));
        //as.add(new UserEvent(12L, "marcelo","", Date.from(agora.plusSeconds(3600 * 90))));


        as.add(new UserEvent(13L, "alex","", Date.from(agora.plusSeconds(3601))));
        as.add(new UserEvent(13L, "alex","", Date.from(agora.plusSeconds(3600 * 2))));


        as.add(new UserEvent(14L, "ze", "", Date.from(agora.plusSeconds(3605))));
        as.add(new UserEvent(14L, "ze", "", Date.from(agora.plusSeconds(3605 * 2))));
        as.add(new UserEvent(14L, "ze", "", Date.from(agora.plusSeconds(3610 * 2))));


        return as;
    }

    public static  void main(String arsg[]){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.add(Calendar.HOUR_OF_DAY, 23);

        System.out.println(c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH));
    }


}
