package com.example.familymapclient;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Model.Event;
import Model.Person;

public class EventFilter {
    private List<Event> eventList = new ArrayList<>();
    private List<Person> personList = new ArrayList<>();
    private Set<Event> maleEventsList = new HashSet<>();
    private Set<Event> femaleEventsList = new HashSet<>();
    private Set<Event> fatherSideEvents = new HashSet<>();
    private Set<Event> motherSideEvents = new HashSet<>();
    DataCache data = DataCache.getInstance();


    private EventFilter(){
    }

    public void loadEventData(List<Person> allPeople, List<Event> allEvents){
        this.eventList = allEvents;
        this.personList = allPeople;

        Log.i("DataCache", "finished loading");

    }




}
