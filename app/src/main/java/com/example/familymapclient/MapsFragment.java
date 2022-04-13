package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.*;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Model.Event;
import Model.Person;

public class MapsFragment extends Fragment {

    float googleColorRed = BitmapDescriptorFactory.HUE_RED;
    float googleColorBlue = BitmapDescriptorFactory.HUE_BLUE;
    float googleColorGreen = BitmapDescriptorFactory.HUE_GREEN;
    float googleColorOrange = BitmapDescriptorFactory.HUE_ORANGE;
    List<Polyline> listOfLines = new ArrayList<>();
    int colorNum = 0;
    Map<String, Integer> colorMap = new HashMap<>();
    ImageView genderPhoto;
    GoogleMap defaultMap;
    DataCache dataCache = DataCache.getInstance();
    Event placeboEvent = new Event("null", "null", "Null", 1, 1, "null", "null", "null", 5000);
    Set<Event> eventList = new HashSet<>();

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            //Load the markers on the map on start
            LinearLayout informationBox = (LinearLayout) getView().findViewById(R.id.eventActivityClick);
            defaultMap = googleMap;
            genderPhoto = (ImageView) getView().findViewById(R.id.genderPic);
            Drawable defaultGender = new IconDrawable(getContext(), FontAwesomeIcons.fa_android).color(R.color.black).sizeDp(35);
            genderPhoto.setImageDrawable(defaultGender);
            loadMarkers(googleMap);

            if (dataCache.isEventActivity()){
                setUpEventActivity();
            }

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    removeLines();
                    loadMarkers(googleMap);
                    TextView nameOfPerson = (TextView) getView().findViewById(R.id.mapTextName);
                    TextView eventInfo = (TextView) getView().findViewById(R.id.mapTextEvent);

