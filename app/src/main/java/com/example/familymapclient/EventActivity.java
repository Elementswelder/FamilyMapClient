package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class EventActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        DataCache dataCache = DataCache.getInstance();
        dataCache.setEventActivity(true);
        //Setup the fragment for the switch to map fragment, please work
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapsFragment mapsFragment;

        mapsFragment = new MapsFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.eventFragmentFrame, mapsFragment)
                .detach(mapsFragment)
                .attach(mapsFragment)
                .commit();
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