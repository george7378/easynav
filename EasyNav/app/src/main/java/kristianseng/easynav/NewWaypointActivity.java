package kristianseng.easynav;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Map;

public class NewWaypointActivity extends AppCompatActivity
{
    private EditText nameEditText, latEditText, lonEditText, altEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_waypoint);

        nameEditText = (EditText)findViewById(R.id.nameEditText);
        latEditText = (EditText)findViewById(R.id.latEditText);
        lonEditText = (EditText)findViewById(R.id.lonEditText);
        altEditText = (EditText)findViewById(R.id.altEditText);
        altEditText.setHint(getResources().getString(R.string.label_altwgs) + " (" + (GlobalData.settings_metric ? "m)" : "ft)"));
    }

    public void onClickSave(View view)
    {
        String stringName = nameEditText.getText().toString();
        String stringLat = latEditText.getText().toString();
        String stringLon = lonEditText.getText().toString();
        String stringAlt = altEditText.getText().toString();

        if (stringName.length() == 0 || stringLat.length() == 0 || stringLon.length() == 0 || stringAlt.length() == 0)
        {
            GlobalData.showAlertMessage(getResources().getString(R.string.alert_saveerror_waypoint), getResources().getString(R.string.alert_saveerror_content_msg), this);
            return;
        }

        float floatLat = Float.parseFloat(stringLat);
        float floatLon = Float.parseFloat(stringLon);
        float floatAlt = Float.parseFloat(stringAlt);

        if (floatLat < -90.0f || floatLat > 90.0f)
        {
            GlobalData.showAlertMessage(getResources().getString(R.string.alert_saveerror_waypoint), getResources().getString(R.string.alert_saveerror_latitude_msg), this);
            return;
        }
        if (floatLon < -180.0f || floatLon > 180.0f)
        {
            GlobalData.showAlertMessage(getResources().getString(R.string.alert_saveerror_waypoint), getResources().getString(R.string.alert_saveerror_longitude_msg), this);
            return;
        }

        SharedPreferences savedWaypoints = getSharedPreferences(GlobalData.WAYPOINTS_FILENAME, MODE_PRIVATE);
        Map <String, ?> allSavedWaypoints = savedWaypoints.getAll();
        for (String waypointKey : allSavedWaypoints.keySet())
        {
            if (waypointKey.equals(stringName))
            {
                GlobalData.showAlertMessage(getResources().getString(R.string.alert_saveerror_waypoint), getResources().getString(R.string.alert_saveerror_name_msg), this);
                return;
            }
        }

        SharedPreferences.Editor editor = savedWaypoints.edit();
        editor.putString(stringName, stringName + "," + floatLat + "," + floatLon + "," + (GlobalData.settings_metric ? floatAlt : floatAlt/GlobalData.CONVERT_M_TO_FT));
        editor.commit();

        this.finish();
    }

    public void onClickCancel(View view)
    {
        this.finish();
    }
}
