package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Iconify.with(new FontAwesomeModule());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        DataCache dataCache = DataCache.getInstance();
        Person selectedOne = dataCache.getPersonActivity();
        String first = selectedOne.getFirstName();
        String last = selectedOne.getLastName();
        String name = first + " " + last;
        String personGender = selectedOne.getGender();
        getSupportActionBar().setTitle("Person: " + name);

        TextView firstName = (TextView)findViewById(R.id.firstName);
        TextView lastName = (TextView)findViewById(R.id.lastname);
        TextView gender = (TextView)findViewById(R.id.personGender);

        //Set the text for names and gender
        firstName.setText(first);
        lastName.setText(last);
        if (personGender.equalsIgnoreCase("m")){
            gender.setText("Male");
        }
        else {
            gender.setText("Female");
        }

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        List<Event> personEvents = dataCache.getAssociatedEvents(selectedOne.getPersonID());
        List<Person> personRelatives = new ArrayList<>();
        boolean child = true;
        if (selectedOne.getFatherID() != null){
            personRelatives.add(dataCache.getPerson(selectedOne.getFatherID()));
        }
        if (selectedOne.getMotherID() != null){
            personRelatives.add(dataCache.getPerson(selectedOne.getMotherID()));
        }
        if (selectedOne.getSpouseID() != null){
            personRelatives.add(dataCache.getPerson(selectedOne.getSpouseID()));
        } else {
            child = false;
        }
        if (child) {
            if (selectedOne.getGender().equals("m")) {
                List<Person> allPeople = dataCache.getListPerson();
                for (Person peep : allPeople) {
                    try {
                        if (peep.getFatherID().equals(selectedOne.getPersonID())) {
                            personRelatives.add(peep);
                            break;
                        }
                    } catch (Exception ex){
                        Log.i("Crash", "not bad");
                    }
                }
            } else {
                List<Person> allPeople = dataCache.getListPerson();
                for (Person peep : allPeople) {
                    try {
                        if (peep.getMotherID().equals(selectedOne.getPersonID())) {
                            personRelatives.add(peep);
                            break;
                        }
                    }catch (Exception ex){
                        Log.i("Crash", "not bad");
                    }
                }
            }
        }
        Collections.sort(personEvents, year);

        expandableListView.setAdapter(new ExpandableListAdapter(personEvents, personRelatives));

    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int EVENT_POSITION = 0;
        private static final int PERSON_POSITION = 1;

        private final List<Event> allEvents;
        private final List<Person> allRelatives;

        ExpandableListAdapter(List<Event> events, List<Person> people) {
            this.allEvents = events;
            this.allRelatives = people;
        }

        @Override
        public int getGroupCount(){
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition){
            switch (groupPosition) {
                case EVENT_POSITION:
                    return allEvents.size();
                case PERSON_POSITION:
                    return allRelatives.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            // Not used
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // Not used
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.group_item_list, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_POSITION:
                    titleView.setText(R.string.lifeEvents);
                    break;
                case PERSON_POSITION:
                    titleView.setText(R.string.family);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case EVENT_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_layout, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case PERSON_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_layout, parent, false);
                    initializePeresonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeEventView(View eventView, final int childPosition){
            DataCache dataCache = DataCache.getInstance();
            Event currentEvent = allEvents.get(childPosition);
            TextView eventType = eventView.findViewById(R.id.eventExtendType);
            String eventTypeAll = currentEvent.getEventType() + ": " + currentEvent.getCity() + ", " + currentEvent.getCountry() + "(" + currentEvent.getYear() + ")";
            eventType.setText(eventTypeAll);

            TextView eventName = eventView.findViewById(R.id.eventExtendPerson);
            String fullName = dataCache.getPersonActivity().getFirstName() + " " + dataCache.getPersonActivity().getLastName();
            eventName.setText(fullName);

            ImageView markerIcon = (ImageView)eventView.findViewById(R.id.personEventIcon);
            Drawable maleGender = new IconDrawable(getBaseContext(), FontAwesomeIcons.fa_map_marker).colorRes(R.color.black).sizeDp(35);
            markerIcon.setImageDrawable(maleGender);

            eventView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataCache data = DataCache.getInstance();
                    data.setEventActivity(allEvents.get(childPosition));
                    startActivity(new Intent(getBaseContext(), EventActivity.class));
                }
            });
        }

        private void initializePeresonView(View personView, final int childPosition){
            DataCache data = DataCache.getInstance();
            TextView personName = personView.findViewById(R.id.eventExtendType);
            String name = allRelatives.get(childPosition).getFirstName() + " " + allRelatives.get(childPosition).getLastName();
            personName.setText(name);

            String relation = calculateRelationship(allRelatives.get(childPosition));
            TextView relative = personView.findViewById(R.id.eventExtendPerson);
            relative.setText(relation);
            ImageView genderIcon = (ImageView)personView.findViewById(R.id.personEventIcon);
            if (allRelatives.get(childPosition).getGender().equalsIgnoreCase("m")){
                Drawable maleGender = new IconDrawable(getBaseContext(), FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(35);
                genderIcon.setImageDrawable(maleGender);
            }
            else {
                Drawable femaleGender = new IconDrawable(getBaseContext(), FontAwesomeIcons.fa_female).colorRes(R.color.pink).sizeDp(35);
                genderIcon.setImageDrawable(femaleGender);
            }
            personView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataCache data = DataCache.getInstance();
                    data.setPersonActivity(allRelatives.get(childPosition));
                    startActivity(new Intent(getBaseContext(), PersonActivity.class));
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition){
            return true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private String calculateRelationship(Person person){
        DataCache data = DataCache.getInstance();

        if ((data.getPersonActivity().getFatherID() != null) && (data.getPersonActivity().getFatherID().equals(person.getPersonID()))){
            return "Father";
        } else if ((data.getPersonActivity().getMotherID() != null) && (data.getPersonActivity().getMotherID().equals(person.getPersonID()))){
            return "Mother";
        } else if ((data.getPersonActivity().getSpouseID() != null) && (data.getPersonActivity().getSpouseID().equals(person.getPersonID()))){
            return "Spouse";
        }
        return "Child";
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