package kristianseng.easynav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class WaypointsActivity extends AppCompatActivity
{
    private TextView activeWaypointText;
    private Spinner savedWaypointsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waypoints);

        savedWaypointsSpinner = (Spinner)findViewById(R.id.savedWaypointsSpinner);
        activeWaypointText = (TextView)findViewById(R.id.activeWaypointValueTextView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        populateActiveWaypoint();
        populateWaypointsSpinner();
    }

    public void onClickDelete(View view)
    {
        Object selectedSpinnerItem = savedWaypointsSpinner.getSelectedItem();
        if (selectedSpinnerItem == null)
        {
            return;
        }

        SharedPreferences savedWaypoints = getSharedPreferences(GlobalData.WAYPOINTS_FILENAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = savedWaypoints.edit();
        editor.remove(selectedSpinnerItem.toString());
        editor.commit();

        populateWaypointsSpinner();
    }

    public void onClickActivate(View view)
    {
        Object selectedSpinnerItem = savedWaypointsSpinner.getSelectedItem();
        if (selectedSpinnerItem == null)
        {
            return;
        }

        SharedPreferences savedWaypoints = getSharedPreferences(GlobalData.WAYPOINTS_FILENAME, MODE_PRIVATE);
        String selectedWaypoint = savedWaypoints.getString(selectedSpinnerItem.toString(), "none");
        if (selectedWaypoint.equals("none"))
        {
            return;
        }

        GlobalData.updateSettingsActiveWaypoint(selectedWaypoint, this);

        populateActiveWaypoint();
    }

    public void onClickActiveWaypoint(View view)
    {
        DecimalFormat latlonFormatter = new DecimalFormat("0.####");
        DecimalFormat altFormatter = new DecimalFormat("0.#");
        String activeWaypointSummary = getResources().getString(R.string.heading_latitude_colon) + latlonFormatter.format(GlobalData.settings_activeWaypoint.lat) + getResources().getString(R.string.append_degrees_shorthand) + "\n"
                                     + getResources().getString(R.string.heading_longitude_colon) + latlonFormatter.format(GlobalData.settings_activeWaypoint.lon) + getResources().getString(R.string.append_degrees_shorthand) + "\n"
                                     + getResources().getString(R.string.heading_altitude_colon) + altFormatter.format(GlobalData.settings_metric ? GlobalData.settings_activeWaypoint.alt : GlobalData.settings_activeWaypoint.alt*GlobalData.CONVERT_M_TO_FT) + (GlobalData.settings_metric ? " m" : " ft");

        GlobalData.showAlertMessage(getResources().getString(R.string.alert_waypoint_info) + GlobalData.settings_activeWaypoint.name, activeWaypointSummary, this);
    }

    public void onClickNew(View view)
    {
        Intent intent = new Intent(this, NewWaypointActivity.class);
        startActivity(intent);
    }

    public void onClickReturn(View view)
    {
        this.finish();
    }

    private void populateWaypointsSpinner()
    {
        SharedPreferences savedWaypoints = getSharedPreferences(GlobalData.WAYPOINTS_FILENAME, MODE_PRIVATE);
        Map <String, ?> allSavedWaypoints = savedWaypoints.getAll();

        ArrayList <String> savedWaypointsList = new ArrayList <>();
        for (String waypointKey : allSavedWaypoints.keySet())
        {
            savedWaypointsList.add(waypointKey);
        }
        Collections.sort(savedWaypointsList, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter <String> waypointSpinnerAdapter = new ArrayAdapter <>(this, android.R.layout.simple_spinner_item, savedWaypointsList);
        waypointSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        savedWaypointsSpinner.setAdapter(waypointSpinnerAdapter);
    }

    private void populateActiveWaypoint()
    {
        activeWaypointText.setText(GlobalData.settings_activeWaypoint.name);
    }
}
