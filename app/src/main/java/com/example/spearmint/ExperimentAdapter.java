package com.example.spearmint;

/**
 * Custom adapter that processes and stores Experiment objects in a list
 * connects to content.xml file for the visual formatting of objects stored in the adapter
 * Abram Hindle, "Lab 3 instructions - CustomList", Public Domain, https://eclass.srv.ualberta.ca/pluginfile.php/6713985/mod_resource/content/1/Lab%203%20instructions%20-%20CustomList.pdf
 * @author Daniel and Andrew
 */

import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class ExperimentAdapter extends ArrayAdapter<Experiment> {
    private int resourceLayout;
    private ArrayList<Experiment> trials;
    private Context context;

    public ExperimentAdapter(Context context, int resource, ArrayList<Experiment> trials) {
        super(context, resource, trials);
        this.resourceLayout = resource;
        this.trials = trials;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.experiment_content, parent, false);
        }
        Experiment experiment = trials.get(position);

        TextView experimentOwner = view.findViewById(R.id.experiment_owner);
        TextView experimentDescription = view.findViewById(R.id.experiment_description);
        TextView experimentType = view.findViewById(R.id.experiment__trial_type);

        experimentOwner.setText("Owner: " + experiment.getExperimentOwner().get(1));
        experimentDescription.setText(experiment.getExperimentDescription());
        experimentType.setText(experiment.getTrialType());

        return view;
    }
}