                    Event markerEvent = (Event) marker.getTag();
                    dataCache.setEventActivity(markerEvent);
                    dataCache.setPersonActivity(dataCache.getPerson(markerEvent.getPersonID()));
                    assert markerEvent != null;
                    Person personClicked = dataCache.getPerson(markerEvent.getPersonID());
                    if (personClicked.getGender().equalsIgnoreCase("m")){
                        Drawable maleGender = new IconDrawable(getContext(), FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(35);
                        genderPhoto.setImageDrawable(maleGender);
                    }
                    else {
                        Drawable femaleGender = new IconDrawable(getContext(), FontAwesomeIcons.fa_female).colorRes(R.color.pink).sizeDp(35);
                        genderPhoto.setImageDrawable(femaleGender);
                    }
                    String name = personClicked.getFirstName() + " " + personClicked.getLastName();
                    nameOfPerson.setText(name);
                    String eventString = markerEvent.getEventType() + ": " + markerEvent.getCity() + ", " + markerEvent.getCountry() + " (" + markerEvent.getYear() + ")";
                    eventInfo.setText(eventString);
                    setLines(markerEvent, googleMap);
                    return true;
                }
            });

            informationBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataCache.getPersonActivity() != null) {
                        startActivity(new Intent(getContext(), PersonActivity.class));
                    }
                    else {
                        Toast.makeText(getContext(), "Please select a marker to see a person", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    };

    @Override
    public void onResume(){
        super.onResume();
        if (defaultMap != null){
            defaultMap.clear();
            removeLines();
            loadMarkers(defaultMap);
           // eventList = dataCache.calculateEventsOnSettings();
            if (dataCache.getEventActivity() != null){
                setLines(dataCache.getEventActivity(), defaultMap);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Iconify.with(new FontAwesomeModule());

        setHasOptionsMenu(true);


        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.searchMenu);
        MenuItem settingItem = menu.findItem(R.id.settingMenu);
        if (!dataCache.isEventActivity()) {
            searchItem.setIcon(new IconDrawable(getContext(), FontAwesomeIcons.fa_search)
                    .colorRes(R.color.white)
                    .actionBarSize());

            settingItem.setIcon(new IconDrawable(getContext(), FontAwesomeIcons.fa_gear)
                    .colorRes(R.color.white)
                    .actionBarSize());
        }
        else{
            searchItem.setVisible(false);
            settingItem.setVisible(false);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        //super.onOptionsItemSelected(menu);
        switch (menu.getItemId()) {
            case R.id.searchMenu:
                startActivity(new Intent(getContext(), SearchActivity.class));
                return true;
            case R.id.settingMenu:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(menu);
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }

    private void setLines(Event clickedEvent, GoogleMap map){
        //Set up
        Person clickedOnPerson = dataCache.getPerson(clickedEvent.getPersonID());
        List<Event> personEvents = dataCache.getAssociatedEvents(clickedOnPerson.getPersonID());

        //Check for a spouse and then add lines if spouse exists
        if ((dataCache.getPerson(clickedOnPerson.getSpouseID()) != null) && (dataCache.isSpouseLine()) && (eventExists(clickedOnPerson.getSpouseID()))) {
            Person clickedOnSpouse = dataCache.getPerson(clickedOnPerson.getSpouseID());
            //Set the spouse lines
            List<Event> spouseEvents = dataCache.getAssociatedEvents(clickedOnSpouse.getPersonID());
            Event spouseEvent = getEarliestEvent(spouseEvents);
            LatLng startPoint = new LatLng(clickedEvent.getLatitude(), clickedEvent.getLongitude());
            LatLng endPoint = new LatLng(spouseEvent.getLatitude(), spouseEvent.getLongitude());
            PolylineOptions options = new PolylineOptions().add(startPoint).add(endPoint).color(getActivity().getResources().getColor(R.color.black)).width(10);
            Polyline line = map.addPolyline(options);
            listOfLines.add(line);
        }
        //Setup the Person Events in Chronological Order
        Collections.sort(personEvents, year);
        if(dataCache.isLifeStory()) {
            for (int i = 0; i < personEvents.size(); i++) {
                if (i + 1 >= personEvents.size()) {
                    break;
                } else {
                    LatLng start = new LatLng(personEvents.get(i).getLatitude(), personEvents.get(i).getLongitude());
                    LatLng end = new LatLng(personEvents.get(i + 1).getLatitude(), personEvents.get(i + 1).getLongitude());
                    PolylineOptions polyOptions = new PolylineOptions().add(start).add(end).color(getActivity().getResources().getColor(R.color.white)).width(10);
                    Polyline newLine = map.addPolyline(polyOptions);
                    listOfLines.add(newLine);
                }
            }
        }
        //Recursive Function to add the lines for family
        if (dataCache.isFamilyTreeLine()) {
            dataCache.setFirstMarker(true);
            addLineRecursive(clickedOnPerson, map, 24);
        }
    }

//Find the earliest event (usually birth) for the spouse event
    private Event getEarliestEvent(List<Event> events){
        Event lowestEvent = placeboEvent;
        for (Event event : events){
            if (event.getYear() < lowestEvent.getYear()){
                lowestEvent = event;
            }
        }
        return lowestEvent;
    }

//Load all the markers onto the map
    private void loadMarkers(GoogleMap googleMap){

        eventList = dataCache.calculateEventsOnSettings();

        for (Event newEvent : eventList){
            if (newEvent.getEventType().equalsIgnoreCase("birth")) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(newEvent.getLatitude(), newEvent.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(googleColorBlue)));
                marker.setTag(newEvent);
            } else if (newEvent.getEventType().equalsIgnoreCase("death")){
                Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(newEvent.getLatitude(), newEvent.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(googleColorRed)));
                marker.setTag(newEvent);
            } else if (newEvent.getEventType().equalsIgnoreCase("marriage")){
                Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(newEvent.getLatitude(), newEvent.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(googleColorGreen)));
                marker.setTag(newEvent);
            } else {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(newEvent.getLatitude(), newEvent.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(changeColor(newEvent.getEventType()))));
                marker.setTag(newEvent);
            }
        }
    }
//Remove the lines since .clear is not working
    private void removeLines(){
        for (Polyline poly : listOfLines){
            poly.remove();
        }
    }
