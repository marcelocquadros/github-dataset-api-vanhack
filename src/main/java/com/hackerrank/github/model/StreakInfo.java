package com.hackerrank.github.model;

import com.hackerrank.github.model.entities.Event;

import java.util.*;
import java.util.stream.Collectors;

public class StreakInfo {

    private Long id;
    private String login;
    private Long count = 0L;
    private Date createdAt;
    private String avatar;

    public StreakInfo(Long id, String login, String avatar, Date createdAt) {
        this.id = id;
        this.login = login;
        this.createdAt = createdAt;
        this.avatar = avatar;
    }

    public StreakInfo(){}

    public static List<StreakInfo> sort(Map<String, StreakInfo> userEventCount) {

        List<StreakInfo> sortedStreakInfo =   userEventCount.entrySet()
                .stream()
                .map(e -> e.getValue())
                .sorted(Comparator.comparing(StreakInfo::getCount).reversed())
                .sorted((o1, o2) -> {
                    if(o1.getCount() == o2.getCount()){
                        return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                    }
                    return 0;
                })
                .sorted( (o1, o2) -> {
                    if(o2.getCreatedAt().equals(o1.getCreatedAt())
                            && o2.getCount() == o1.getCount() ){

                        return o1.getLogin().compareTo(o2.getLogin());
                    }
                    return 0;

                })
                .collect(Collectors.toList());

        return sortedStreakInfo;
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public static Map<String, StreakInfo> buildStreakInfo(Map<String, List<Event>> userEventsSortedByDate) {

        Map<String, StreakInfo> userStreakMap = new HashMap<>();

        for(Map.Entry<String, List<Event>> entry : userEventsSortedByDate.entrySet()){

            String currentUser = null;
            Calendar calendarTemp = Calendar.getInstance();
            Long maxConsecutiveEvents = 0L;
            Long consecutiveEvents = 0L;

            for(Event e : entry.getValue()){

                if(! e.getActor().getLogin().equals(currentUser) ){
                    currentUser = e.getActor().getLogin();
                    consecutiveEvents = 0L; //reset consecutive days
                    maxConsecutiveEvents = 0L;

                    userStreakMap.put(e.getActor().getLogin(),
                            new StreakInfo(
                                    e.getActor().getId(),
                                    e.getActor().getLogin(),
                                    e.getActor().getAvatar(),
                                    e.getCreatedAt()));

                    calendarTemp.setTime(e.getCreatedAt());

                    continue;
                }

                Calendar created = Calendar.getInstance();
                created.setTime(e.getCreatedAt());

                if(((created.get(Calendar.DAY_OF_MONTH) - calendarTemp.get(Calendar.DAY_OF_MONTH))) == 1){
                    consecutiveEvents++;
                } else {
                    consecutiveEvents = 0L; //reset consecutive events
                }

                if(maxConsecutiveEvents < consecutiveEvents){
                    maxConsecutiveEvents = consecutiveEvents;
                }

                userStreakMap.get(currentUser).setCreatedAt(e.getCreatedAt());
            }

            userStreakMap.get(currentUser).setCount(maxConsecutiveEvents);
        }

        return userStreakMap;
    }
}
