package com.example.wittmanf.gefrierschrankmanager;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public class Constants {
    public static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
    public static final int REQUEST_ADD_ITEM = 1;
    public static final int REQUEST_MODIFY_ITEM = 2;
    public static final String DEFAULT_MAX_FREEZE_DATE = "31.12.9999";

    public static final String SORT_NAME = "Name";
    public static final String SORT_KATEGORIE = "Kategorie";
    public static final String SORT_HALTBARKEIT = "Haltbarkeit";
    public static final String SORT_FACH = "Fach";

    public static final String ALERT_THREE_DAYS = "3 Tage";
    public static final String ALERT_ONE_WEEK = "1 Woche";
    public static final String ALERT_TWO_WEEKS = "2 Wochen";
    public static final String ALERT_THREE_WEEKS = "3 Wochen";
    public static final String ALERT_FOUR_WEEKS = "4 Wochen";
    public static final String ALERT_NEVER = "Nie";

    public static final HashMap<String, Integer> ALERTIME_DAYS_MAPPING = new HashMap<>();

    static {
        ALERTIME_DAYS_MAPPING.put(ALERT_THREE_DAYS, 3);
        ALERTIME_DAYS_MAPPING.put(ALERT_ONE_WEEK, 7);
        ALERTIME_DAYS_MAPPING.put(ALERT_TWO_WEEKS, 14);
        ALERTIME_DAYS_MAPPING.put(ALERT_THREE_WEEKS, 21);
        ALERTIME_DAYS_MAPPING.put(ALERT_FOUR_WEEKS, 28);
    }

    public static final String DB_CHILD_FREEZER = "freezer";
    public static final String DB_CHILD_ITEMS = "items";
    public static final String DB_CHILD_EXP_DATE_SHOWN = "expDateShown";
    public static final String DB_CHILD_NAME = "name";
    public static final String DB_CHILD_AMOUNT = "amount";
    public static final String DB_CHILD_CREATION_DATE = "creationDate";
    public static final String DB_CHILD_EINHEIT = "einheit";
    public static final String DB_CHILD_FACH = "fach";
    public static final String DB_CHILD_KATEGORIE = "kategorie";
    public static final String DB_CHILD_MAX_FREEZE_DATE = "maxFreezeDate";
}
