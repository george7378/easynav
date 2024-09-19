package kristianseng.easynav;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    private TextView firstFieldLabelText, firstFieldValueText, secondFieldLabelText, secondFieldValueText, bearingValueText;
    private boolean firstFieldShowDistance = true, secondFieldShowAltOffset = true;
    private ImageView bezelImage;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private float lastBearingToTarget = -1;

    private SensorManager sensorManager;
    private Sensor sMagnetometer;
    private float [] lastMagnetometerReading;
    private Sensor sAccelerometer;
    private float [] lastAccelerometerReading;
    private int compassUpdateCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalData.reloadSettingsFromFile(this);

        firstFieldLabelText = (TextView)findViewById(R.id.firstLabelTextView);
        firstFieldValueText = (TextView)findViewById(R.id.firstValueTextView);
        secondFieldLabelText = (TextView)findViewById(R.id.secondLabelTextView);
        secondFieldValueText = (TextView)findViewById(R.id.secondValueTextView);
        bearingValueText = (TextView)findViewById(R.id.bearingValueTextView);

        bezelImage = (ImageView)findViewById(R.id.bezelImageView);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Location activeWaypointAsLocation = new Location(LocationManager.GPS_PROVIDER);
                activeWaypointAsLocation.setLatitude(GlobalData.settings_activeWaypoint.lat);
                activeWaypointAsLocation.setLongitude(GlobalData.settings_activeWaypoint.lon);
                activeWaypointAsLocation.setAltitude(GlobalData.settings_activeWaypoint.alt);
                lastBearingToTarget = location.bearingTo(activeWaypointAsLocation);
                if (lastBearingToTarget < 0)
                {
                    lastBearingToTarget += 360;
                }

                String firstFieldValueString;
                if (firstFieldShowDistance)
                {
                    firstFieldValueString = GlobalData.getFormattedOutput(location.distanceTo(activeWaypointAsLocation), ValueInputType.DIST);
                }
                else
                {
                    firstFieldValueString = GlobalData.getFormattedOutput((float)location.getLatitude(), ValueInputType.LATLON) + " / " + GlobalData.getFormattedOutput((float)location.getLongitude(), ValueInputType.LATLON);
                }
                firstFieldValueText.setText(firstFieldValueString);

                String secondFieldValueString;
                if (secondFieldShowAltOffset)
                {
                    float altOffset = (float)location.getAltitude() - GlobalData.settings_activeWaypoint.alt;
                    secondFieldValueString = GlobalData.getFormattedOutput(altOffset, ValueInputType.ALTOFFSET);
                    secondFieldValueString += (altOffset < 0 ? getResources().getString(R.string.append_below) : getResources().getString(R.string.append_above));
                }
                else
                {
                    secondFieldValueString = GlobalData.getFormattedOutput((float)location.getAltitude(), ValueInputType.ALT);
                }
                secondFieldValueText.setText(secondFieldValueString);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
                if (status == LocationProvider.OUT_OF_SERVICE)
                {
                    firstFieldValueText.setText(getResources().getString(R.string.empty_field));
                    secondFieldValueText.setText(getResources().getString(R.string.empty_field));
                    bearingValueText.setText(getResources().getString(R.string.empty_field));

                    lastBearingToTarget = -1;
                }
            }

            @Override
            public void onProviderEnabled(String provider)
            {

            }

            @Override
            public void onProviderDisabled(String provider)
            {
                firstFieldValueText.setText(getResources().getString(R.string.empty_field));
                secondFieldValueText.setText(getResources().getString(R.string.empty_field));
                bearingValueText.setText(getResources().getString(R.string.empty_field));

                lastBearingToTarget = -1;
            }
        };

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String []{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        try
        {
            locationManager.removeUpdates(locationListener);
        }
        catch (SecurityException se)
        {

        }

        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        try
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*GlobalData.settings_updateFrequency, 0, locationListener);
        }
        catch (SecurityException se)
        {
            GlobalData.showAlertMessage(getResources().getString(R.string.alert_permswarning), getResources().getString(R.string.alert_permswarning_msg), this);
        }

        sensorManager.registerListener(this, sMagnetometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void onClickFirstField(View view)
    {
        firstFieldShowDistance = !firstFieldShowDistance;
        if (firstFieldShowDistance)
        {
            firstFieldLabelText.setText(getResources().getString(R.string.label_distance));
        }
        else
        {
            firstFieldLabelText.setText(getResources().getString(R.string.label_latlon));
        }

        firstFieldValueText.setText(getResources().getString(R.string.empty_field));
    }

    public void onClickSecondField(View view)
    {
        secondFieldShowAltOffset = !secondFieldShowAltOffset;
        if (secondFieldShowAltOffset)
        {
            secondFieldLabelText.setText(getResources().getString(R.string.label_altoffset));
        }
        else
        {
            secondFieldLabelText.setText(getResources().getString(R.string.label_altwgs));
        }

        secondFieldValueText.setText(getResources().getString(R.string.empty_field));
    }

    public void onClickWaypoints(View view)
    {
        Intent intent = new Intent(this, WaypointsActivity.class);
        startActivity(intent);
    }

    public void onClickSettings(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            lastMagnetometerReading = lowPassFilter(event.values.clone(), lastMagnetometerReading);
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            lastAccelerometerReading = lowPassFilter(event.values.clone(), lastAccelerometerReading);
        }

        if (lastMagnetometerReading != null && lastAccelerometerReading != null)
        {
            compassUpdateCounter += 1;
            if (compassUpdateCounter > 5)
            {
                compassUpdateCounter = 0;

                float relativeHeading = 0;
                if (lastBearingToTarget > 0)
                {
                    float[] R = new float[9];
                    SensorManager.getRotationMatrix(R, null, lastAccelerometerReading, lastMagnetometerReading);
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);

                    float azimuth = (float)Math.toDegrees(orientation[0]);
                    if (azimuth < 0)
                    {
                        azimuth += 360;
                    }
                    relativeHeading = ((((azimuth - lastBearingToTarget) % 360) + 540) % 360) - 180;

                    bearingValueText.setText(String.valueOf(Math.abs(Math.round(relativeHeading))));
                }

                Matrix bezelRotMatrix = new Matrix();
                bezelImage.setScaleType(ImageView.ScaleType.MATRIX);
                bezelRotMatrix.postRotate(-relativeHeading, bezelImage.getLeft() + bezelImage.getWidth() / 2, bezelImage.getTop() + bezelImage.getHeight() / 2);
                bezelImage.setImageMatrix(bezelRotMatrix);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    private float [] lowPassFilter(float [] input, float [] output)
    {
        if (output == null)
        {
            return input;
        }

        for (int i = 0; i < input.length; i++)
        {
            output[i] = output[i] + 0.2f*(input[i] - output[i]);
        }

        return output;
    }
}
