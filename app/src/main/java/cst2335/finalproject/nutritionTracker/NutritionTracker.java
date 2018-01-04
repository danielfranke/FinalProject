/**
 * CST2335 Android Final Project
 * Created by Daniel Franke
 */

package cst2335.finalproject.nutritionTracker;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cst2335.finalproject.DatabaseHelper;
import cst2335.finalproject.MainActivity;
import cst2335.finalproject.R;

public class NutritionTracker extends MainActivity {

    private final static String ACTIVITY_NAME = "NutritionTracker"; // used for log messages
    private ArrayList<FoodEntry> foodItems; // arraylist to store food items
    private ListView foodItemsListView; // displayed list of all food items recorded
    private TextView caloriesTotal;
    private int selectedEntry;
    private NutritionAdapter nutritionAdapter; // used to access ListView Adapter.


    // START: OnCreate ****************************************************************************************************************************************************************************** /

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrtion_tracker);

        // Initialize variables
        foodItems = new ArrayList<>();
        foodItemsListView = findViewById(R.id.food_list);
        final ImageButton addEntryBtn = findViewById(R.id.nutrition_add_entry_button);
        final ImageButton deleteEntryBtn = findViewById(R.id.nutrition_delete_entry_button);
        final ImageButton EditEntryBtn = findViewById(R.id.nutrition_edit_entry_button);
        caloriesTotal = findViewById(R.id.nutrition_total_calories_textView);

        addEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // add fragment when users clicks add
                Intent addIntent = new Intent(NutritionTracker.this, NutritionAddEntry.class);
                Bundle details = new Bundle();
                details.putInt("code", 10);
                addIntent.putExtras(details);
                startActivityForResult(addIntent, 0);
            }
        });
        deleteEntryBtn.setEnabled(false); // delete button is disabled
        deleteEntryBtn.setOnClickListener(new View.OnClickListener() { // delete button is enabled
            @Override
            public void onClick(View view) { // warn user if delete button clicked
                warnUserBeforeDeletion();
            }
        });
        EditEntryBtn.setEnabled(false); // edit button disabled
        EditEntryBtn.setOnClickListener(new View.OnClickListener() { // edit button enabled
            @Override
            public void onClick(View view) { // if user clicks edit button
                startActivityForResult(generateEditIntent(), 0);
            }
        });

        // Get existing entries form database
        new QueryNutritionDatabase().execute("1");

        foodItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Buttons only work if entry is selected
                selectedEntry = i;
                deleteEntryBtn.setEnabled(true);
                EditEntryBtn.setEnabled(true);
                //foodItemsListView.smoothScrollToPosition(i);
                inflateNutritionFragment(i);
            }
        });
    } // END: OnCreate
    
    private Intent generateEditIntent() {
        Intent editIntent = new Intent(NutritionTracker.this, NutritionAddEntry.class);
        FoodEntry food = foodItems.get(selectedEntry);
        Bundle fragmentInfo = new Bundle();
        fragmentInfo.putInt("code", 11);
        fragmentInfo.putString("item", food.getFoodItem());
        fragmentInfo.putString("calories", String.valueOf(food.getCalories()));
        fragmentInfo.putString("fat", String.valueOf(food.getFat()));
        fragmentInfo.putString("carbohydrates", String.valueOf(food.getCarbohydrates()));
        fragmentInfo.putInt("selectedEntry", selectedEntry);
        editIntent.putExtras(fragmentInfo);
        return editIntent;
    }

    // Show dialog box that confirms selected entry was deleted
    private void warnUserBeforeDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NutritionTracker.this);
        builder.setTitle(getApplication().getString(R.string.confirm_delete))
                .setPositiveButton(getApplication().getString(R.string.confirm_delete_response_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // delete from database if user clicks on Delete.
                        new QueryNutritionDatabase().execute("-10");
                    }
                })
                .setNegativeButton(getApplication().getString(R.string.confirm_delete_response_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .create()
                .show();
    }

    // Inflates fragment to show selected entry
    private void inflateNutritionFragment(int i) {
        if(!foodItems.isEmpty()) {
            FoodEntry food = foodItems.get(i);
            Bundle fragmentInfo = new Bundle();
            fragmentInfo.putInt("code", 1);
            fragmentInfo.putString("showEntryDetails", food.getFoodItem());
            fragmentInfo.putString("caloriesDetails", String.valueOf(food.getCalories()));
            fragmentInfo.putString("fatDetails", String.valueOf(food.getFat()));
            fragmentInfo.putString("carbohydratesDetails", String.valueOf(food.getCarbohydrates()));
            fragmentInfo.putString("entryDateDetails", food.getFormattedDate());
            NutritionFragment detailsFragment = new NutritionFragment();
            detailsFragment.setArguments(fragmentInfo);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nutrition_details_fragment, detailsFragment);
            transaction.commit();
        }
    }

    // Calculate total calories
    private void setTotalCaloriesTextView() {
        String currentDate = new SimpleDateFormat("MMddyyyy").format(new Date(System.currentTimeMillis()));
        double totalCalories = 0;
        for(FoodEntry food : foodItems) {
            if(food.getDate().equals(currentDate)) {
                totalCalories += food.getCalories();
            }
        }
        // Total calories consumed today
        caloriesTotal.setText(getApplicationContext().getString(R.string.nutrition_daily_calories_eaten) + " " + String.valueOf(totalCalories) + " Kcal");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        if(menuItem.getItemId() == R.id.mi_help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(NutritionTracker.this);
            LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_box, null);
            TextView author = layout.findViewById(R.id.author);
            TextView version = layout.findViewById(R.id.version);
            TextView instructions = layout.findViewById(R.id.instructions);

            // Information page
            String instructionsPage = getString(R.string.instructions);
            author.setText(String.format("%s Daniel Franke", getApplication().getString(R.string.authorText)));
            version.setText(String.format("%s 1.0.0", getApplication().getString(R.string.version)));
            //instructions.setText(String.format("%s%s", getApplication().getString(R.string.nutrition_instructions), instructionsPage));
            builder.setView(layout);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode) {
            case 10:
                QueryNutritionDatabase nutritionQuery = new QueryNutritionDatabase();
                nutritionQuery.setBundle(data.getExtras());
                nutritionQuery.execute(String.valueOf(resultCode));
                break;
            case 11:
                nutritionQuery = new QueryNutritionDatabase();
                nutritionQuery.setBundle(data.getExtras());
                nutritionQuery.execute(String.valueOf(resultCode));
                break;
        }
    }

    // QueryNutritionDatase CLASS ****************************************************************************************************************************************************************************** /

    // Interact with nutrition database; run tasks in background
    // AsyncTask: https://developer.android.com/reference/android/os/AsyncTask.html
    private class QueryNutritionDatabase extends AsyncTask<String, Integer, String> {

        private SQLiteDatabase db; // database
        private Cursor cursor;
        private ProgressBar progressBar;
        private Bundle details;
        private int code;

        // AsyncTask DO IN BACKGROUND - STEP 2 *****
        @Override
        protected String doInBackground(String... args) { // https://androidresearch.wordpress.com/2012/03/17/understanding-asynctask-once-and-forever/

            DatabaseHelper helper = new DatabaseHelper(NutritionTracker.this);
            db = helper.getWritableDatabase();
            cursor = queryNutritionTable();
            progressBar = findViewById(R.id.progress_bar);

            code = 0; // default code
            try {
                code = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {}


            switch (code) { // Conditional switch statements


                case -10: // User pressed DELETE button
                    cursor.moveToPosition(selectedEntry);
                    publishProgress(15); // update progress bar status (percentage)
                    long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ID));
                    publishProgress(30);
                    removeFromDatabase(String.valueOf(id));
                    publishProgress(45);
                    cursor = queryNutritionTable();
                    publishProgress(60);
                    foodItems.clear();
                    publishProgress(75);
                    loadPrevious();
                    publishProgress(100);
                    break;


                case 1: // Display PREVIOUS DATA on activity launch
                    loadPrevious();
                    break;


                case 10: // User pressed SUBMIT button in fragment
                    String item = details.getString("item");
                    double calories = details.getDouble("calories");
                    double fat = details.getDouble("fat");
                    double carbs = details.getDouble("carbohydrates");
                    long timestamp = details.getLong("timestamp");

                    foodItems.add(new FoodEntry(item, calories, fat, carbs, timestamp));
                    publishProgress(50);
                    writeToDatabase(item, calories, fat, carbs, timestamp);
                    publishProgress(100);
                    break;


                case 11: // User SELECTED entry to display details
                    cursor.moveToPosition(details.getInt("selectedEntry"));
                    id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ID));
                    item = details.getString("item");
                    calories = details.getDouble("calories");
                    fat = details.getDouble("fat");
                    carbs = details.getDouble("carbohydrates");
                    updateDatabase(String.valueOf(id), item, calories, fat, carbs);
                    publishProgress(25);
                    cursor = queryNutritionTable();
                    publishProgress(50);
                    foodItems.clear();
                    publishProgress(75);
                    loadPrevious();
                    publishProgress(100);
                    break;
            }
            db.close(); // close database
            return "";
        }

        // AsyncTask ON PROGRESS UPDATE - STEP 3 *****
        @Override
        protected void onProgressUpdate(Integer... progress) {

            progressBar.setVisibility(View.VISIBLE); // Show progress bar
            progressBar.setProgress(progress[0]); // update progress bar status

        }

        // AsyncTask ON POST EXECUTE - STEP 4 *****
        @Override
        protected void onPostExecute(String result) {

            switch (code) {


                case -10: // User pressed DELETE button
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.nutrition_details_fragment, new NutritionFragment());
                    transaction.commit();
                    nutritionAdapter.notifyDataSetChanged();
                    findViewById(R.id.nutrition_delete_entry_button).setEnabled(false);
                    findViewById(R.id.nutrition_edit_entry_button).setEnabled(false);
                    Snackbar.make(findViewById(R.id.nutrition_layout_buttons), getApplication().getString(R.string.nutrition_snackbar_delete_entry), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;


                case 1: // Display PREVIOUS DATA on activity launch
                    nutritionAdapter = new NutritionAdapter(NutritionTracker.this);
                    foodItemsListView.setAdapter(nutritionAdapter);
                    break;


                case 10: // User pressed SUBMIT button in fragment
                    nutritionAdapter.notifyDataSetChanged();
                    Snackbar.make(findViewById(R.id.nutrition_layout_buttons), getApplication().getString(R.string.nutrition_snackbar_add_entry), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;

                case 11: // User pressed UPDATE button in fragment
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.nutrition_details_fragment, new NutritionFragment());
                    transaction.commit();
                    nutritionAdapter.notifyDataSetChanged();
                    findViewById(R.id.nutrition_delete_entry_button).setEnabled(false);
                    findViewById(R.id.nutrition_edit_entry_button).setEnabled(false);
                    Snackbar.make(findViewById(R.id.nutrition_layout_buttons), getApplication().getString(R.string.nutrition_snackbar_update_entry), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
            }
            progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
            setTotalCaloriesTextView();
        }

        public void setBundle(Bundle bundle) {
            this.details = bundle;
        }

        // LOAD foodItems from previous entries
        private void loadPrevious() {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                foodItems.add(new FoodEntry(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.FOOD_ITEM)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.CALORIES)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.FAT)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.CARBOHYDRATES)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.NUTRITION_ENTRY_DATE))
                ));
                cursor.moveToNext();
            }
        }

        // QUERY all records in nutrition database table
        private Cursor queryNutritionTable() {
            return db.query(DatabaseHelper.NUTRITION_TABLE,
                    new String[]{DatabaseHelper.ID,
                            DatabaseHelper.FOOD_ITEM,
                            DatabaseHelper.CALORIES,
                            DatabaseHelper.FAT,
                            DatabaseHelper.CARBOHYDRATES,
                            DatabaseHelper.NUTRITION_ENTRY_DATE},
                    null, null, null, null, null);
        }

        // ADD ENTRY to nutrition database table
        private void writeToDatabase(String item, double calories, double fat, double carbohydrates, long timestamp) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.FOOD_ITEM, item);
            values.put(DatabaseHelper.CALORIES, calories);
            values.put(DatabaseHelper.FAT, fat);
            values.put(DatabaseHelper.CARBOHYDRATES, carbohydrates);
            values.put(DatabaseHelper.NUTRITION_ENTRY_DATE, timestamp);
            try {
                db.insert(DatabaseHelper.NUTRITION_TABLE, "NULL MESSAGE", values);
                Log.i(ACTIVITY_NAME, "(!) Success! Entry was added to database");
            } catch (Exception e) {
                Log.i(ACTIVITY_NAME, "(!!) Failed! Entry was NOT added to database");
                e.printStackTrace();
            }
        }

        // REMOVE ENTRY from nutrition database table
        private void removeFromDatabase(String id) {
            try {
                db.execSQL("DELETE FROM " + DatabaseHelper.NUTRITION_TABLE + " WHERE " + DatabaseHelper.ID + " = " + id);
                Log.i(ACTIVITY_NAME, "(!) Success! Entry was deleted from database");
            } catch (Exception e) {
                Log.i(ACTIVITY_NAME, "(!!) Failed! Entry was NOT deleted from database");
                e.printStackTrace();
            }
        }

        // UPDATE ENTRY from nutrition database table
        private void updateDatabase(String id, String item, double calories, double fat, double carbohydrates) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.FOOD_ITEM, item);
            values.put(DatabaseHelper.CALORIES, calories);
            values.put(DatabaseHelper.FAT, fat);
            values.put(DatabaseHelper.CARBOHYDRATES, carbohydrates);

            try {
                db.update(DatabaseHelper.NUTRITION_TABLE, values, DatabaseHelper.ID + " = " + id, null);
                Log.i(ACTIVITY_NAME, "(!) Success! Entry was updated in database");
            } catch (Exception e) {
                Log.i(ACTIVITY_NAME, "(!!) Failed! Entry was NOT updated in database");
            }
        }
    } // END CLASS: QueryNutritionDatabase


    // ADAPTER CLASS ****************************************************************************************************************************************************************************** /

    // Display Nutrition Tracker's list view
    private class NutritionAdapter extends ArrayAdapter<FoodEntry> {

        public NutritionAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getCount() { return foodItems.size(); }

        @Override
        public FoodEntry getItem(int index) { return foodItems.get(index);}

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            LayoutInflater inflater = NutritionTracker.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.list_row, null);
            TextView foodEntry = result.findViewById(R.id.row_entry);
            foodEntry.setText(getItem(index).getFoodItem());
