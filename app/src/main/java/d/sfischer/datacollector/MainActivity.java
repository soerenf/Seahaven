package d.sfischer.datacollector;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import d.sfischer.sensorcollector.R;

public class MainActivity extends AppCompatActivity {


    private Button ButtonSensorData;
    private SensorManager mSensorManager;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Get the list of all sensors from the sensor manager. Store the list in a List object whose values are of type Sensor

        List<Sensor> sensorList  =
                null;
        if (mSensorManager != null) {
            sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        }


        //Iterate over the list of sensors. For each sensor, get that sensor's official name with the getName() method, and append that name to the sensorText string.
        // Each line of the sensor list is separated by the value of the line.separator property, typically a newline character

        StringBuilder sensorText = new StringBuilder();

        sensorText.append("Liste der Sensoren des Smartphones: \n \n");

        if (sensorList != null) {
            for (Sensor currentSensor : sensorList ) {
                sensorText.append(currentSensor.getName()).append(
                        System.getProperty("line.separator"));
            }
        }

        // Get a reference to the TextView for the sensor list, and update the text of that view with
        // the string containing the list of sensors:

        //Lines without letter/number codes are virtual or composite sensors, that is, sensors that are simulated in software.
        // These sensors use the data from one or more physical sensors.
        // So, for example, the gravity sensor may use data from the accelerometer, gyroscope,
        // and magnetometer to provide the direction and magnitude of gravity in the device's coordinate system.

        TextView sensorTextView = findViewById(R.id.text_sensor_list);
        sensorTextView.setText(sensorText);




        ButtonSensorData = findViewById(R.id.button_sensor_data);

        ButtonSensorData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchActivity(DataCollectionActivity.class);
            }
        });
    }

    public void launchActivity(Class placeholder) {

        Intent intent = new Intent(this, placeholder);
        startActivity(intent);
    }
}




