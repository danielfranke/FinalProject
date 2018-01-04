package cst2335.finalproject.nutritionTracker;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import cst2335.finalproject.R;


public class NutritionAddEntry extends Activity {

    private final static String ACTIVITY_NAME = "AddNutritionEntryEntry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_add_entry);
        Log.i(ACTIVITY_NAME, "(!) IS PHONE");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        NutritionFragment nutritionFragment = new NutritionFragment();
        Bundle info = getIntent().getExtras();
        nutritionFragment.setArguments(info);
        nutritionFragment.setTablet(false);
        transaction.replace(R.id.add_nutrition_frame_layout, nutritionFragment);
        transaction.commit();
    }
}
