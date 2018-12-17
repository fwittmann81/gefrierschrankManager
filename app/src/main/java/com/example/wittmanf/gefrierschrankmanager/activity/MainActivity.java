package com.example.wittmanf.gefrierschrankmanager.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wittmanf.gefrierschrankmanager.Constants;
import com.example.wittmanf.gefrierschrankmanager.Item;
import com.example.wittmanf.gefrierschrankmanager.ItemListViewAdapter;
import com.example.wittmanf.gefrierschrankmanager.R;
import com.example.wittmanf.gefrierschrankmanager.SortDialog;
import com.example.wittmanf.gefrierschrankmanager.comparator.FachComparator;
import com.example.wittmanf.gefrierschrankmanager.comparator.KategorieComparator;
import com.example.wittmanf.gefrierschrankmanager.comparator.MaxHaltbarkeitComparator;
import com.example.wittmanf.gefrierschrankmanager.comparator.NameComparator;
import com.example.wittmanf.gefrierschrankmanager.notification.NotificationHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SortDialog.OnInputListener {
    private static final String TAG = "MainActivity";
    private ArrayList<Item> items = new ArrayList<>();
    private int selectedItemPostition = 0;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ItemListViewAdapter itemListViewAdapter;
    SharedPreferences sharedPreferences;
    public static String FREEZER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get FREEZER_ID to connect to correct database items
        sharedPreferences = getSharedPreferences("com.example.wittmanf.gefrierschrankmanager", Context.MODE_PRIVATE);
        FREEZER_ID = sharedPreferences.getString("freezerId", "-1");

        //create ListView for databaseItems
        ListView itemListView = findViewById(R.id.itemListView);
        itemListViewAdapter = new ItemListViewAdapter(this, R.layout.custom_listview_layout, items);
        itemListView.setAdapter(itemListViewAdapter);


        //if an item was selected to detail view
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemPostition = position;
                Intent intent = new Intent(MainActivity.this, ShowDetailsActivity.class);
                Item item = items.get(position);
                intent.putExtra("selectedItem", item);
                startActivity(intent);
            }
        });

        itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemPostition = position;

                final Item itemToDelete = items.get(position);

                //create Dialog to ask for deletion
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Essen auftauen")
                        .setMessage("Willst du " + itemToDelete.getName() + " wirklich auftauen?")
                        .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.child(FREEZER_ID).child("items").child(itemToDelete.getDatabaseKey()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MainActivity.this, itemToDelete.getName() + " wurde erfolgreich aufgetaut", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, itemToDelete.getName() + " konnte nicht aufgetaut werden", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Deletion of item failed: " + e.getMessage());
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Nein", null)
                        .show();
                return true;
            }
        });

        databaseReference.child(FREEZER_ID).child(Constants.DB_CHILD_ITEMS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Create for every snapshot a Item and put it to the custom Listview Adapter
                Item addedItem = Item.convertToItem(dataSnapshot);
                addedItem.setDatabaseKey(dataSnapshot.getKey());
                items.add(addedItem);
                itemListViewAdapter.notifyDataSetChanged();

                //Add Notification if maxFreezeDate was set
                if (!Constants.DEFAULT_MAX_FREEZE_DATE.equals(Constants.SDF.format(addedItem.getMaxFreezeDate())) && !addedItem.isExpDateShown()) {
                    NotificationHandler.setNextNotification(MainActivity.this, addedItem);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Create for every snapshot a Item and put it to the custom Listview Adapter
                Item changedItem = Item.convertToItem(dataSnapshot);
                items.remove(selectedItemPostition);
                items.add(selectedItemPostition, changedItem);
                itemListViewAdapter.notifyDataSetChanged();

                //Modify Notification if maxFreezeDate was set
                if (!Constants.DEFAULT_MAX_FREEZE_DATE.equals(changedItem.getMaxFreezeDate()) && !changedItem.isExpDateShown()) {
                    NotificationHandler.setNextNotification(MainActivity.this, changedItem);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                items.remove(selectedItemPostition);
                itemListViewAdapter.notifyDataSetChanged();

                //Delete Notification if maxFreezeDate was set
                Item itemToDelete = Item.convertToItem(dataSnapshot);
                if (!Constants.DEFAULT_MAX_FREEZE_DATE.equals(itemToDelete.getMaxFreezeDate())) {
                    NotificationHandler.deleteNotification(MainActivity.this, itemToDelete);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Create settings menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //If settings would be clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addItem) {
            Intent intent = new Intent(MainActivity.this, AddNewItemActivity.class);
            startActivityForResult(intent, Constants.REQUEST_ADD_ITEM);
        } else if (item.getItemId() == R.id.sortItems) {
            SortDialog sortDialog = new SortDialog();
            sortDialog.show(getSupportFragmentManager(), "sortItems");
        } else if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.logout) {
            sharedPreferences.edit().remove("freezerId").apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Case if an item was added
        if (requestCode == Constants.REQUEST_ADD_ITEM && resultCode == RESULT_OK && data != null) {
            Item newItem = (Item) data.getExtras().getSerializable("newItem");

            //Create a Map with key fields of database and its values.
            //The map can simply pushed to firebase DB.
            HashMap<String, String> itemData = new HashMap<>();
            itemData.put(Constants.DB_CHILD_NAME, newItem.getName());
            itemData.put(Constants.DB_CHILD_KATEGORIE, newItem.getKategorie().toString());
            itemData.put(Constants.DB_CHILD_CREATION_DATE, Constants.SDF.format(newItem.getCreationDate()));
            itemData.put(Constants.DB_CHILD_MAX_FREEZE_DATE, Constants.SDF.format(newItem.getMaxFreezeDate()));
            itemData.put(Constants.DB_CHILD_AMOUNT, String.valueOf(newItem.getAmount()));
            itemData.put(Constants.DB_CHILD_FACH, String.valueOf(newItem.getFach()));
            itemData.put(Constants.DB_CHILD_EINHEIT, newItem.getEinheit().toString());
            itemData.put(Constants.DB_CHILD_EXP_DATE_SHOWN, String.valueOf(newItem.isExpDateShown()));

            databaseReference.child(FREEZER_ID).child(Constants.DB_CHILD_ITEMS).push().setValue(itemData);
        }
    }

    //To get the sort options from the dialog, and sort the list view items
    @Override
    public void sendSortCriteria(String input) {
        switch (input) {
            case Constants.SORT_NAME:
                Collections.sort(items, new NameComparator());
                itemListViewAdapter.notifyDataSetChanged();
                break;
            case Constants.SORT_HALTBARKEIT:
                Collections.sort(items, new MaxHaltbarkeitComparator());
                itemListViewAdapter.notifyDataSetChanged();
                break;
            case Constants.SORT_KATEGORIE:
                Collections.sort(items, new KategorieComparator());
                itemListViewAdapter.notifyDataSetChanged();
                break;
            case Constants.SORT_FACH:
                Collections.sort(items, new FachComparator());
                itemListViewAdapter.notifyDataSetChanged();
                break;
            default:
        }
    }
}
