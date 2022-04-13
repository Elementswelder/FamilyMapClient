package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int EVENT_TYPE = 0;
    private static final int PERSON_TYPE = 1;
    DataCache dataCache = DataCache.getInstance();
    List<Event> allEventList = new ArrayList<>();
    List<Person> newAllPeople = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setTitle("Family Map: Search");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.RecyclerViewPerson);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        List<Person> allPeople = dataCache.getListPerson();
        Set<Event> allSetEvents = dataCache.calculateEventsOnSettings();
        allEventList = new ArrayList<>(allSetEvents);
        SearchView search = (SearchView)findViewById(R.id.searchBar);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                allEventList.clear();
                newAllPeople.clear();
                for (Event event : allSetEvents){
                    if ((event.getEventType().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT)) || event.getCountry().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT)))
                            || event.getCity().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                        allEventList.add(event);
                    }
                }
                for (Person person : allPeople){
                    if ((person.getLastName().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) || person.getFirstName().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))){
                        newAllPeople.add(person);
                    }
                }
                SearchAdaptor adapt = new SearchAdaptor(newAllPeople, allEventList);
                recyclerView.setAdapter(adapt);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                allEventList.clear();
                newAllPeople.clear();
                for (Event event : allSetEvents){
                    if ((event.getEventType().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT)) || event.getCountry().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT)))
                            || event.getCity().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                        allEventList.add(event);
                    }
                }
                for (Person person : allPeople){
                    if ((person.getLastName().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) || person.getFirstName().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))){
                        newAllPeople.add(person);
                    }
                }
                SearchAdaptor adapt = new SearchAdaptor(newAllPeople, allEventList);
                recyclerView.setAdapter(adapt);
                return false;
            }
        });

      //  SearchAdaptor adaptor = new SearchAdaptor(allPeople, allEventList);
     ///   recyclerView.setAdapter(adaptor);
    }



    private class SearchAdaptor extends RecyclerView.Adapter<SearchAdaptorViewHolder> {
        private final List<Person> allPeople;
        private final List<Event> allEvents;

        SearchAdaptor(List<Person> peeps, List<Event> events){
            this.allEvents = events;
            this.allPeople = peeps;
        }

        @Override
        public int getItemViewType(int position){
            return position < allPeople.size() ? PERSON_TYPE : EVENT_TYPE;
        }

        @NonNull
        @Override
        public SearchAdaptorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view;

            if (viewType == PERSON_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_layout, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_layout, parent, false);
            }

            return new SearchAdaptorViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchAdaptorViewHolder holder, int position){
            if (position < allPeople.size()){
                holder.bind(allPeople.get(position));
            } else {
                holder.bind(allEvents.get(position - allPeople.size()));
            }
        }

        @Override
        public int getItemCount() {
            return allPeople.size() + allEvents.size();
        }
    }

    private class SearchAdaptorViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        private final TextView eventName;
        private final TextView personName;
        private final ImageView imageView;

        private final int viewType;
        private Event event;
        private Person person;

        SearchAdaptorViewHolder(View view, int viewType){
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if (viewType == PERSON_TYPE){
                eventName = itemView.findViewById(R.id.eventExtendTypePeep);
                personName = null;
                imageView = itemView.findViewById(R.id.personEventIconPeep);
            }
            else {
                eventName = itemView.findViewById(R.id.eventExtendType);
                personName = itemView.findViewById(R.id.eventExtendPerson);
                imageView = itemView.findViewById(R.id.personEventIcon);
            }
        }
        private void bind(Person person){
            this.person = person;
            String name = person.getFirstName() + " " + person.getLastName();
            eventName.setText(name);
            if (person.getGender().equalsIgnoreCase("m")){
                Drawable maleGender = new IconDrawable(getBaseContext(), FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(35);
                imageView.setImageDrawable(maleGender);
            }
            else {
                Drawable femaleGender = new IconDrawable(getBaseContext(), FontAwesomeIcons.fa_female).colorRes(R.color.pink).sizeDp(35);
                imageView.setImageDrawable(femaleGender);
            }
        }

        private void bind(Event event){
            this.event = event;
            String eventType = event.getEventType() + ": " + event.getCity() + " " + event.getCountry() + "(" + event.getYear() + ")";
            eventName.setText(eventType);
            Person personEvent = dataCache.getPerson(event.getPersonID());
            String name = personEvent.getFirstName() + " " + personEvent.getLastName();
            personName.setText(name);
            Drawable marker = new IconDrawable(getBaseContext(), FontAwesomeIcons.fa_map_marker).colorRes(R.color.black).sizeDp(35);
            imageView.setImageDrawable(marker);
        }

        @Override
        public void onClick(View view) {
            if (viewType == PERSON_TYPE){
                dataCache.setPersonActivity(person);
                startActivity(new Intent(getBaseContext(), PersonActivity.class));
            }
            else {
                dataCache.setEventActivity(event);
                startActivity(new Intent(getBaseContext(), EventActivity.class));
            }
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




}