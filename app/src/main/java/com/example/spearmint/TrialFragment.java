package com.example.spearmint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TrialFragment extends Fragment {

    private static final String SHARED_PREFS = "SharedPrefs";
    private static final String TEXT = "Text";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Button addTrial;
        Button goBack;
        Button scanner;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString(TEXT, null);

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        View view = inflater.inflate(R.layout.fragment_trials, container, false);
        Experiment experiment = getArguments().getParcelable("dataKey");
        String exDescription = experiment.getExperimentDescription();
        String exType = experiment.getTrialType();

        ListView listView = (ListView) view.findViewById(R.id.trial_list);

        final CollectionReference collectionReferenceExperiments = db.collection("Experiments");
        final CollectionReference collectionReferenceTrials = collectionReferenceExperiments.document(exDescription).collection("Trials");
        final CollectionReference collectionReferenceUser = db.collection("User");

        /**
         * Samantha Squires. (2016, March 1). 1.5: Display a ListView in a Fragment [Video]. YouTube. https://www.youtube.com/watch?v=edZwD54xfbk
         * Abram Hindle, "Lab 3 instructions - CustomList", Public Domain, 2021-02-12, https://eclass.srv.ualberta.ca/pluginfile.php/6713985/mod_resource/content/1/Lab%203%20instructions%20-%20CustomList.pdf
         *  https://stackoverflow.com/users/788677/rakhita. (2011, Nov 17). Custom Adapter for List View. https://stackoverflow.com/. https://stackoverflow.com/questions/8166497/custom-adapter-for-list-view/8166802#8166802
         */

        ArrayList<Trial> trialList = new ArrayList<>();

        TrialAdapter customAdapter = new TrialAdapter(getActivity(), R.layout.trial_content, trialList);

        listView.setAdapter(customAdapter);

        collectionReferenceTrials.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                trialList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {

                    String trialDescription = doc.getId();
                    String trialResult = (String) doc.get("trialResult");
                    String experimenter = (String) doc.get("experimenter");
                    ArrayList<String> coordinates = (ArrayList<String>) doc.get("trialLocation");

                    trialList.add(new Trial(trialDescription, trialResult, experimenter, coordinates));
                }
                customAdapter.notifyDataSetChanged();
            }
        });

        addTrial = view.findViewById(R.id.add_trial);
        addTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle experimentInfo = new Bundle();
                experimentInfo.putParcelable("dataKey", experiment);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                if (experiment.getStatus().contentEquals("Open")) {
                    switch (exType) {
                        case "Counts":
                            PublishCount publishCount = new PublishCount();
                            publishCount.setArguments(experimentInfo);
                            transaction.replace(R.id.nav_host_fragment, publishCount);
                            transaction.commit();
                            break;
                        case "Binomial Trials":
                            PublishBinomial publishBinomial = new PublishBinomial();
                            publishBinomial.setArguments(experimentInfo);
                            transaction.replace(R.id.nav_host_fragment, publishBinomial);
                            transaction.commit();
                            break;
                        case "Non-negative Integer Counts":
                            PublishNonNegative publishNonNegative = new PublishNonNegative();
                            publishNonNegative.setArguments(experimentInfo);
                            transaction.replace(R.id.nav_host_fragment, publishNonNegative);
                            transaction.commit();
                            break;
                        case "Measurement Trials":
                            PublishMeasurement publishMeasurement = new PublishMeasurement();
                            publishMeasurement.setArguments(experimentInfo);
                            transaction.replace(R.id.nav_host_fragment, publishMeasurement);
                            transaction.commit();
                            break;
                    }
                }
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("CANNOT COMPLETE REQUEST");
                    alert.setMessage("This experiment has been closed. Please tap anywhere to continue.");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });
                    alert.show();
                }
            }
        });

        ArrayList<Boolean> ownsExperiment = new ArrayList<>();
        collectionReferenceUser.document(uniqueID).collection("ownedExperiments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                ownsExperiment.clear();
                for (QueryDocumentSnapshot doc : value) {
                    String experimentName = doc.getId();
                    if (experimentName.contentEquals(experiment.getExperimentDescription())) {
                        ownsExperiment.add(true);
                    }
                }
                ownsExperiment.add(false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Trial trial = trialList.get(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle(trial.getTrialDescription());
                alert.setMessage(trial.getTrialResult() + "\n" + trial.getExperimenter());
                alert.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
                alert.setPositiveButton("CODES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle trialInfo = new Bundle();
                        QRCodeFragment qrCodeFragment = new QRCodeFragment();
                        Trial trial = trialList.get(position);

                        trialInfo.putParcelable("dataKey", trial);
                        qrCodeFragment.setArguments(trialInfo);
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.nav_host_fragment, qrCodeFragment);
                        transaction.commit();
                    }
                });

                /*
                if (ownsExperiment.get(0)) {
                    alert.setPositiveButton("IGNORE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            collectionReferenceTrials.document(trial.getTrialDescription()).delete();
                        }
                    });
                }*/
                alert.show();
            }
        });

        scanner = view.findViewById(R.id.scanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle experimentInfo = new Bundle();
                ScannerFragment scannerFragment = new ScannerFragment();
                experimentInfo.putParcelable("dataKey", experiment);
                scannerFragment.setArguments(experimentInfo);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, scannerFragment);
                transaction.commit();
            }
        });

        goBack = view.findViewById(R.id.go_back_details);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle experimentInfo = new Bundle();
                ExperimentDetailsFragment experimentDetailsFragment = new ExperimentDetailsFragment();
                experimentInfo.putParcelable("dataKey", experiment);
                experimentDetailsFragment.setArguments(experimentInfo);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, experimentDetailsFragment);
                transaction.commit();
            }
        });

        /*
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle trialInfo = new Bundle();
                QRCodeFragment qrCodeFragment = new QRCodeFragment();
                Trial trial = trialList.get(position);

                trialInfo.putParcelable("dataKey", trial);
                qrCodeFragment.setArguments(trialInfo);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, qrCodeFragment);
                transaction.commit();

                }

            });

         */

        return view;
    }
}