//            TextView entryDate = result.findViewById(R.id.row_sub_entry);
//            entryDate.setText(getItem(index).getFormattedDate());
            return result;
        }
    } // END CLASS: NutritionAdapter


    // DAO CLASS ****************************************************************************************************************************************************************************** /

   // Data Access Object (DAO) for Nutrition table
    private class FoodEntry { // Class to record food item details
        private String foodItems; // food items being recorded
        private double calories; // number of calories for food item
        private double fat; // number of fat (in grams "g") for food item
        private double carbohydrates; // number of carbohydrates (in grams "g") for food item
        private long timestamp; // time of entry
        private String date; // used to check against formattedDate
        private String formattedDate; // date entry was added

        public FoodEntry(String foodItems, long timestamp) {
            this(foodItems, 0, 0, 0, timestamp);
        }

        public FoodEntry(String foodItems, double calories, double fat, double carbohydrates, long timestamp) {
            this.foodItems = foodItems;
            this.calories = calories;
            this.fat = fat;
            this.carbohydrates = carbohydrates;
            this.timestamp = timestamp;
            this.date = getFormattedDate("MMddyyyy");
            this.formattedDate = getFormattedDate(" EEE MMM dd, yyyy '('hh:mm:ss a')'"); // formatted date displayed in listView of food items
        }

        public String getFoodItem() { return this.foodItems; }
        public double getCalories() { return this.calories; };
        public double getFat() { return this.fat; }
        public double getCarbohydrates() { return this.carbohydrates; }
        public String getDate() { return this.date; }
        public String getFormattedDate() { return this.formattedDate; } // **** find usage
        public long getTimestamp() { return this.timestamp; }

        // Method to return formatted date
        // (https://developer.android.com/reference/java/text/DateFormat.html)
        private String getFormattedDate(String dateFormat) {
            SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);
            String date = formatDate.format(new Date(this.timestamp));
            return date;
        }
    }
} // END CLASS: NutritionTracker