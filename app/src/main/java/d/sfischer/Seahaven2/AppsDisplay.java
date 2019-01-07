package d.sfischer.Seahaven2;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;


public class AppsDisplay extends Activity implements View.OnClickListener {


        private static final String TAG = "InfoDisplay";

        private Button ButtonSensorData;

        @Override
        public void onCreate ( Bundle savedInstanceState )
        {
            super.onCreate (savedInstanceState);

            setContentView (R.layout.activity_apps);

            ButtonSensorData = findViewById(R.id.button_sensor_data);

            ButtonSensorData.setOnClickListener(this);



            StringBuilder appsText = new StringBuilder();

            appsText.append("Hier sind alle Paket-Namen der installierten Apps aufgeführt. \n");
            appsText.append("Der Paket Name der App der Uni Bonn lautet zum Beispiel `de.unibonn.android`. \n");
            appsText.append("Liste der gespeicherte Apps: \n \n \n \n ");



            PackageManager myPM = getPackageManager ();


            if (myPM != null) {
                List <ApplicationInfo> packages = myPM.getInstalledApplications (PackageManager.GET_META_DATA);

                int i = 0;
                for (ApplicationInfo applicationInfo : packages) {

                    i = i+ 1;
                    appsText.append (i+ ". "+ applicationInfo.packageName + " \n");
                }
            }

            appsText.append("\n \n \n \n \n \n \n \n\n \n \n \n");






            TextView appsTextView = findViewById(R.id.text_apps);
            appsTextView.setText(appsText);



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


