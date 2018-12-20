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
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wittmanf.gefrierschrankmanager.Constants;
import com.example.wittmanf.gefrierschrankmanager.Item;
import com.example.wittmanf.gefrierschrankmanager.R;
import com.example.wittmanf.gefrierschrankmanager.comparator.FachComparator;
import com.example.wittmanf.gefrierschrankmanager.comparator.KategorieComparator;
import com.example.wittmanf.gefrierschrankmanager.comparator.MaxHaltbarkeitComparator;
import com.example.wittmanf.gefrierschrankmanager.comparator.NameComparator;
import com.example.wittmanf.gefrierschrankmanager.notification.NotificationHandler;
import com.example.wittmanf.gefrierschrankmanager.widget.ItemListViewAdapter;
import com.example.wittmanf.gefrierschrankmanager.widget.SortDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SortDialog.OnInputListener {
    private static final String TAG = "MainActivity";
    private ArrayList<Item> allItems = new ArrayList<>();
    private int selectedItemPostition = 0;
    private Item selectedItem;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ItemListViewAdapter itemListViewAdapter;
    SharedPreferences sharedPreferences;
    public static String FREEZER_ID;
    String[] kategorien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get FREEZER_ID to connect to correct database allItems
        sharedPreferences = getSharedPreferences("com.example.wittmanf.gefrierschrankmanager", Context.MODE_PRIVATE);
        FREEZER_ID = sharedPreferences.getString(Constants.SP_FREEZER_ID, "-1");

        //create ListView for databaseItems
        ListView itemListView = findViewById(R.id.itemListView);
        itemListViewAdapter = new ItemListViewAdapter(this, R.layout.custom_listview_layout, allItems);
        itemListView.setAdapter(itemListViewAdapter);

        kategorien = getResources().getStringArray(R.array.kategorien);

        //if an item was selected to detail view
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemPostition = position;
                Intent intent = new Intent(MainActivity.this, ShowDetailsActivity.class);
                selectedItem = (Item) itemListViewAdapter.getItem(position);
                intent.putExtra("selectedItem", selectedItem);
                startActivity(intent);
            }
        });

        itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemPostition = position;

                final Item itemToDelete = (Item) itemListViewAdapter.getItem(position);

                //create Dialog to ask for deletion
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Essen auftauen")
                        .setMessage("Willst du " + itemToDelete.getName() + " wirklich auftauen?")
                        .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.child(FREEZER_ID).child(Constants.DB_CHILD_ITEMS).child(itemToDelete.getDatabaseKey()).removeValue()
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
                itemListViewAdapter.add(addedItem);

                //Add Notification if maxFreezeDate was set
                if (!Constants.DEFAULT_MAX_FREEZE_DATE.equals(Constants.SDF.format(addedItem.getMaxFreezeDate())) && !addedItem.isExpDateShown()) {
                    NotificationHandler.setNextNotification(MainActivity.this, addedItem);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Create for every snapshot a Item and put it to the custom Listview Adapter
                Item changedItem = Item.convertToItem(dataSnapshot);
                itemListViewAdapter.remove(selectedItem);
                itemListViewAdapter.insert(changedItem, selectedItemPostition);

                //Modify Notification if maxFreezeDate was set
                if (!Constants.DEFAULT_MAX_FREEZE_DATE.equals(changedItem.getMaxFreezeDate()) && !changedItem.isExpDateShown()) {
                    NotificationHandler.setNextNotification(MainActivity.this, changedItem);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Item itemToDelete = Item.convertToItem(dataSnapshot);
                itemListViewAdapter.remove(itemToDelete);

                //Delete Notification if maxFreezeDate was set
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

        MenuItem filterKategorie = menu.findItem(R.id.filter_kategorie);
        SubMenu kategorieSubMenu = filterKategorie.getSubMenu();
        MenuItem filterFach = menu.findItem(R.id.filter_fach);
        SubMenu fachSubMenu = filterFach.getSubMenu();
        int count = 1;

        //initialize Kategorie filter
        String[] kategorien = getResources().getStringArray(R.array.kategorien);
        for (String kategorie : kategorien) {
            kategorieSubMenu.add(0, count, count, kategorie);
            count++;
        }

        //initialize Fach filter
        int countFach = sharedPreferences.getInt(Constants.SP_COUNT_FACH, 1);
        for (int i = 0; i < countFach; i++) {
            fachSubMenu.add(1, count, i, String.format(getResources().getString(R.string.item_edit_section_label), i + 1));
        }

        return super.onCreateOptionsMenu(menu);
    }

    //If a menu item would be clicked
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
            sharedPreferences.edit().remove(Constants.SP_FREEZER_ID).apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (Arrays.asList(kategorien).indexOf(item.getTitle().toString()) != -1){
            itemListViewAdapter.getFilter().filter(item.getTitle().toString());
        } else if (item.getTitle().toString().contains("Fach ")) {
            filterFach(item.getTitle().toString());
        } else if (item.getItemId() == R.id.resetFilter) {
            itemListViewAdapter.getFilter().filter(null);
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterFach(String fach) {
        //fach is like "Fach 1". Only the number is needed to filter items
        String[] split = fach.split(" ");
        itemListViewAdapter.getFilter().filter(split[1]);
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
            itemData.put(Constants.DB_CHILD_KATEGORIE, newItem.getKategorie());
            itemData.put(Constants.DB_CHILD_CREATION_DATE, Constants.SDF.format(newItem.getCreationDate()));
            itemData.put(Constants.DB_CHILD_MAX_FREEZE_DATE, Constants.SDF.format(newItem.getMaxFreezeDate()));
            itemData.put(Constants.DB_CHILD_AMOUNT, String.valueOf(newItem.getAmount()));
            itemData.put(Constants.DB_CHILD_FACH, String.valueOf(newItem.getFach()));
            itemData.put(Constants.DB_CHILD_EINHEIT, newItem.getEinheit());
            itemData.put(Constants.DB_CHILD_EXP_DATE_SHOWN, String.valueOf(newItem.isExpDateShown()));

            databaseReference.child(FREEZER_ID).child(Constants.DB_CHILD_ITEMS).push().setValue(itemData);
        }
    }

    //To get the sort options from the dialog, and sort the list view allItems
    @Override
    public void sendSortCriteria(String input) {
        switch (input) {
            case Constants.SORT_NAME:
                itemListViewAdapter.sort(new NameComparator());
                break;
            case Constants.SORT_HALTBARKEIT:
                itemListViewAdapter.sort(new MaxHaltbarkeitComparator());
                break;
            case Constants.SORT_KATEGORIE:
                itemListViewAdapter.sort(new KategorieComparator());
                break;
            case Constants.SORT_FACH:
                itemListViewAdapter.sort(new FachComparator());
                break;
            default:
        }
    }
}
