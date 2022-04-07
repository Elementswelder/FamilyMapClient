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
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.Icon;
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
    Event placeboEvent = new Event("null", "null", "Null", 1, 1, "null", "null", "null", 5000);

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
            genderPhoto = (ImageView) getView().findViewById(R.id.genderPic);
            Drawable defaultGender = new IconDrawable(getContext(), FontAwesomeIcons.fa_android).color(R.color.black).sizeDp(35);
            genderPhoto.setImageDrawable(defaultGender);
            loadMarkers(googleMap);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    DataCache dataCache = DataCache.getInstance();
                    removeLines();
                    loadMarkers(googleMap);
                    TextView nameOfPerson = (TextView) getView().findViewById(R.id.mapTextName);
                    TextView eventInfo = (TextView) getView().findViewById(R.id.mapTextEvent);

                    Event markerEvent = (Event) marker.getTag();
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

        }

    };


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
        searchItem.setIcon(new IconDrawable(getContext(), FontAwesomeIcons.fa_search)
                .colorRes(R.color.white)
                .actionBarSize());

        MenuItem settingItem = menu.findItem(R.id.settingMenu);
        settingItem.setIcon(new IconDrawable(getContext(), FontAwesomeIcons.fa_gear)
                .colorRes(R.color.white)
                .actionBarSize());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        //super.onOptionsItemSelected(menu);
        switch (menu.getItemId()) {
            case R.id.searchMenu:
                Toast.makeText(getActivity(), "Clicked on Search", Toast.LENGTH_SHORT).show();
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
        DataCache data = DataCache.getInstance();
        Person clickedOnPerson = data.getPerson(clickedEvent.getPersonID());
        List<Event> personEvents = data.getAssociatedEvents(clickedOnPerson.getPersonID());

        //Check for a spouse and then add lines if spouse exists
        if (data.getPerson(clickedOnPerson.getSpouseID()) != null) {
            Person clickedOnSpouse = data.getPerson(clickedOnPerson.getSpouseID());
            //Set the spouse lines
            List<Event> spouseEvents = data.getAssociatedEvents(clickedOnSpouse.getPersonID());
            Event spouseEvent = getEarliestEvent(spouseEvents);
            LatLng startPoint = new LatLng(clickedEvent.getLatitude(), clickedEvent.getLongitude());
            LatLng endPoint = new LatLng(spouseEvent.getLatitude(), spouseEvent.getLongitude());
            PolylineOptions options = new PolylineOptions().add(startPoint).add(endPoint).color(getActivity().getResources().getColor(R.color.black)).width(10);
            Polyline line = map.addPolyline(options);
            listOfLines.add(line);
        }
        //Setup the Person Events in Chronological Order
        Collections.sort(personEvents, year);
        for (int i = 0; i < personEvents.size(); i++){
            if (i + 1 >= personEvents.size()){
                break;
            } else {
                LatLng start = new LatLng(personEvents.get(i).getLatitude(), personEvents.get(i).getLongitude());
                LatLng end = new LatLng(personEvents.get(i+1).getLatitude(), personEvents.get(i+1).getLongitude());
                PolylineOptions polyOptions = new PolylineOptions().add(start).add(end).color(getActivity().getResources().getColor(R.color.white)).width(10);
                Polyline newLine = map.addPolyline(polyOptions);
                listOfLines.add(newLine);
            }
        }
        //Recursive Function to add the lines for family
        addLineRecursive(clickedOnPerson, map);
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
        DataCache dataCache = DataCache.getInstance();
        Set<Event> eventList = dataCache.getListEvents();

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

    private void addLineRecursive(Person person, GoogleMap map){
        DataCache data = DataCache.getInstance();
        if (person.getFatherID() != null){
            //Recursivly call the data till the end
            addLineRecursive(data.getPerson(person.getFatherID()), map);
            addLineRecursive(data.getPerson(person.getMotherID()), map);

            List<Event> personEvents = data.getAssociatedEvents(person.getPersonID());
            List<Event> motherEvents = data.getAssociatedEvents(person.getMotherID());
            List<Event> fatherEvents = data.getAssociatedEvents(person.getFatherID());

            Collections.sort(personEvents, year);
            Collections.sort(motherEvents, year);
            Collections.sort(fatherEvents, year);
            //Create Mother Lines
            LatLng start = new LatLng(personEvents.get(0).getLatitude(), personEvents.get(0).getLongitude());
            LatLng end = new LatLng(motherEvents.get(0).getLatitude(), motherEvents.get(0).getLongitude());
            PolylineOptions polyOptions = new PolylineOptions().add(start).add(end).color(getActivity().getResources().getColor(R.color.purple_200)).width(checkGeneration(motherEvents.get(0).getYear()));
            Polyline newLine = map.addPolyline(polyOptions);
            listOfLines.add(newLine);
            //Create Father Lines
            LatLng startFather = new LatLng(personEvents.get(0).getLatitude(), personEvents.get(0).getLongitude());
            LatLng endFather = new LatLng(fatherEvents.get(0).getLatitude(), fatherEvents.get(0).getLongitude());
            PolylineOptions polyOptionsFather = new PolylineOptions().add(startFather).add(endFather).color(getActivity().getResources().getColor(R.color.purple_200)).width(checkGeneration(fatherEvents.get(0).getYear()));
            Polyline newLineFather = map.addPolyline(polyOptionsFather);
            listOfLines.add(newLineFather);
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

    public static Comparator<Event> year = new Comparator<Event>() {
        @Override
        public int compare(Event event, Event t1) {
            int yearOne = event.getYear();
            int yearTwo = t1.getYear();

            return yearOne - yearTwo;
        }
    };
}