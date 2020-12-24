package com.abcd.paulboutot.cps406project;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public final int LOCATION_REQUEST = 500;
    private MapFragment mapFragment;

    private ListView search_location;
    private ArrayAdapter<String> adapter;

    private DrawerLayout aLayout;
    private ActionBarDrawerToggle toggle;

    private DirectionFragment directionFragment;

    private naviDB database;
    /**
     * Launches app bar on top of screen on start up
     * @param savedInstanceState instructions for creating this view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new naviDB();

        //sets the layout as defined by activity main (magnifying glass basically)
        setContentView(R.layout.activity_main);

        search_location = findViewById(R.id.search_location);
        enableDirectionButton();

        /**
        //locations stored in an ArrayList in strings.xml
        ArrayList<String> arrayLocation = new ArrayList<>();
        arrayLocation.addAll(Arrays.asList(getResources().getStringArray(R.array.my_locations)));

        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayLocation);

        //searches ArrayList
        search_location.setAdapter(adapter);
        **/

        //creates the layout defined by drawerLayout in activity_main.xml
        aLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, aLayout, R.string.open, R.string.close);
        aLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initializes navigation view listener (sidebar listener)
        setNavigationViewListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //NEW: receives text from QRActivity and searches
        String decoded = getIntent().getStringExtra("fromQR");
        if (decoded != null) {
            directionFragment.showComponents();
            setSearch_location(decoded, null);
        }
    }

    /*
         * Determines what to do when an item is selected from the sidebar menu
         */
    public boolean onNavigationItemSelected(@NonNull MenuItem item){

        switch (item.getItemId()){

            case R.id.qr: {
                Intent intent = new Intent(MainActivity.this, QRActivity.class);
                startActivity(intent);
                break;
            }
        }

        aLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * Method sets up the listener for the the sidebar menu
     */
    private void setNavigationViewListener(){
        NavigationView navigationView = findViewById(R.id.nav_menu);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * sets the text in the textFields.
     * @param str_start starting location
     * @param str_end final location
     */
    public void setSearch_location(String str_start, String str_end) {
        EditText startingLocation = (EditText) findViewById(R.id.starting_location);
        EditText finalLocation = (EditText) findViewById(R.id.final_destination);

        if (str_start != null) {
            startingLocation.setText(str_start, EditText.BufferType.EDITABLE);
        }
        if (str_end != null) {
            finalLocation.setText(str_end, EditText.BufferType.EDITABLE);
        }

    }

    /**
     * gets the database of locations.
     * @return the database of locations.
     */
    public naviDB getDatabase() {
        return database;
    }


    /**
     * Gets the map fragment.
     * @return the instance of the map fragment class, that is being shown in this class.
     */
    public MapFragment getMapFragment() {
        return mapFragment;
    }

    /**
     * sets the map fragment.
     * @param mapFragment an instance of the map fragment class that is being shown in this class.
     */
    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    /**
     * Gets the map that is being shown to the user.
     * @return the map that is shown in this activity.
     */
    public GoogleMap getMap() {
        return getMapFragment().getMap();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.goTo) {
            if (directionFragment.componentHidden()) {
                directionFragment.showComponents();
            } else {
                directionFragment.hideComponents();
            }
        }
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    //@Override
    /*
     * Initializes the place where you can type stuff into
     */
   /** public boolean onCreateOptionsMenu(Menu menu) {

        //opens the search bar defined in search_menu.xml
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        //presents location from activity_main.xml
        MenuItem item = menu.findItem(R.id.search_location);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint("Search here");

        //checks if anything was typed into the search bar, if it is, display it, otherwise don't
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    } **/

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (getMap() != null) {
                    getMap().setMyLocationEnabled(true);
                }
                else {
                    throw new NullPointerException("setMap was never called properly in MapFragment.java" +
                            " or setMap was called somewhere else, and was set to null.");
                }
            }
        }
    }

    // enables direction fragment either by invoke or press of a button
    public void enableDirectionButton() {
        directionFragment = new DirectionFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.map_fragment, directionFragment);
        transaction.commit();
    }

}
