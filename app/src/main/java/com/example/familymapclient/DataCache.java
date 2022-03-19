package com.example.familymapclient;

import java.util.List;
import java.util.Map;

import Model.Event;
import Model.Person;

public class DataCache {

    private static DataCache dataCache = new DataCache();
    private Map<String, Person> people;
    private Map<String, Event> events;
    private Map<String, List<Event>> personEvents;



    private DataCache() {
    }

    public Person getPerson(String personID){
        Person newPerson = people.get(personID);
        return newPerson;

    }

    public Event getEvent(String eventID){
        Event newEvent = events.get(eventID);
        return newEvent;
    }


    public static DataCache getInstance() {
        return dataCache;
    }
}
