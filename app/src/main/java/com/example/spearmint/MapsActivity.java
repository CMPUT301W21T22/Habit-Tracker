package com.example.spearmint;

/**
 * Android Coding. (2018, December 27). How to Implement Google Map in Android Studio | GoogleMap | Android Coding [Video]. YouTube. https://www.youtube.com/watch?v=eiexkzCI8m8&t=301s
 * Google Map Platform, "Quickstart - Adding a Map", Apache 2.0 License, Creative Commons Attribution 4.0 License, https://developers.google.com/maps/documentation/android-sdk/start
 */

// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/** Used the class and xml from the website
 * Google Map Platform, "Quickstart - Adding a Map", Apache 2.0 License, Creative Commons Attribution 4.0 License, https://developers.google.com/maps/documentation/android-sdk/start
 * Used the video for AndroidManifest, map_api and implementation
 * Android Coding. (2018, December 27). How to Implement Google Map in Android Studio | GoogleMap | Android Coding [Video]. YouTube. https://www.youtube.com/watch?v=eiexkzCI8m8&t=301s
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        Experiment experiment = intent.getParcelableExtra("dataKey");
        String exDescription = experiment.getExperimentDescription();

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        final CollectionReference collectionReferenceUser = db.collection("Experiments").document(exDescription).collection("Trials");
        collectionReferenceUser.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value) {
                    String description = doc.getString("trialDescription");
                    ArrayList<String> coordinates = (ArrayList<String>) doc.get("trialLocation");
                    String trialLatitude = coordinates.get(0);
                    String trialLongitude = coordinates.get(1);
                    Double latitude = new Double(trialLatitude);
                    Double longitude = new Double(trialLongitude);
                    LatLng trialLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions()
                            .position(trialLocation)
                            .title(description));
                }
            }
        });

/**
 *

        // Add a marker in Sydney and move the camera
        LatLng Edmonton = new LatLng(53.5461, -113.4938);
        mMap.addMarker(new MarkerOptions()
                .position(Edmonton)
                .title("Marker in Edmonton"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Edmonton));
 */
    }
}


