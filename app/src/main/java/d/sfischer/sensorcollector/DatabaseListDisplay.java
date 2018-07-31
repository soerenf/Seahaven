package d.sfischer.sensorcollector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class DatabaseListDisplay extends Activity implements View.OnClickListener {


    private Button ButtonSensorData, ButtonRefreshData;
    static List<Happening> databaseList;
    static TextView databaseTextView;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {


        //setup

        super.onCreate (savedInstanceState);


        setContentView (R.layout.activity_third);

        ButtonSensorData = findViewById(R.id.button_sensor_data);

        ButtonSensorData.setOnClickListener(this);


        ButtonRefreshData = findViewById(R.id.button_refresh_data);

        ButtonRefreshData.setOnClickListener (this);


        databaseTextView = findViewById(R.id.text_database_list);



        databaseTextView.setText(initDatabaseView ());







    }


    private void launchActivity(Class className) {

        Intent intent = new Intent(this, className);
        startActivity(intent);
    }



    private StringBuilder initDatabaseView ()
    {
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
        return databaseText;


    }

    @Override
    public void onClick ( View view ) {
        switch (view.getId()) {

            case R.id.button_sensor_data:
                launchActivity(DataCollectionActivity.class);
                break;

            case R.id.button_refresh_data:


                databaseTextView.setText(initDatabaseView ());
                System.out.println ("Refresh pressed");


                break;


            default:
                break;
        }

    }
}


