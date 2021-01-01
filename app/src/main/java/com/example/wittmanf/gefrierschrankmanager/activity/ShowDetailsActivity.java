package com.example.wittmanf.gefrierschrankmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wittmanf.gefrierschrankmanager.Constants;
import com.example.wittmanf.gefrierschrankmanager.Item;
import com.example.wittmanf.gefrierschrankmanager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ShowDetailsActivity extends AppCompatActivity {

    private final static String TAG = "ShowDetailsActivity";
    private TextView nameTV;
    private TextView kategorieTV;
    private TextView creationDateTV;
    private TextView maxFreezeDateTV;
    private TextView amountTV;
    private TextView fachTV;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        //initialize Views
        nameTV = findViewById(R.id.nameValueTV);
        kategorieTV = findViewById(R.id.kategorieValueTV);
        creationDateTV = findViewById(R.id.creationDateValueTV);
        maxFreezeDateTV = findViewById(R.id.maxFreezeDateValueTV);
        amountTV = findViewById(R.id.amountValueTV);
        fachTV = findViewById(R.id.fachValueTV);

        Intent intent = getIntent();
        item = (Item) intent.getExtras().getSerializable("selectedItem");

        setValues();
    }

    private void setValues() {
        nameTV.setText(item.getName());
        kategorieTV.setText(item.getKategorie());
        creationDateTV.setText(Constants.SDF.format(item.getCreationDate()));

        //only set max freeze date if it is not the default value 31.12.9999
        String maxFreezeDate = Constants.SDF.format(item.getMaxFreezeDate());
        if (!maxFreezeDate.equals(Constants.DEFAULT_MAX_FREEZE_DATE)) {
            maxFreezeDateTV.setText(maxFreezeDate);
        } else {
            maxFreezeDateTV.setText("");
        }
        amountTV.setText(String.valueOf(item.getAmount()) + " " + item.getEinheit());
        fachTV.setText(String.valueOf(item.getFach()));
    }

    //Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //If edit would be clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editBTN) {
            Intent modifyIntent = new Intent(ShowDetailsActivity.this, ModifyItemActivity.class);
            modifyIntent.putExtra("itemToModify", this.item);
            startActivityForResult(modifyIntent, Constants.REQUEST_MODIFY_ITEM);
        }
        return super.onOptionsItemSelected(item);
    }

    //Return of modify activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.REQUEST_MODIFY_ITEM && resultCode == RESULT_OK && data != null) {
            final Item modifiedItem = (Item) data.getExtras().getSerializable("selectedItem");

            //override current item to get all changes
            ShowDetailsActivity.this.item = modifiedItem;

            HashMap<String, String> itemData = new HashMap<>();
            itemData.put(Constants.DB_CHILD_NAME, modifiedItem.getName());
            itemData.put(Constants.DB_CHILD_KATEGORIE, modifiedItem.getKategorie());
            itemData.put(Constants.DB_CHILD_CREATION_DATE, Constants.SDF.format(modifiedItem.getCreationDate()));
            itemData.put(Constants.DB_CHILD_MAX_FREEZE_DATE, Constants.SDF.format(modifiedItem.getMaxFreezeDate()));
            itemData.put(Constants.DB_CHILD_AMOUNT, String.valueOf(modifiedItem.getAmount()));
            itemData.put(Constants.DB_CHILD_FACH, String.valueOf(modifiedItem.getFach()));
            itemData.put(Constants.DB_CHILD_EINHEIT, String.valueOf(modifiedItem.getEinheit()));
            itemData.put(Constants.DB_CHILD_EXP_DATE_SHOWN, String.valueOf(modifiedItem.isExpDateShown()));

            final HashMap<String, Object> updateData = new HashMap<>();
            updateData.put("/" + MainActivity.FREEZER_ID + "/items/" + this.item.getDatabaseKey(), itemData);

            FirebaseDatabase.getInstance().getReference().updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ShowDetailsActivity.this, "Update erfolgreich", Toast.LENGTH_SHORT).show();
                    setValues();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ShowDetailsActivity.this, "Update fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Update failed: " + e.getMessage());
                }
            });
        }
    }
}
