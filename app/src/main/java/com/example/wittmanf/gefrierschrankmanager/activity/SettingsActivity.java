package com.example.wittmanf.gefrierschrankmanager.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;

import com.example.wittmanf.gefrierschrankmanager.Constants;
import com.example.wittmanf.gefrierschrankmanager.R;
import com.example.wittmanf.gefrierschrankmanager.widget.AlertDialog;

public class SettingsActivity extends AppCompatActivity implements AlertDialog.OnInputListener {
    TextInputEditText alertET, freezerID, countFach;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //saved value from the user
        sharedPreferences = getSharedPreferences("com.example.wittmanf.gefrierschrankmanager", MODE_PRIVATE);
        String alertTime = sharedPreferences.getString(Constants.SP_ALERT_TIME, Constants.ALERT_ONE_WEEK);

        freezerID = findViewById(R.id.freezerIdTV);
        freezerID.setText(MainActivity.FREEZER_ID);
        freezerID.setInputType(InputType.TYPE_NULL);
        freezerID.setTextIsSelectable(true);
        freezerID.setKeyListener(null);

        countFach = findViewById(R.id.countfachTV);
        countFach.setText(String.valueOf(sharedPreferences.getInt(Constants.SP_COUNT_FACH, 1)));

        alertET = findViewById(R.id.alertET);
        alertET.setText(alertTime);
        alertET.setCursorVisible(false);
        alertET.setInputType(InputType.TYPE_NULL);

        alertET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog();
                alertDialog.show(getSupportFragmentManager(), "alertItems");
            }
        });
    }

    @Override
    public void sendAlertCriteria(String input) {
        switch (input) {
            case Constants.ALERT_THREE_DAYS:
                alertET.setText(Constants.ALERT_THREE_DAYS);
                sharedPreferences.edit().putString(Constants.SP_ALERT_TIME, Constants.ALERT_THREE_DAYS).apply();
                break;
            case Constants.ALERT_ONE_WEEK:
                alertET.setText(Constants.ALERT_ONE_WEEK);
                sharedPreferences.edit().putString(Constants.SP_ALERT_TIME, Constants.ALERT_ONE_WEEK).apply();
                break;
            case Constants.ALERT_TWO_WEEKS:
                alertET.setText(Constants.ALERT_TWO_WEEKS);
                sharedPreferences.edit().putString(Constants.SP_ALERT_TIME, Constants.ALERT_TWO_WEEKS).apply();
                break;
            case Constants.ALERT_THREE_WEEKS:
                alertET.setText(Constants.ALERT_THREE_WEEKS);
                sharedPreferences.edit().putString(Constants.SP_ALERT_TIME, Constants.ALERT_THREE_WEEKS).apply();
                break;
            case Constants.ALERT_FOUR_WEEKS:
                alertET.setText(Constants.ALERT_FOUR_WEEKS);
                sharedPreferences.edit().putString(Constants.SP_ALERT_TIME, Constants.ALERT_FOUR_WEEKS).apply();
                break;
            case Constants.ALERT_NEVER:
                alertET.setText(Constants.ALERT_NEVER);
                sharedPreferences.edit().putString(Constants.SP_ALERT_TIME, Constants.ALERT_NEVER).apply();
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        if (countFach.getText() == null || countFach.getText().length() <= 0) {
            countFach.setError("Bitte die Anzahl der Fächer eingeben");
        } else {
            super.onBackPressed();
            sharedPreferences.edit().putInt(Constants.SP_COUNT_FACH, Integer.valueOf(countFach.getText().toString())).apply();
        }
    }
}
