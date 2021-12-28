package com.nutrition.checker;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    TextView ageView;
    TextView heightView;
    TextView weightView;
    TextView exerciseView;
    TextView sexView;
    TextView calorieView;

    String ageSave, heightSave, weightSave, exerciseSave, sexSave, calorieSave;

    Button editButton;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String AGETEXT = "age";
    public static final String HEIGHTTEXT = "height";
    public static final String WEIGHTTEXT = "weight";
    public static final String EXERCISETEXT = "exercise";
    public static final String SEXTEXT = "sex";
    public static final String CALORIETEXT = "calorie";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ageView = v.findViewById(R.id.ageView);
        heightView = v.findViewById(R.id.heightView);
        weightView = v.findViewById(R.id.weightView);
        exerciseView = v.findViewById(R.id.exerciseView);
        sexView = v.findViewById(R.id.sexView);
        calorieView = v.findViewById(R.id.calorieView);

        loadData();

        editButton = (Button) v.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new EditFragment()).commit();
            }
        });

        return v;
    }

    public void loadData() {
        SharedPreferences sp = getContext().getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        ageSave = sp.getString(AGETEXT, "");
        heightSave = sp.getString(HEIGHTTEXT, "");
        weightSave = sp.getString(WEIGHTTEXT, "");
        exerciseSave = sp.getString(EXERCISETEXT, "");
        sexSave = sp.getString(SEXTEXT, "");
        calorieSave = sp.getString(CALORIETEXT, "");

        ageView.setText(ageSave);
        heightView.setText(heightSave + " in");
        weightView.setText(weightSave + " lbs");
        exerciseView.setText(exerciseSave + " min");
        sexView.setText(sexSave);
        calorieView.setText(calorieSave);
    }
}
