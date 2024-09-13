package kristianseng.easynav;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity
{
    private EditText updateFrequencyEditText;
    private CheckBox metricCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        GlobalData.reloadSettingsFromFile(this);

        updateFrequencyEditText = (EditText)findViewById(R.id.updateFrequencyEditText);
        metricCheckBox = (CheckBox)findViewById(R.id.metricCheckBox);

        updateFrequencyEditText.setText(String.valueOf(GlobalData.settings_updateFrequency));
        metricCheckBox.setChecked(GlobalData.settings_metric);
    }

    public void onClickReturn(View view)
    {
        String stringUpdateFrequency = updateFrequencyEditText.getText().toString();
        boolean boolMetric = metricCheckBox.isChecked();

        if (stringUpdateFrequency.length() == 0)
        {
            GlobalData.showAlertMessage(getResources().getString(R.string.alert_saveerror_settings), getResources().getString(R.string.alert_saveerror_content_msg), this);
            return;
        }

        int intUpdateFrequency = Integer.parseInt(stringUpdateFrequency);

        if (intUpdateFrequency < 1 || intUpdateFrequency > 60)
        {
            GlobalData.showAlertMessage(getResources().getString(R.string.alert_saveerror_settings), getResources().getString(R.string.alert_saveerror_updatefreq_msg), this);
            return;
        }

        GlobalData.updateSettingsMetric(boolMetric, this);
        GlobalData.updateSettingsUpdateFrequency(intUpdateFrequency, this);

        this.finish();
    }
}