//Return the width of the line based on the generation of person, calculated by birth year
    private int checkGeneration(int birthYear){
        //Gen 1 = 12
        //Gen 2 = 10
        //Gen 3 = 8
        //Gen 4 = 6
        //Gen 5 = 4
        //Gen 6 = 2
        if (birthYear >= 2000 && birthYear <= 2020){
            return 12;
        }
        else if (birthYear >= 1965 && birthYear <= 1999){
            return 10;
        }
        else if (birthYear >= 1930 && birthYear <= 1964){
            return 8;
        }
        else if (birthYear >= 1890 && birthYear <= 1929){
            return 6;
        }
        else if (birthYear >= 1860 && birthYear <= 1889){
            return 4;
        }
        else if (birthYear >= 1820 && birthYear <= 1859){
            return 2;
        }
        return 20;
    }

    private void addLineRecursive(Person person, GoogleMap map, int width){
        if (person.getFatherID() != null){
            //Recursivly call the data till the end
            addLineRecursive(dataCache.getPerson(person.getFatherID()), map, width - 8);
            addLineRecursive(dataCache.getPerson(person.getMotherID()), map, width -8);

            List<Event> personEvents = dataCache.getAssociatedEvents(person.getPersonID());
            List<Event> motherEvents = dataCache.getAssociatedEvents(person.getMotherID());
            List<Event> fatherEvents = dataCache.getAssociatedEvents(person.getFatherID());

            Collections.sort(personEvents, year);
            Collections.sort(motherEvents, year);
            Collections.sort(fatherEvents, year);
            if(person.getPersonID().equals(dataCache.getEventActivity().getPersonID())) {
                //Mother Lines for Clicked Event
                LatLng start = new LatLng(dataCache.getEventActivity().getLatitude(), dataCache.getEventActivity().getLongitude());
                LatLng end = new LatLng(motherEvents.get(0).getLatitude(), motherEvents.get(0).getLongitude());
                PolylineOptions polyOptions = new PolylineOptions().add(start).add(end).color(getActivity().getResources().getColor(R.color.purple_200)).width(width);
                Polyline newLine = map.addPolyline(polyOptions);
                listOfLines.add(newLine);

                //Create Father Lines
                LatLng startFather = new LatLng(dataCache.getEventActivity().getLatitude(), dataCache.getEventActivity().getLongitude());
                LatLng endFather = new LatLng(fatherEvents.get(0).getLatitude(), fatherEvents.get(0).getLongitude());
                PolylineOptions polyOptionsFather = new PolylineOptions().add(startFather).add(endFather).color(getActivity().getResources().getColor(R.color.purple_200)).width(width);
                Polyline newLineFather = map.addPolyline(polyOptionsFather);
                listOfLines.add(newLineFather);
            } else {
                //Create Mother Lines
                LatLng start = new LatLng(personEvents.get(0).getLatitude(), personEvents.get(0).getLongitude());
                LatLng end = new LatLng(motherEvents.get(0).getLatitude(), motherEvents.get(0).getLongitude());
                PolylineOptions polyOptions = new PolylineOptions().add(start).add(end).color(getActivity().getResources().getColor(R.color.purple_200)).width(checkGeneration(width));
                Polyline newLine = map.addPolyline(polyOptions);
                listOfLines.add(newLine);

                //Create Father Lines

                LatLng startFather = new LatLng(personEvents.get(0).getLatitude(), personEvents.get(0).getLongitude());
                LatLng endFather = new LatLng(fatherEvents.get(0).getLatitude(), fatherEvents.get(0).getLongitude());
                PolylineOptions polyOptionsFather = new PolylineOptions().add(startFather).add(endFather).color(getActivity().getResources().getColor(R.color.purple_200)).width(width);
                Polyline newLineFather = map.addPolyline(polyOptionsFather);
                listOfLines.add(newLineFather);
            }


        }
    }

    private int changeColor(String eventType){
        colorNum = colorNum + 10;
        eventType = eventType.toLowerCase(Locale.ROOT);
        if (colorMap.get(eventType) == null){
            colorMap.put(eventType,colorNum);
            return colorNum;
        } else {
            return colorMap.get(eventType);
        }
    }

    private void setUpEventActivity(){
        Event selectedOne = dataCache.getEventActivity();
        LatLng current = new LatLng(selectedOne.getLatitude(), selectedOne.getLongitude());
        defaultMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        Person personClicked = dataCache.getPerson(selectedOne.getPersonID());
        if (personClicked.getGender().equalsIgnoreCase("m")){
            Drawable maleGender = new IconDrawable(getContext(), FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(35);
            genderPhoto.setImageDrawable(maleGender);
        }
        else {
            Drawable femaleGender = new IconDrawable(getContext(), FontAwesomeIcons.fa_female).colorRes(R.color.pink).sizeDp(35);
            genderPhoto.setImageDrawable(femaleGender);
        }
        TextView nameOfPerson = (TextView) getView().findViewById(R.id.mapTextName);
        TextView eventInfo = (TextView) getView().findViewById(R.id.mapTextEvent);
        String name = personClicked.getFirstName() + " " + personClicked.getLastName();
        nameOfPerson.setText(name);
        String eventString = selectedOne.getEventType() + ": " + selectedOne.getCity() + ", " + selectedOne.getCountry() + " (" + selectedOne.getYear() + ")";
        eventInfo.setText(eventString);
        setLines(selectedOne, defaultMap);
       // dataCache.setEventActivty(false);
    }

    private boolean eventExists(String PersonID){
        for (Event event : eventList){
            if (event.getPersonID().equalsIgnoreCase(PersonID)){
                return true;
            }
        }
        return false;
    }

    public static Comparator<Event> year = new Comparator<Event>() {
        @Override
        public int compare(Event event, Event t1) {
            int yearOne = event.getYear();
            int yearTwo = t1.getYear();
            return yearOne - yearTwo;
        }
    };
}