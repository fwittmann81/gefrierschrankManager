package com.example.wittmanf.gefrierschrankmanager.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.wittmanf.gefrierschrankmanager.Constants;
import com.example.wittmanf.gefrierschrankmanager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private EditText freezerCodeET;
    private RadioGroup radioGroup;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("com.example.wittmanf.gefrierschrankmanager", MODE_PRIVATE);
        String freezerID = sharedPreferences.getString(Constants.SP_FREEZER_ID, "-1");

        if ("-1".equals(freezerID)) {
            createLoginPage();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void createLoginPage() {
        freezerCodeET = findViewById(R.id.freezerET);
        radioGroup = findViewById(R.id.loginGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.createNewFreezer == checkedId) {
                    freezerCodeET.setVisibility(View.INVISIBLE);
                } else {
                    freezerCodeET.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void login(View view) {
        boolean isValid = false;

        //connect to existing freezer
        if (R.id.connectToExistingFreezer == radioGroup.getCheckedRadioButtonId()) {
            if (freezerCodeET.getText().length() <= 0) {
                freezerCodeET.setError("Es wurde kein Code eingegeben");
            } else {
                checkConnection();
            }
        } else {
            //create new freezer
            String uniqueFreezerID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(Constants.SP_FREEZER_ID, uniqueFreezerID).apply();
            isValid = true;
        }

        if (isValid) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkConnection() {
        FirebaseDatabase.getInstance().getReference().child(freezerCodeET.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    sharedPreferences.edit().putString(Constants.SP_FREEZER_ID, freezerCodeET.getText().toString().trim()).apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "ID ist falsch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
