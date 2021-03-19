package com.example.spearmint;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;

import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class ExperimentFragment extends Fragment {

    Button addExperiment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        final CollectionReference collectionReference = db.collection("Experiments");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_experiment, container, false);

        ListView listView = (ListView) view.findViewById(R.id.experiment_list);

        /*
        Samantha Squires. (2016, March 1). 1.5: Display a ListView in a Fragment [Video]. YouTube. https://www.youtube.com/watch?v=edZwD54xfbk
        Abram Hindle, "Lab 3 instructions - CustomList", Public Domain, 2021-02-12, https://eclass.srv.ualberta.ca/pluginfile.php/6713985/mod_resource/content/1/Lab%203%20instructions%20-%20CustomList.pdf
        https://stackoverflow.com/users/788677/rakhita. (2011, Nov 17). Custom Adapter for List View. https://stackoverflow.com/. https://stackoverflow.com/questions/8166497/custom-adapter-for-list-view/8166802#8166802
         */

        ArrayList<Experiment> experimentList = new ArrayList<>();

        ExperimentAdapter customAdapter = new ExperimentAdapter(getActivity(), R.layout.content, experimentList);

        listView.setAdapter(customAdapter);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                experimentList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {

                    String description = doc.getId();
                    String region = (String) doc.get("experimentRegion");
                    String count = (String) doc.get("experimentCount");

                    experimentList.add(new Experiment(description, region, count));
                }
                customAdapter.notifyDataSetChanged();
            }
        });

        // Deleting an experiment from Firebase
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String experimentID = experimentList.get(position).getExperimentDescription();
                collectionReference.document(experimentID).delete();

                return false;
            }
        });

        // Opening a new activity/fragment to comment/post questions
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle experimentInfo = new Bundle();
                ExperimentDetails detailsFragment = new ExperimentDetails();
                String experimentTitle = experimentList.get(position).getExperimentDescription();

                experimentInfo.putString("dataKey", experimentTitle);
                detailsFragment.setArguments(experimentInfo);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.navHostfragment, detailsFragment);
                transaction.commit();

            }
        });

        addExperiment = view.findViewById(R.id.addButton);
        addExperiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PublishExperimentFragment publishFragment = new PublishExperimentFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.navHostfragment, publishFragment);
                transaction.commit();

            }
        });
        return view;


    }
}