package com.example.wittmanf.gefrierschrankmanager.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.wittmanf.gefrierschrankmanager.Constants;
import com.example.wittmanf.gefrierschrankmanager.Item;
import com.example.wittmanf.gefrierschrankmanager.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * You can use this adapter to provide views for an {@link AdapterView},
 * Returns a view for each object in a collection of data objects you
 * provide, and can be used with list-based user interface widgets such as
 * {@link ListView} or {@link Spinner}.
 * <p>
 * By default, the array adapter creates a view by calling {@link Object#toString()} on each
 * data object in the collection you provide, and places the result in a TextView.
 * You may also customize what type of view is used for the data object in the collection.
 * To customize what type of view is used for the data object,
 * override {@link #getView(int, View, ViewGroup)}
 * and inflate a view resource.
 * For a code example, see
 * the <a href="https://developer.android.com/samples/CustomChoiceList/index.html">
 * CustomChoiceList</a> sample.
 * </p>
 * <p>
 * For an example of using an array adapter with a ListView, see the
 * <a href="{@docRoot}guide/topics/ui/declaring-layout.html#AdapterViews">
 * Adapter Views</a> guide.
 * </p>
 * <p>
 * For an example of using an array adapter with a Spinner, see the
 * <a href="{@docRoot}guide/topics/ui/controls/spinner.html">Spinners</a> guide.
 * </p>
 * <p class="note"><strong>Note:</strong>
 * If you are considering using array adapter with a ListView, consider using
 * {@link android.support.v7.widget.RecyclerView} instead.
 * RecyclerView offers similar features with better performance and more flexibility than
 * ListView provides.
 * See the
 * <a href="https://developer.android.com/guide/topics/ui/layout/recyclerview.html">
 * Recycler View</a> guide.</p>
 */
public class ItemListViewAdapter<T> extends BaseAdapter implements Filterable {
    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();

    private final LayoutInflater mInflater;

    private final Context mContext;

    private View mView;

    private TextView nameTV;
    private TextView fachTV;
    private TextView haltbarkeitTV;

    /**
     * The resource indicating what views to inflate to display the content of this
     * array adapter.
     */
    private final int mResource;

    /**
     * Contains the list of objects that represent the data of this ItemListViewAdapter.
     * The content of this list is referred to as "the array" in the documentation.
     */
    private List<T> mObjects;

    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
     * {@link #mObjects} is modified.
     */
    private boolean mNotifyOnChange = true;

    // A copy of the original mObjects array, initialized from and then used instead as soon as
    // the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
    private ArrayList<T> mOriginalValues;
    private ArrayFilter mFilter;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ItemListViewAdapter(@NonNull Context context, @LayoutRes int resource,
                               @NonNull List<T> objects) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mObjects = objects;
        mResource = resource;
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void add(@Nullable T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(object);
            }
            mObjects.add(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of an element of the specified
     *                                       collection prevents it from being added to this list
     * @throws NullPointerException          if the specified collection contains one
     *                                       or more null elements and this list does not permit null
     *                                       elements, or if the specified collection is null
     * @throws IllegalArgumentException      if some property of an element of the
     *                                       specified collection prevents it from being added to this list
     */
    public void addAll(@NonNull Collection<? extends T> collection) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(collection);
            } else {
                mObjects.addAll(collection);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void addAll(T... items) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.addAll(mOriginalValues, items);
            } else {
                Collections.addAll(mObjects, items);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void insert(@Nullable T object, int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(index, object);
            }
            mObjects.add(index, object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the array.
     *
     * @param index The index of the object to remove.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void remove(int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(index);
            }
            mObjects.remove(index);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The index of the object to remove.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void remove(@Nullable T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(object);
            }
            mObjects.remove(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     *
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void clear() {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
            } else {
                mObjects.clear();
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *                   in this adapter.
     */
    public void sort(@NonNull Comparator<? super T> comparator) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.sort(mOriginalValues, comparator);
            }
            Collections.sort(mObjects, comparator);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    /**
     * Control whether methods that change the list ({@link #add}, {@link #addAll(Collection)},
     * {@link #addAll(Object[])}, {@link #insert}, {@link #remove}, {@link #clear},
     * {@link #sort(Comparator)}) automatically call {@link #notifyDataSetChanged}.  If set to
     * false, caller must manually call notifyDataSetChanged() to have the changes
     * reflected in the attached view.
     * <p>
     * The default is true, and calling notifyDataSetChanged()
     * resets the flag to true.
     *
     * @param notifyOnChange if true, modifications to the list will
     *                       automatically call {@link
     *                       #notifyDataSetChanged}
     */
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    /**
     * Returns the context associated with this array adapter. The context is used
     * to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    public @NonNull
    Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public @Nullable
    T getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(@Nullable T item) {
        return mObjects.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, parent);
    }

    private @NonNull
    View createViewFromResource(int position, @NonNull ViewGroup parent) {
        String name = ((Item) getItem(position)).getName();
        int amount = ((Item) getItem(position)).getAmount();
        String einheit = ((Item) getItem(position)).getEinheit();
        int fach = ((Item) getItem(position)).getFach();
        Date haltbarkeit = ((Item) getItem(position)).getMaxFreezeDate();
        String formatedHaltbarkeit = Constants.SDF.format(haltbarkeit);

        View convertView = mInflater.inflate(mResource, parent, false);
        this.mView = convertView;

        nameTV = convertView.findViewById(R.id.customLayoutName);
        fachTV = convertView.findViewById(R.id.customLayoutFach);
        haltbarkeitTV = convertView.findViewById(R.id.customLayoutHaltbarkeit);

        nameTV.setText(name + " (" + amount + " " + einheit + ")");
        fachTV.setText(String.format(convertView.getResources().getString(R.string.item_edit_section_label), fach));
        haltbarkeitTV.setText(Constants.DEFAULT_MAX_FREEZE_DATE.equals(formatedHaltbarkeit) ? "" : formatedHaltbarkeit);

        //check max freeze date to color entries only when max freeze date was set
        computeTextColor(haltbarkeit, formatedHaltbarkeit);

        return convertView;
    }

    private void computeTextColor(Date haltbarkeit, String formatedHaltbarkeit) {
        if (!Constants.DEFAULT_MAX_FREEZE_DATE.equals(formatedHaltbarkeit)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 14); //add 2 Weeks

            if (calendar.getTimeInMillis() >= haltbarkeit.getTime()) {
                colorText(Color.RED);
                return;
            }

            calendar.add(Calendar.DAY_OF_MONTH, 14); //add 2 Weeks = 4 Weeks in future
            if (calendar.getTimeInMillis() >= haltbarkeit.getTime()) {
                colorText(Color.rgb(0, 204, 0)); //dark green
            }
        }
    }

    private void colorText(int color) {
        nameTV.setTextColor(color);
        fachTV.setTextColor(color);
        haltbarkeitTV.setTextColor(color);
    }

    @Override
    public @NonNull
    Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    /**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();

            String[] kategorien = mView.getResources().getStringArray(R.array.kategorien);

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mObjects);
                }
            }

            if (constraint == null || constraint.length() == 0) {
                final ArrayList<T> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else if (Arrays.asList(kategorien).indexOf(constraint.toString()) != -1) {
                //Filter with Kategorie
                ArrayList<Item> filteredItems = filterKategorie(constraint.toString());
                results.values = filteredItems;
                results.count = filteredItems.size();
            } else {
                //Filter with Fach
                ArrayList<Item> filteredItems = filterFach(Integer.valueOf(constraint.toString()));
                results.values = filteredItems;
                results.count = filteredItems.size();
            }
            return results;
        }

        private ArrayList<Item> filterFach(Integer fach) {
            ArrayList<Item> filteredItems = new ArrayList<>();
            for (Item item : (ArrayList<Item>) mOriginalValues) {
                if (item.getFach() == fach) {
                    filteredItems.add(item);
                }
            }
            return filteredItems;
        }

        private ArrayList<Item> filterKategorie(String kategorie) {
            ArrayList<Item> filteredItems = new ArrayList<>();
            for (Item item : (ArrayList<Item>) mOriginalValues) {
                if (item.getKategorie().equals(kategorie)) {
                    filteredItems.add(item);
                }
            }
            return filteredItems;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}