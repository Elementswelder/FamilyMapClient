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

    //Vars for the clicked On Person, Event etc.
    private Person personActivity;
    private Event eventActivity;
    private boolean isFirstMarker = false;

    private static DataCache dataCache = new DataCache();
    private Map<String, Person> peopleMap = new HashMap<>();
    private Map<String, Event> eventsMap = new HashMap<>();
    private Map<String, List<Event>> personEvents = new HashMap<>();
    private Set<Event> FINAL_EVENT_LIST = new HashSet<>();
    private List<Person> personList = new ArrayList<>();
    private Set<Event> emptyEventSet = new HashSet<>();
    private String username;
    private String authtoken;
    private String personID;
    private String firstName;
    private String lastName;


    //Settings bool and Arrays
    private boolean lifeStory = true;
    private boolean familyTreeLine = true;
    private boolean spouseLine = true;
    private boolean fatherSide = true;
    private boolean motherSide = true;
    private boolean maleEvents = true;
    private boolean femaleEvents = true;
    private boolean isEventActivity = false;

    //Options for Settings
    private Set<Event> maleEventsList = new HashSet<>();
    private Set<Event> femaleEventsList = new HashSet<>();
    private Set<Event> fatherSideEvents = new HashSet<>();
    private Set<Event> motherSideEvents = new HashSet<>();
    private Set<Event> motherSideFemale = new HashSet<>();
    private Set<Event> motherSideMale = new HashSet<>();
    private Set<Event> fatherSideFemale = new HashSet<>();
    private Set<Event> fatherSideMale = new HashSet<>();


    private DataCache() {
    }

    public void loadData(Set<Event> events, Set<Person> people, String authtoken, String personID, String username, String firstName, String lastName){
        this.username = username;
        this.authtoken = authtoken;
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.FINAL_EVENT_LIST = events;


        for (Person peep : people){
            peopleMap.put(peep.getPersonID(), peep);
            personList.add(peep);
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

        sortSettings();
    }


    private void sortSettings(){

        sortGenderList(personList);
        Person root = getPerson(this.personID);
        Person rootMother = getPerson(root.getMotherID());
        Person rootFather = getPerson(root.getFatherID());

        motherSideEvents.addAll(getAssociatedEvents(this.personID));
        fatherSideEvents.addAll(getAssociatedEvents(this.personID));
        if (root.getSpouseID() != null){
            motherSideEvents.addAll(getAssociatedEvents(root.getSpouseID()));
            fatherSideEvents.addAll(getAssociatedEvents(root.getSpouseID()));
        }

        sortMaternal(rootMother);
        sortPaternal(rootFather);
        sortMaternalGender();
        sortPaternalGender();
        Log.i("DataCache", "finished loading");
    }

    private void sortGenderList(List<Person> people){
        for(Person person : people){
            if (person.getGender().equalsIgnoreCase("m")){
                maleEventsList.addAll(getAssociatedEvents(person.getPersonID()));
            } else if (person.getGender().equalsIgnoreCase("f")){
                femaleEventsList.addAll(getAssociatedEvents(person.getPersonID()));

            }
        }
        Log.i("DATA", "TEST");
    }

    private void sortPaternal(Person root){
        fatherSideEvents.addAll(getAssociatedEvents(root.getPersonID()));
        // Person person = getPerson(root.getMotherID());
        if (root.getFatherID() != null){
            sortPaternal(getPerson(root.getMotherID()));
            sortPaternal(getPerson(root.getFatherID()));
        }
    }

    private void sortMaternal(Person root){
        motherSideEvents.addAll(getAssociatedEvents(root.getPersonID()));
       // Person person = getPerson(root.getMotherID());
        if (root.getMotherID() != null){
            sortMaternal(getPerson(root.getMotherID()));
            sortMaternal(getPerson(root.getFatherID()));
        }
    }

    private void sortMaternalGender(){
        for (Event event : motherSideEvents){
            if (getPerson(event.getPersonID()).getGender().equalsIgnoreCase("m")){
                motherSideMale.add(event);
            }else {
                motherSideFemale.add(event);
            }
        }
    }

    private void sortPaternalGender(){
        for (Event event : fatherSideEvents){
            if (getPerson(event.getPersonID()).getGender().equalsIgnoreCase("m")){
                fatherSideMale.add(event);
            }else {
                fatherSideFemale.add(event);
            }
        }
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
        return FINAL_EVENT_LIST;
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

    public Set<Event> getMaleEventsList() {
        return maleEventsList;
    }

    public Set<Event> getFemaleEventsList() {
        return femaleEventsList;
    }

    public Set<Event> getFatherSideEvents() {
        return fatherSideEvents;
    }

    public Set<Event> getMotherSideEvents() {
        return motherSideEvents;
    }

    public Person getPersonActivity() {
        return personActivity;
    }

    public void setPersonActivity(Person personActivity) {
        this.personActivity = personActivity;
    }

    public Event getEventActivity() {
        return eventActivity;
    }

    public void setEventActivity(Event eventActivity) {
        this.eventActivity = eventActivity;
    }

    public boolean isEventActivity() {
        return isEventActivity;
    }

    public void setEventActivity(boolean eventActivty) {
        isEventActivity = eventActivty;
    }

    public void setFatherSideEvents(Set<Event> fatherSideEvents) {
        this.fatherSideEvents = fatherSideEvents;
    }

    public void setMotherSideEvents(Set<Event> motherSideEvents) {
        this.motherSideEvents = motherSideEvents;
    }

    public Set<Event> getMotherSideFemale() {
        return motherSideFemale;
    }

    public void setMotherSideFemale(Set<Event> motherSideFemale) {
        this.motherSideFemale = motherSideFemale;
    }

    public Set<Event> getMotherSideMale() {
        return motherSideMale;
    }

    public void setMotherSideMale(Set<Event> motherSideMale) {
        this.motherSideMale = motherSideMale;
    }

    public Set<Event> getFatherSideFemale() {
        return fatherSideFemale;
    }

    public void setFatherSideFemale(Set<Event> fatherSideFemale) {
        this.fatherSideFemale = fatherSideFemale;
    }

    public Set<Event> getFatherSideMale() {
        return fatherSideMale;
    }

    public void setFatherSideMale(Set<Event> fatherSideMale) {
        this.fatherSideMale = fatherSideMale;
    }

    public boolean isFirstMarker() {
        return isFirstMarker;
    }

    public void setFirstMarker(boolean firstMarker) {
        isFirstMarker = firstMarker;
    }

    public void clearAll(){
        peopleMap.clear();
        eventsMap.clear();
        personEvents.clear();
        FINAL_EVENT_LIST.clear();
        personList.clear();

    }

    public Set<Event> calculateEventsOnSettings(){
        Set<Event> sortedEvents = new HashSet<>();
        if (!isFatherSide() && !isMotherSide()){
            return emptyEventSet;
        }
        else if (!isMaleEvents() && !isFemaleEvents()){
            return emptyEventSet;
        }
        else if ((!isFatherSide()) && (isMotherSide()) && (!isMaleEvents()) && (isFemaleEvents())){
            sortSettings();
            sortedEvents = getMotherSideFemale();
            return sortedEvents;
        }
        else if ((!isFatherSide()) && (isMotherSide()) && (isMaleEvents()) && (!isFemaleEvents())){
            sortSettings();
            sortedEvents = getMotherSideMale();
            return sortedEvents;
        }
        else if ((!isFatherSide()) && (isMotherSide()) && (isMaleEvents()) && (isFemaleEvents())){
            sortSettings();
            sortedEvents = getMotherSideEvents();
            return sortedEvents;
        }
        else if ((isFatherSide()) && (!isMotherSide()) && (!isMaleEvents()) && (isFemaleEvents())){
            sortSettings();
            sortedEvents = getFatherSideFemale();
            return sortedEvents;
        }
        else if ((isFatherSide()) && (!isMotherSide()) && (isMaleEvents()) && (!isFemaleEvents())){
            sortSettings();
            sortedEvents = getFatherSideMale();
            return sortedEvents;
        }
        else if ((isFatherSide()) && (!isMotherSide()) && (isMaleEvents()) && (isFemaleEvents())){
            sortSettings();
            sortedEvents = getFatherSideEvents();
            return sortedEvents;
        }
        else if ((isFatherSide()) && (isMotherSide()) && (!isMaleEvents()) && (isFemaleEvents())){
            sortSettings();
            sortedEvents = getFemaleEventsList();
            return sortedEvents;
        }
        else if ((isFatherSide()) && (isMotherSide()) && (isMaleEvents()) && (!isFemaleEvents())){
            sortSettings();
            sortedEvents = getMaleEventsList();
            return sortedEvents;
        }
        else if ((isFatherSide()) && (isMotherSide()) && (isMaleEvents()) && (isFemaleEvents())){
            //ONLY PLACE FOR FINAL EVENT LIST
            sortSettings();
            FINAL_EVENT_LIST = getListEvents();
            return FINAL_EVENT_LIST;
        }

        //EVERYTHING AFTER THIS IS MANUELLY INPUTED SETTINGS
        return null;
    }

}
