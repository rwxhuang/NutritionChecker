package com.nutrition.checker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FormActivity extends AppCompatActivity {
    double age, weight, height, exercise;
    String sex;

    EditText ageInput;
    EditText heightInput;
    EditText weightInput;
    EditText exerciseInput;
    Spinner mySpinner;

    String ageSave, heightSave, weightSave, exerciseSave, sexSave;

    Button submitButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        ageSave = "";
        heightSave = "";
        weightSave = "";
        exerciseSave = "";
        sexSave = "";

        mySpinner = (Spinner) findViewById(R.id.spinnerSex);

        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sex));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        ageInput = (EditText) findViewById(R.id.ageInput);
        heightInput = (EditText) findViewById(R.id.heightInput);
        weightInput = (EditText) findViewById(R.id.weightInput);
        exerciseInput = (EditText) findViewById(R.id.exerciseInput);

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(getApplicationContext(), "Please fill out the form before submitting", Toast.LENGTH_SHORT).show();
                    formCompleted = false;
                }

                // sex spinner must be selected as either male or female
                Spinner sexSpinner = (Spinner) findViewById(R.id.spinnerSex);
                if(sexSpinner.getSelectedItem().toString().equals("Select sex")) {
                    Toast.makeText(getApplicationContext(), "Please select your sex", Toast.LENGTH_SHORT).show();
                    formCompleted = false;
                }

                if(formCompleted) {
                    SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
                    edit.commit();
                    boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
                    Log.d("TESTING_FORM", previouslyStarted + "");

                    saveData();

                    Intent intent = new Intent(FormActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void saveData() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
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
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        ageSave = sp.getString(AGETEXT, "");
        heightSave = sp.getString(HEIGHTTEXT, "");
        weightSave = sp.getString(WEIGHTTEXT, "");
        exerciseSave = sp.getString(EXERCISETEXT, "");
        sexSave = sp.getString(SEXTEXT, "");
    }

    public void updateViews() {
        /*
        int i = str.indexOf("S0D1UM")+6;
        while(!((int)(str.charAt(i)) >= 65 && (int)(str.charAt(i)) <= 90) && i < str.length())
            i++;
        String letter = "" + str.charAt(i);
        sodium = Double.parseDouble(str.substring(str.indexOf("S0D1UM")+6, str.indexOf(letter, i)));*/
        try {
            ageInput.setText("Saved Age" + ageSave);
            heightInput.setText("Saved Height" + heightSave);
            weightInput.setText("Saved Weight" + weightSave);
            exerciseInput.setText("Saved Exercise" + exerciseSave);
            //mySpinner.setSelection(Integer.parseInt(tot.substring(tot.length()-2, tot.length())));
        }catch(Exception e)
        {
        }
        //heightInput.setText(tot.substring())
        //ageInput.setText(tot);
    }
}