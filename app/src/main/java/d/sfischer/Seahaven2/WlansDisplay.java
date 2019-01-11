package d.sfischer.Seahaven2;


import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;


public class WlansDisplay extends Activity implements View.OnClickListener {


    private static final String TAG = "InfoDisplay";

    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate (savedInstanceState);

        setContentView (R.layout.activity_wlans);

        Button buttonSensorData = findViewById (R.id.button_sensor_data);

        buttonSensorData.setOnClickListener (this);
        List<WifiConfiguration> netConfig;



        StringBuilder wlanText = new StringBuilder();

        wlanText.append ("Hier werden alle Namen (SSIDs) gespeicherter WLANs angezeigt. \n \n \n");

        WifiManager myWM = (WifiManager) getApplicationContext ().getSystemService (WIFI_SERVICE);


        if (myWM != null)
        {
            netConfig = myWM.getConfiguredNetworks ();


            if (netConfig != null) {

                int i = 0;
                for (WifiConfiguration currentWifiConfiguration : netConfig) {


                    String ssid = currentWifiConfiguration.SSID.replace ("\"", "");

                    i= i+1;
                    wlanText.append (i).append (". ").append (ssid).append ("\n");
                }
            }
        }
        else
            {
                wlanText.append ("Keine WLANs gespeichert.\n");
            }


        wlanText.append ("\n \n \n \n \n \n \n \n");

        TextView wlanTextView = findViewById(R.id.text_wlans);
        wlanTextView.setText(wlanText);



    }

    private void launchActivity(Class className) {

        Intent intent = new Intent(this, className);
        startActivity(intent);
    }
    @Override
    public void onClick ( View view ) {

        switch (view.getId())
        {

            case R.id.button_sensor_data:
                launchActivity (DataCollectionActivity.class);
                break;
            default:
                break;
        }
    }

}


