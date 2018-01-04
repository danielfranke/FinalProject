package cst2335.finalproject.nutritionTracker;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cst2335.finalproject.R;

public class NutritionFragment extends Fragment {

    private final static String FRAGMENT_NAME = "NutritionFragment";
    private View nutritionFragmentView;
    private boolean isTablet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(FRAGMENT_NAME, "In onCreateView for Fragment");
        Bundle passedInfo = getArguments();

        int code = 0;
        try {
            code = passedInfo.getInt("code");
            Log.i(FRAGMENT_NAME, "refer to process code");
        } catch(NullPointerException e) {}

        switch(code) {

            case 1:
                nutritionFragmentView = inflater.inflate(R.layout.activity_nutrition_details, container, false);
                showEntryDetails(nutritionFragmentView, passedInfo);
                break;

            case 10:
                nutritionFragmentView = inflater.inflate(R.layout.activity_nutrition_add_entry, container, false);
                addNutritionEntry(nutritionFragmentView);
                break;

            case 11:
                nutritionFragmentView = inflater.inflate(R.layout.activity_nutrition_add_entry, container, false);
                editNutritionEntry(nutritionFragmentView, passedInfo);
                break;
        }
        return nutritionFragmentView;
    }

    // DISPLAY ENTRY DETAILS WHEN ENTRY CLICKED BY USER
    private void showEntryDetails(final View layoutView, Bundle info) {
        TextView entryItemDetails = layoutView.findViewById(R.id.nutrition_item_details); // item entered
        TextView entryDateDetails = layoutView.findViewById(R.id.nutrition_date_details); // entry date
        TextView caloriesDetails = layoutView.findViewById(R.id.nutrition_calories_details); // entry calories
        TextView fatDetails = layoutView.findViewById(R.id.nutrition_fat_details); // entry fat
        TextView carbohydratesDetails = layoutView.findViewById(R.id.nutrition_carbohydrates_details); // entry carbohydrates

        // Set string values for nutrition details
        entryItemDetails.setText(info.getString("showEntryDetails"));
        entryDateDetails.setText(String.format("%s%s", getActivity().getString(R.string.nutrition_details_entry_date), info.getString("entryDateDetails")));
        caloriesDetails.setText(String.format("%s %s Kcal", getActivity().getString(R.string.nutrition_details_calories), info.getString("caloriesDetails")));
        fatDetails.setText(String.format("%s %s g", getActivity().getString(R.string.nutrition_details_fat), info.getString("fatDetails")));
        carbohydratesDetails.setText(String.format("%s %s g", getActivity().getString(R.string.nutrition_details_carbohydrates), info.getString("carbohydratesDetails")));
    }


    // ADD ENTRY
    public void addNutritionEntry(final View layoutView) {

        final EditText editTextAddEntry = layoutView.findViewById(R.id.add_nutrition_item_edit_text);
        Button addNutritionEntryButton = layoutView.findViewById(R.id.add_nutrition_entry_button);
        addNutritionEntryButton.setText(getActivity().getString(R.string.add_new_entry));
        addNutritionEntryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View handlerView) {

                if(editTextAddEntry.getText().toString().matches("")) { // check if empty

                    Toast.makeText( // display warning
                            handlerView.getContext(),
                            getActivity().getString(R.string.nutrition_empty_entry_warning),
                            Toast.LENGTH_LONG
                    ).show();
                }
                else { // attach time stamp to entry

                    Bundle formValues = getNewEntryInfo(layoutView);
                    formValues.putString("item", editTextAddEntry.getText().toString().trim());
                    formValues.putLong("timestamp", System.currentTimeMillis());

                    Intent completedForm = new Intent(handlerView.getContext(), NutritionTracker.class);
                    completedForm.putExtras(formValues);
                    getActivity().setResult(10, completedForm);


                    if(isTablet) { // is the device a tablet?

                        getActivity().getFragmentManager().beginTransaction().remove(NutritionFragment.this).commit(); // remove fragment
                    }
                    else {

                        getActivity().finish(); // finish activity
                    }
                }
            }
        });
    }

    private Bundle getNewEntryInfo(View view) {

        // Set values for editText
        Bundle info = new Bundle();
        EditText editTextAddEntryItem = view.findViewById(R.id.add_nutrition_item_edit_text);
        EditText editTextAddCalories = view.findViewById(R.id.add_nutrition_calories_edit_text);
        EditText editTextAddFat = view.findViewById(R.id.add_nutrition_fat_edit_text);
        EditText editTextAddCarbohydrates = view.findViewById(R.id.add_nutrition_carbohydrates_edit_text);

        // Initialize variables
        String foodItem = editTextAddEntryItem.getText().toString();
        double calories = 0;
        double fat = 0;
        double carbohydrates = 0;

        // Error catching
        try{calories = Double.parseDouble(editTextAddCalories.getText().toString());} catch(Exception e) {}
        try{fat = Double.parseDouble(editTextAddFat.getText().toString());} catch(Exception e) {}
        try{carbohydrates = Double.parseDouble(editTextAddCarbohydrates.getText().toString());} catch(Exception e) {}

        // Store values in bundle
        info.putString("food_item", foodItem);
        info.putDouble("calories", calories);
        info.putDouble("fat", fat);
        info.putDouble("carbohydrates", carbohydrates);

        return info; // return all values stored
    }

    // EDIT ENTRY
    private void editNutritionEntry(final View layoutView, final Bundle info) {

        // Get ids from
        final EditText editTextAddEntry = layoutView.findViewById(R.id.add_nutrition_item_edit_text);
        final EditText editTextAddCalories = layoutView.findViewById(R.id.add_nutrition_calories_edit_text);
        final EditText editTextAddFat = layoutView.findViewById(R.id.add_nutrition_fat_edit_text);
        final EditText editTextAddCarbohydrates = layoutView.findViewById(R.id.add_nutrition_carbohydrates_edit_text);

        // fill the edit texts with the current value of the details for the entry
        editTextAddEntry.setText(info.getString("item"));
        editTextAddCalories.setText(info.getString("calories"));
        editTextAddFat.setText(info.getString("fat"));
        editTextAddCarbohydrates.setText(info.getString("carbohydrates"));

        // Make the edit text headers visible, so that user knows what field they're changing value of
        layoutView.findViewById(R.id.add_nutrition_item_text_view).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.add_nutrition_calories_text_view).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.add_nutrition_fat_text_view).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.add_nutrition_carbohydrates_text_view).setVisibility(View.VISIBLE);

        // Submit button actions
        Button submitButton = layoutView.findViewById(R.id.add_nutrition_entry_button);
        submitButton.setText(getActivity().getString(R.string.update_entry));
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View handlerView) {
                String item = editTextAddEntry.getText().toString().trim();

                // Has the entry changed?
                if(isEntryChanged(info, item, editTextAddCalories.getText().toString().trim(),
                        editTextAddFat.getText().toString().trim(), editTextAddCarbohydrates.getText().toString().trim())) {

                    // Get existing and updated values
                    Bundle formValues = getNewEntryInfo(layoutView);
                    formValues.putString("item", item);
                    formValues.putInt("selectedEntry", info.getInt("selectedEntry"));

                    Intent completedForm = new Intent(handlerView.getContext(), NutritionTracker.class);
                    completedForm.putExtras(formValues);

                    // Pass values and update database
                    getActivity().setResult(11, completedForm);

                    // Is device a tablet?
                    if(isTablet) { // if true: remove fragment
                        getActivity().getFragmentManager().beginTransaction().remove(NutritionFragment.this).commit();
                    }
                    else { // else: finish
                        getActivity().finish();
                    }
                }
                else { // Display warning if no changes made to entry

                    Toast.makeText(
                            handlerView.getContext(),
                            getActivity().getString(R.string.nutrition_nothing_changed_toast),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }

    private boolean isEntryChanged(Bundle info, String... values) {

        boolean isEntryChanged = false;

        if( // Same values or empty field
                (!values[0].matches("") && !values[0].equals(info.getString("item"))) ||
                        !values[1].equals(info.getString("calories")) ||
                        !values[2].equals(info.getString("fat")) ||
                        !values[3].equals(info.getString("carbohydrates"))

                ) {
            isEntryChanged = true;
            Log.i(FRAGMENT_NAME, "(!) ENTRY CHANGED!");
        }
        else {
            Log.i(FRAGMENT_NAME, "(!) ENTRY UNCHANGED");
        }
        return isEntryChanged;
    }

    public void setTablet(boolean isTablet) { this.isTablet = isTablet; }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

