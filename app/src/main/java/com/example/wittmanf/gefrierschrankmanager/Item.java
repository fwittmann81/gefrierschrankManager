package com.example.wittmanf.gefrierschrankmanager;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public class Item implements Serializable {
    private String databaseKey;
    private String name;
    private String kategorie;
    private Date creationDate;
    private Date maxFreezeDate;
    private int amount;
    private String einheit;
    private int fach;
    private boolean expDateShown = false;

    public Item(String name, String kategorie, Date maxFreezeDate, int amount, int fach, Date creationDate, String einheit) {
        this.name = name;
        this.kategorie = kategorie;
        this.maxFreezeDate = maxFreezeDate;
        this.amount = amount;
        this.fach = fach;
        this.creationDate = creationDate;
        this.einheit = einheit;
    }

    public boolean isExpDateShown() {
        return expDateShown;
    }

    public void setExpDateShown(boolean expDateShown) {
        this.expDateShown = expDateShown;
    }

    public String getDatabaseKey() {
        return databaseKey;
    }

    public void setDatabaseKey(String databaseKey) {
        this.databaseKey = databaseKey;
    }

    public String getEinheit() {
        return einheit;
    }

    public void setEinheit(String einheit) {
        this.einheit = einheit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getMaxFreezeDate() {
        return maxFreezeDate;
    }

    public void setMaxFreezeDate(Date maxFreezeDate) {
        this.maxFreezeDate = maxFreezeDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getFach() {
        return fach;
    }

    public void setFach(int fach) {
        this.fach = fach;
    }

    public static Item convertToItem(DataSnapshot dataSnapshot) {
        String name = dataSnapshot.child("name").getValue().toString();
        String kategorie = dataSnapshot.child("kategorie").getValue().toString();
        Date creationDate = null;
        Date maxFreezeDate = null;
        try {
            creationDate = Constants.SDF.parse(dataSnapshot.child("creationDate").getValue().toString());
            maxFreezeDate = Constants.SDF.parse(dataSnapshot.child("maxFreezeDate").getValue().toString());
        } catch (ParseException e) {
            //should never occur
            e.printStackTrace();
        }

        int amount = Integer.valueOf(dataSnapshot.child("amount").getValue().toString());
        int fach = Integer.valueOf(dataSnapshot.child("fach").getValue().toString());
        String einheit = dataSnapshot.child("einheit").getValue().toString();

        Item newItem = new Item(name, kategorie, maxFreezeDate, amount, fach, creationDate, einheit);
        newItem.setDatabaseKey(dataSnapshot.getKey());
        newItem.setExpDateShown(Boolean.valueOf(dataSnapshot.child("expDateShown").getValue().toString()));
        return newItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return amount == item.amount &&
                fach == item.fach &&
                expDateShown == item.expDateShown &&
                Objects.equals(databaseKey, item.databaseKey) &&
                Objects.equals(name, item.name) &&
                kategorie == item.kategorie &&
                Objects.equals(creationDate, item.creationDate) &&
                Objects.equals(maxFreezeDate, item.maxFreezeDate) &&
                einheit == item.einheit;
    }

    @Override
    public String toString() {
        return "Item{" +
                "databaseKey='" + databaseKey + '\'' +
                ", name='" + name + '\'' +
                ", kategorie=" + kategorie +
                ", creationDate=" + creationDate +
                ", maxFreezeDate=" + maxFreezeDate +
                ", amount=" + amount +
                ", einheit=" + einheit +
                ", fach=" + fach +
                ", expDateShown=" + expDateShown +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseKey, name, kategorie, creationDate, maxFreezeDate, amount, einheit, fach, expDateShown);
    }
}
