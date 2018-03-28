package kristianseng.easynav;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import java.text.DecimalFormat;

class GlobalData
{
    private static final String SETTINGS_FILENAME = "prefsSettings";
    private static final String SETTINGS_ACTIVE_WAYPOINT_KEY = "activeWaypoint";
    private static final String SETTINGS_METRIC_KEY = "metric";
    private static final String SETTINGS_UPDATE_FREQUENCY_KEY = "updateFrequency";

    public static final String WAYPOINTS_FILENAME = "prefsWaypoints";

    public static final float CONVERT_M_TO_FT = 3.28084f;

    public static Waypoint settings_activeWaypoint = new Waypoint();
    public static boolean settings_metric = true;
    public static int settings_updateFrequency = 2;

    public static void reloadSettingsFromFile(Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS_FILENAME, Context.MODE_PRIVATE);
        settings_activeWaypoint = parseWaypoint(settings.getString(SETTINGS_ACTIVE_WAYPOINT_KEY, "Default,0,0,0"));
        settings_metric = settings.getBoolean(SETTINGS_METRIC_KEY, true);
        settings_updateFrequency = settings.getInt(SETTINGS_UPDATE_FREQUENCY_KEY, 2);
    }

    public static void updateSettingsActiveWaypoint(String newValue, Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SETTINGS_ACTIVE_WAYPOINT_KEY, newValue);
        editor.commit();

        reloadSettingsFromFile(context);
    }

    public static void updateSettingsMetric(boolean newValue, Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SETTINGS_METRIC_KEY, newValue);
        editor.commit();

        reloadSettingsFromFile(context);
    }

    public static void updateSettingsUpdateFrequency(int newValue, Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(SETTINGS_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SETTINGS_UPDATE_FREQUENCY_KEY, newValue);
        editor.commit();

        reloadSettingsFromFile(context);
    }

    private static Waypoint parseWaypoint(String waypointString)
    {
        String [] waypointContents = waypointString.split(",");
        if (waypointContents.length < 4)
        {
            return new Waypoint();
        }

        return new Waypoint(waypointContents[0], Float.parseFloat(waypointContents[1]), Float.parseFloat(waypointContents[2]), Float.parseFloat(waypointContents[3]));
    }

    public static void showAlertMessage(String title, String message, Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton("OK", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String getFormattedOutput(float value, ValueInputType valtype)
    {
        if (valtype == ValueInputType.LATLON)
        {
            DecimalFormat formatter = new DecimalFormat("0.0000");
            return formatter.format(value);
        }
        else if (valtype == ValueInputType.DIST)
        {
            if (settings_metric)
            {
                boolean showInKM = value > 1000;
                if (showInKM)
                {
                    value /= 1000;
                }
                DecimalFormat formatter = showInKM ? new DecimalFormat("0.00") : new DecimalFormat("0.0");
                return formatter.format(value) + (showInKM ? " km" : " m");
            }
            value *= CONVERT_M_TO_FT;
            boolean showInMiles = value > 5280;
            if (showInMiles)
            {
                value /= 5280;
            }
            DecimalFormat formatter = showInMiles ? new DecimalFormat("0.00") : new DecimalFormat("0.0");
            return formatter.format(value) + (showInMiles ? " mi" : " ft");
        }
        else if (valtype == ValueInputType.ALT)
        {
            DecimalFormat formatter = new DecimalFormat("0.0");
            if (settings_metric)
            {
                return formatter.format(value) + " m";
            }
            value *= CONVERT_M_TO_FT;
            return formatter.format(value) + " ft";
        }
        //ALTOFFSET
        DecimalFormat formatter = new DecimalFormat("0.0");
        if (settings_metric)
        {
            return formatter.format(Math.abs(value)) + " m";
        }
        value *= CONVERT_M_TO_FT;
        return formatter.format(Math.abs(value)) + " ft";
    }
}

enum ValueInputType
{
    LATLON, DIST, ALT, ALTOFFSET
}

class Waypoint
{
    public String name;
    public float lat, lon, alt;

    public Waypoint()
    {
        name = "Default";
        lat = 0.0f;
        lon = 0.0f;
        alt = 0.0f;
    }
    public Waypoint(String na, float la, float lo, float al)
    {
        name = na;
        lat = la;
        lon = lo;
        alt = al;
    }

    public String getAsString()
    {
        return name + "," + lat + "," + lon + "," + alt;
    }
}
