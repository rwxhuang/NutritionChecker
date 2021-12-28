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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EditFragment extends Fragment {
    double age, weight, height, exercise;
    String sex;

    EditText ageInput;
    EditText heightInput;
    EditText weightInput;
    EditText exerciseInput;

    Spinner mySpinner;
    ArrayAdapter<String> myAdapter;

    String ageSave, heightSave, weightSave, exerciseSave, sexSave;

    Button saveButton;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String AGETEXT = "age";
    public static final String HEIGHTTEXT = "height";
    public static final String WEIGHTTEXT = "weight";
    public static final String EXERCISETEXT = "exercise";
    public static final String SEXTEXT = "sex";
    public static final String CALORIETEXT = "calorie";

    private String ageT = "";
    private String heightT = "";
    private String weightT = "";
    private String exerciseT = "";
    private String sexT = "";
    private String calorieT = "";

    private SharedPreferences sp;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit, container, false);

        ageSave = "";
        heightSave = "";
        weightSave = "";
        exerciseSave = "";
        sexSave = "";

        mySpinner = (Spinner) v.findViewById(R.id.spinnerSex);

        myAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sex));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        ageInput = (EditText) v.findViewById(R.id.ageInput);
        heightInput = (EditText) v.findViewById(R.id.heightInput);
        weightInput = (EditText) v.findViewById(R.id.weightInput);
        exerciseInput = (EditText) v.findViewById(R.id.exerciseInput);

        sp = getContext().getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);

        loadData();

        saveButton = (Button) v.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean formCompleted = true;

                // NumberFormatException thrown if fields are left empty and submit is pressed
                try {
                    age = Double.parseDouble(ageInput.getText().toString());
                    ageT = (int)age + "";
                    height = Double.parseDouble(heightInput.getText().toString());
                    heightT = (int)height + "";
                    weight = Double.parseDouble(weightInput.getText().toString());
                    weightT = (int)weight + "";
                    exercise = Double.parseDouble(exerciseInput.getText().toString());
                    exerciseT = (int)exercise + "";
                    sex = mySpinner.getSelectedItem().toString();
                    sexT = sex;
                    //= mySpinner.getSelectedItemPosition();

                    double index = 1.2;
                    if (exercise >= 10 && exercise < 30) index = 1.375;
                    else if (exercise >= 30 && exercise < 60) index = 1.55;
                    else if (exercise >= 60 && exercise < 90) index = 1.725;
                    else if (exercise >= 120) index = 1.9;

                    if (sex.equals("Male"))
                        calorieT = (int) (index * (66 + 6.3 * weight + 12.9 * height - 6.8 * age)) + "";
                    else
                        calorieT = (int) (index * (655 + 4.3 * weight + 4.7 * height - 4.7 * age)) + "";
                }
                catch(NumberFormatException e) {
                    Toast.makeText(getContext(), "Please fill out the form before submitting", Toast.LENGTH_SHORT).show();
                    formCompleted = false;
                }

                // sex spinner must be selected as either male or female
                if(mySpinner.getSelectedItem().toString().equals("Select sex")) {
                    Toast.makeText(getContext(), "Please select your sex", Toast.LENGTH_SHORT).show();
                    formCompleted = false;
                }

                if(formCompleted) {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
                    edit.commit();
                    boolean previouslyStarted = sp.getBoolean(getString(R.string.pref_previously_started), false);
                    Log.d("TESTING_FORM", previouslyStarted + "");

                    saveData();

                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                }
            }
        });

        return v;
    }

    public void saveData() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AGETEXT, ageT);
        editor.putString(HEIGHTTEXT, heightT);
        editor.putString(WEIGHTTEXT, weightT);
        editor.putString(EXERCISETEXT , exerciseT);
        editor.putString(SEXTEXT, sexT);
        editor.putString(CALORIETEXT, calorieT);
        editor.apply();
    }

    public void loadData() {
        ageSave = sp.getString(AGETEXT, "");
        heightSave = sp.getString(HEIGHTTEXT, "");
        weightSave = sp.getString(WEIGHTTEXT, "");
        exerciseSave = sp.getString(EXERCISETEXT, "");
        sexSave = sp.getString(SEXTEXT, "");

        ageInput.setText(ageSave);
        heightInput.setText(heightSave);
        weightInput.setText(weightSave);
        exerciseInput.setText(exerciseSave);

        if(sexSave.equals("Male"))
            mySpinner.setSelection(myAdapter.getPosition("Male"));
        else
            mySpinner.setSelection(myAdapter.getPosition("Female"));
    }
}
