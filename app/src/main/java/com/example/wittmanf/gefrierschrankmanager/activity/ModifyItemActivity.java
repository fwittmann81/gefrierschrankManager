package com.example.wittmanf.gefrierschrankmanager.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class ModifyItemActivity extends AppCompatActivity {

    private EditText nameET;
    private Spinner kategorienSpinner;
    private Spinner einheitenSpinner;
    private Spinner fachSpinner;
    private EditText maxFreezeDateET;
    private EditText amountET;
    private Item item;

    private Calendar freezeDate = Calendar.getInstance();
    private Calendar expDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_item);

        //initalize all text views
        nameET = findViewById(R.id.nameET);
        kategorienSpinner = findViewById(R.id.kategorieSpinner);
        einheitenSpinner = findViewById(R.id.einheitenSpinner);
        maxFreezeDateET = findViewById(R.id.maxFreezeDateET);
        amountET = findViewById(R.id.amountET);
        fachSpinner = findViewById(R.id.fachSpinner);

        //initalize kategorie spinner
        ArrayAdapter<String> myAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.kategorien));
        myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        kategorienSpinner.setAdapter(myAdapter);

        //initalize fach spinner
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wittmanf.gefrierschrankmanager", MODE_PRIVATE);
        int countFach = sharedPreferences.getInt("countFach", 1);
        ArrayList<String> faecher = new ArrayList<>();
        for (int i = 1; i <= countFach; i++) {
            faecher.add(String.format(getResources().getString(R.string.item_edit_section_label), i));
        }

        ArrayAdapter<String> myFachAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, faecher);
        myFachAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        fachSpinner.setAdapter(myFachAdapter);

        //initalize einheiten spinner
        ArrayAdapter<String> myEinheitenAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Einheiten));
        myEinheitenAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        einheitenSpinner.setAdapter(myEinheitenAdapter);

        einheitenSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String einheit = String.valueOf(adapterView.getItemAtPosition(i));
                        if (Item.EinheitenEnum.GRAMM.toString().equals(einheit))
                            amountET.setHint("Gewicht");
                        if (Item.EinheitenEnum.VOLUMEN.toString().equals(einheit))
                            amountET.setHint("Volumen");
                        if (Item.EinheitenEnum.STUECK.toString().equals(einheit))
                            amountET.setHint("Anzahl");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                }
        );

        //check if we are in edit-Mode or add-Mode
        Intent intent = getIntent();
        item = (Item) intent.getExtras().getSerializable("itemToModify");

        //fill fields with the values of item
        nameET.setText(item.getName());
        kategorienSpinner.setSelection(item.getKategorie().getPosition());
        einheitenSpinner.setSelection(item.getEinheit().getPosition());

        //only set max freeze date if it is not the default value 31.12.9999
        String maxFreezeDate = Constants.SDF.format(item.getMaxFreezeDate());
        if (!maxFreezeDate.equals(Constants.DEFAULT_MAX_FREEZE_DATE)) {
            maxFreezeDateET.setText(maxFreezeDate);
        }
        amountET.setText(String.valueOf(item.getAmount()));

        fachSpinner.setSelection(item.getFach() - 1);

        //initalize date picker
        maxFreezeDateET = findViewById(R.id.maxFreezeDateET);
        maxFreezeDateET.setCursorVisible(false);
    }

    public void openDatePicker(View view) {
        Calendar date;
        DatePickerDialog.OnDateSetListener dateListener;
        long minDate;
        date = freezeDate;
        dateListener = expDateListener;
        minDate = new Date().getTime();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                dateListener,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(minDate);
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
        String name = nameET.getText().toString();
        Item.KategorieEnum kategorie = Item.KategorieEnum.get(kategorienSpinner.getSelectedItem().toString());
        Item.EinheitenEnum einheit = Item.EinheitenEnum.get(einheitenSpinner.getSelectedItem().toString());

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

        this.item.setName(name);
        this.item.setKategorie(kategorie);
        this.item.setMaxFreezeDate(maxFreezeDate);
        this.item.setAmount(amount);
        this.item.setFach(fach);
        this.item.setEinheit(einheit);
        this.item.setExpDateShown(false);

        Intent intent = new Intent(this, ShowDetailsActivity.class);
        intent.putExtra("selectedItem", this.item);
        setResult(RESULT_OK, intent);

        finish();
    }
}
