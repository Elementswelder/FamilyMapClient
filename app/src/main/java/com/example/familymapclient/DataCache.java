package com.example.familymapclient;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;

public class DataCache {

    private static DataCache dataCache = new DataCache();
    private Map<String, Person> peopleMap = new HashMap<>();
    private Map<String, Event> eventsMap = new HashMap<>();
    private Map<String, List<Event>> personEvents = new HashMap<>();
    private Set<Event> eventList = new HashSet<>();
    private List<Person> personList = new ArrayList<>();
    private String username;
    private String authtoken;
    private String personID;
    private String firstName;
    private String lastName;

    //Settings bools

    private boolean lifeStory = true;
    private boolean familyTreeLine = true;
    private boolean spouseLine = true;
    private boolean fatherSide = true;
    private boolean motherSide = true;
    private boolean maleEvents = true;
    private boolean femaleEvents = true;

    private DataCache() {
    }

    public void loadData(Set<Event> events, Set<Person> people, String authtoken, String personID, String username, String firstName, String lastName){
        this.username = username;
        this.authtoken = authtoken;
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.eventList = events;


        for (Person peep : people){
            peopleMap.put(peep.getPersonID(), peep);
        }
        for (Event event : events){
            eventsMap.put(event.getEventID(), event);
            List<Event> listEvents = personEvents.get(event.getPersonID());
            if (listEvents == null) {
                listEvents = new ArrayList<>();
                personEvents.put(event.getPersonID(), listEvents);
            }
            listEvents.add(event);
        }
        Log.i("DataCache", "finished loading");
    }

    public Person getPerson(String personID){
        Person newPerson = peopleMap.get(personID);
        return newPerson;

    }

    public Event getEvent(String eventID){
        Event newEvent = eventsMap.get(eventID);
        return newEvent;
    }

    public Set<Event> getListEvents(){
        return eventList;
    }

    public List<Person> getListPerson(){
        return personList;
    }

    public List<Event> getAssociatedEvents(String personID){
        return personEvents.get(personID);
    }


    public static DataCache getInstance() {
        return dataCache;
    }

    public boolean isLifeStory() {
        return lifeStory;
    }

    public void setLifeStory(boolean lifeStory) {
        this.lifeStory = lifeStory;
    }

    public boolean isFamilyTreeLine() {
        return familyTreeLine;
    }

    public void setFamilyTreeLine(boolean familyTreeLine) {
        this.familyTreeLine = familyTreeLine;
    }

    public boolean isSpouseLine() {
        return spouseLine;
    }

    public void setSpouseLine(boolean spouseLine) {
        this.spouseLine = spouseLine;
    }

    public boolean isFatherSide() {
        return fatherSide;
    }

    public void setFatherSide(boolean fatherSide) {
        this.fatherSide = fatherSide;
    }

    public boolean isMotherSide() {
        return motherSide;
    }

    public void setMotherSide(boolean motherSide) {
        this.motherSide = motherSide;
    }

    public boolean isMaleEvents() {
        return maleEvents;
    }

    public void setMaleEvents(boolean maleEvents) {
        this.maleEvents = maleEvents;
    }

    public boolean isFemaleEvents() {
        return femaleEvents;
    }

    public void setFemaleEvents(boolean femaleEvents) {
        this.femaleEvents = femaleEvents;
    }
}
