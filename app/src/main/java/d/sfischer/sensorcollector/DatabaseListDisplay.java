package d.sfischer.sensorcollector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class DatabaseListDisplay extends Activity{


    private Button ButtonSensorData;
    static List<Happening> databaseList;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {


        //setup

        super.onCreate (savedInstanceState);


        setContentView (R.layout.activity_third);
        ButtonSensorData = findViewById(R.id.button_sensor_data);

        ButtonSensorData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchActivity(DataCollectionActivity.class);
            }
        });



        //funktioniert aber verzögert und nicht live....




        StringBuilder databaseText = new StringBuilder();

        databaseText.append("Liste der Datenbankinhalte: \n \n");

         if (databaseList != null) {
            for (Happening currentHappening : databaseList ) {

                SimpleDateFormat sdf = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                String timing = sdf.format (currentHappening.getStartDate ());
                databaseText.append(currentHappening.getHappeningName ()).append(" event started at: ").append(timing).append(" User approved: ").append (currentHappening.getUserApproved ()).append(
                        System.getProperty("line.separator"));;



            }
        }

        TextView sensorTextView = findViewById(R.id.text_database_list);
        sensorTextView.setText(databaseText);






    }


    private void launchActivity(Class placeholder) {

        Intent intent = new Intent(this, placeholder);
        startActivity(intent);
    }
}


