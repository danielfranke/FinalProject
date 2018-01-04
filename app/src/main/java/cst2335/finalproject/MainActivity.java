package cst2335.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import cst2335.finalproject.nutritionTracker.NutritionTracker;

// THE MAIN CLASS FOR ALL ACTIVITIES
public abstract class MainActivity extends AppCompatActivity {

    @Override
    // When app menu is created
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    // When app menu option is selected
    public boolean onOptionsItemSelected(MenuItem item) {

        // Generic action state for Log.i in case statements (below)
        String tag = "(!) CLICKED";

        // Case statements for app menu
        switch (item.getItemId()) {
            case R.id.mi_activityTracker:
                Log.i(tag, "Activity Tracker icon");
                startActivity(new Intent(getApplicationContext(), NutritionTracker.class));
                break;
            case R.id.mi_nutritionTracker:
                Log.i(tag, "Nutrition Tracker icon");
                startActivity(new Intent(getApplicationContext(), NutritionTracker.class));
                break;
           case R.id.mi_thermostat:
                Log.i(tag, "House Thermostat icon");
                startActivity(new Intent(getApplicationContext(), NutritionTracker.class));
                break;
            case R.id.mi_automobile:
                Log.i(tag, "Automobile icon");
                startActivity(new Intent(getApplicationContext(), NutritionTracker.class));
                break;
        }
        return true;
    }
}