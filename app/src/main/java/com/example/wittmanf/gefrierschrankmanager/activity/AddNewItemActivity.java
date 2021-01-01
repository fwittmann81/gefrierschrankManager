package com.example.wittmanf.gefrierschrankmanager.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.wittmanf.gefrierschrankmanager.Constants;
import com.example.wittmanf.gefrierschrankmanager.Item;
import com.example.wittmanf.gefrierschrankmanager.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddNewItemActivity extends AppCompatActivity {

    private EditText nameET;
    private Spinner kategorienSpinner;
    private Spinner einheitenSpinner;
    private Spinner fachSpinner;
    private EditText maxFreezeDateET;
    private EditText amountET;

    private Calendar freezeDate = Calendar.getInstance();
    private Calendar expDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        String[] kategorien = getResources().getStringArray(R.array.kategorien);

        //initalize all text views
        nameET = findViewById(R.id.nameET);
        kategorienSpinner = findViewById(R.id.kategorieSpinner);
        einheitenSpinner = findViewById(R.id.einheitenSpinner);
        maxFreezeDateET = findViewById(R.id.maxFreezeDateET);
        amountET = findViewById(R.id.amountET);
        fachSpinner = findViewById(R.id.fachSpinner);

        //initalize kategorie spinner
        ArrayAdapter<String> myCategorieAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, kategorien);
        myCategorieAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        kategorienSpinner.setAdapter(myCategorieAdapter);

        //initalize fach spinner
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wittmanf.gefrierschrankmanager", MODE_PRIVATE);
        int countFach = sharedPreferences.getInt(Constants.SP_COUNT_FACH, 1);
        ArrayList<String> faecher = new ArrayList<>();
        for (int i = 1; i <= countFach; i++) {
            faecher.add(String.format(getResources().getString(R.string.item_edit_section_label), i));
        }

        ArrayAdapter<String> myFachAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, faecher);
        myFachAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        fachSpinner.setAdapter(myFachAdapter);

        //initalize einheiten spinner
        ArrayAdapter<String> myEinheitenAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.einheiten));
        myEinheitenAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        einheitenSpinner.setAdapter(myEinheitenAdapter);

        einheitenSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        String[] einheitenLabels = getResources().getStringArray(R.array.einheitenLabels);
                        amountET.setHint(einheitenLabels[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                }
        );

        //initalize date picker
        maxFreezeDateET = findViewById(R.id.maxFreezeDateET);
        maxFreezeDateET.setCursorVisible(false);
    }

    public void openDatePicker(View view) {
        Calendar date;
        DatePickerDialog.OnDateSetListener dateListener;
        date = freezeDate;
        dateListener = expDateListener;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                dateListener,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener expDateListener = (view, year, month, dayOfMonth) -> {
        expDate.set(year, month, dayOfMonth);

        ((EditText) findViewById(R.id.maxFreezeDateET)).setText(Constants.SDF.format(expDate.getTime()));
    };

    //Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //If edit would be clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveBTN) {
            save();
        }
        return super.onOptionsItemSelected(item);
    }

    public void save() {
        if (isValid()) {
            String name = nameET.getText().toString();
            String kategorie = kategorienSpinner.getSelectedItem().toString();

            Date maxFreezeDate = null;
            try {
                if (maxFreezeDateET.getText().length() > 0) {
                    maxFreezeDate = Constants.SDF.parse(maxFreezeDateET.getText().toString());
                } else {
                    maxFreezeDate = Constants.SDF.parse(Constants.DEFAULT_MAX_FREEZE_DATE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int amount = Integer.valueOf(amountET.getText().toString());
            int fach = fachSpinner.getSelectedItemPosition() + 1; //Starting at position 0
            String einheit = einheitenSpinner.getSelectedItem().toString();

            Item item = new Item(name, kategorie, maxFreezeDate, amount, fach, new Date(System.currentTimeMillis()), einheit);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("newItem", item);
            setResult(RESULT_OK, intent);

            finish();
        }
    }

    private boolean isValid() {
        if (nameET.getText().toString().length() <= 0) {
            nameET.setError("Bitte gib einen Namen ein");
            return false;
        }
        if (amountET.getText().toString().length() <= 0) {
            amountET.setError("Bitte gib eine Anzahl ein");
            return false;
        }
        return true;
    }
}
