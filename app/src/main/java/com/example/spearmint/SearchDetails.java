package com.example.spearmint;

/**
 * Fragment with clickable elements that displays the experiment title and allows users to view posts, make a post, and respond to posts
 * Currently has a small bug where the experiment title disappears after responding to a post or after "cancelling" a post
 * Connects to firebase and sends information about posts to ResponseFragment.java
 * Unused class, but will be implemented at a later date
 * @author Andrew
 *
 * firebase implementation is from ...
 * Tanzil Shahriar, "Lab 5 Firestore Integration Instructions", https://eclass.srv.ualberta.ca/pluginfile.php/6714046/mod_resource/content/0/Lab%205%20Firestore%20Integration%20Instructions.pdf
 */

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SearchDetails extends Fragment {

    private static final String SHARED_PREFS = "SharedPrefs";
    private static final String TEXT = "Text";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Button goBack;
        Button subscribe;
        TextView exDescription;
        TextView exRegion;
        TextView exCount;
        TextView exOwner;
        TextView exLocation;
        TextView exType;
        TextView exStatus;
        FirebaseFirestore db;
        ArrayList<String> userInfo = new ArrayList<>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString(TEXT, null);

        db = FirebaseFirestore.getInstance();

        View view = inflater.inflate(R.layout.search_details, container, false);

        Experiment experiment = getArguments().getParcelable("dataKey");

        // Setting the EditText to the experiment title from ExperimentFragment.java
        exDescription = view.findViewById(R.id.experiment_name);
        exRegion = view.findViewById(R.id.experiment__trial_type);
        exCount = view.findViewById(R.id.experiment_count);
        exOwner = view.findViewById(R.id.experiment_username);
        exLocation = view.findViewById(R.id.experiment_location);
        exType = view.findViewById(R.id.experiment_type);
        exStatus = view.findViewById(R.id.experiment_status);

        String description = experiment.getExperimentDescription();
        String region = experiment.getExperimentRegion();
        String count = experiment.getExperimentCount();
        String ownerID = experiment.getExperimentOwner().get(0);
        String owner = experiment.getExperimentOwner().get(1);
        String location = experiment.getGeoLocation();
        String type = experiment.getTrialType();
        String status = experiment.getStatus();

        exDescription.setText("Title: " + description);
        exRegion.setText("City: " + region);
        exCount.setText("Minimum Trials: " + count);
        exOwner.setText("Owner: " + owner);
        exLocation.setText("Requires Location: " + location);
        exType.setText("Trial Type: " + type);
        exStatus.setText("Status: " + status);

        //final CollectionReference collectionReference = db.collection("Experiments");
        final CollectionReference collectionReferenceUser = db.collection("User");
        DocumentReference documentReference = collectionReferenceUser.document(ownerID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String phoneNumber = value.getString("PhoneNum");
                String email = value.getString("Email");
                userInfo.add(phoneNumber);
                userInfo.add(email);
            }
        });

        exOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle(owner);
                alert.setMessage(userInfo.get(0) + "\n" + userInfo.get(1));
                alert.show();
            }
        });

        // ArrayList<Question> questionList = new ArrayList<>();

       // QuestionAdapter customAdapter = new QuestionAdapter(getActivity(), R.layout.question_content, questionList);

        /**
         * Updates the list stored locally in the app with Firebase data to display the data

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                questionList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {

                    String question = doc.getId();
                    String answer = (String) doc.get("Answer");

                    questionList.add(new Question(question, answer));
                }
                customAdapter.notifyDataSetChanged();
            }
        });*/

        /**
         * Redirects user to the search fragment "SearchFragment" through "back" button
         */
        goBack = view.findViewById(R.id.back_button);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment = new SearchFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                transaction.replace(R.id.nav_host_fragment, searchFragment);
                transaction.commit();
            }
        });

        /**
         * https://www.youtube.com/watch?v=EFwHUBbbI-U
         * https://www.youtube.com/watch?v=men8GB-7yM0
         */


        subscribe = view.findViewById(R.id.subscribe);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionReferenceUser
                        .document(uniqueID).collection("subscribedExperiments").document(experiment.getExperimentDescription())
                        .set(experiment);
            }
        });


        /**
         * With Sam. (2019, Jun 21). Drop down menu / Spinner - Android Studio Latest Version [Video]. YouTube. https://www.youtube.com/watch?v=GmXH8wCPEnQ
         */

        /*
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), parent.getItemAtPosition(position) + " Selected", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        endExperiment = view.findViewById(R.id.endExperiment);
        endExperiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment = new SearchFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                transaction.replace(R.id.nav_host_fragment, searchFragment);
                transaction.commit();
            }
        });

        trial = view.findViewById(R.id.trial);
        trial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExperimentCount experimentCount = new ExperimentCount();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                transaction.replace(R.id.nav_host_fragment, experimentCount);
                transaction.commit();
            }
        });

        binomial = view.findViewById(R.id.binomial);
        binomial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BinomialFragment binomialFragment = new BinomialFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                transaction.replace(R.id.nav_host_fragment, binomialFragment);
                transaction.commit();
            }
        });

         */


        return view;
    }


}
