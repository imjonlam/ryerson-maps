package com.abcd.paulboutot.cps406project;

import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class naviDB {

    // instance variables
    private DatabaseReference database;
    public HashMap<String,Object> locations = new HashMap<String,Object>();



    //Constructor
    naviDB()
    {
        //Getting database reference
        database = FirebaseDatabase.getInstance().getReference();
        updateLocations();
    }




    void storeLocation(Location loc) {
            database.child("locations").child(loc.getLocationId()).setValue(loc);
    }
    private void collectLocations( Map<String ,Object> locs)
    {
        for(Map.Entry<String, Object> loc: locs.entrySet())
        {
            Map singleLoc = (Map) loc.getValue();

            //Mapped values
            Location l = new Location(singleLoc.get("locationId").toString(),
                    singleLoc.get("roomNumber").toString(),
                    singleLoc.get("buildingCode").toString(),
                    singleLoc.get("buildingName").toString(),
                    singleLoc.get("address").toString(),
                    singleLoc.get("coordinates").toString());

            locations.put(loc.getKey(),l);
        }
    }
    void updateLocations()
    {
        // Attach a listener to read the new location data
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("locations");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    collectLocations((Map<String,Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
