package d.sfischer.sensorcollector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DatabaseListDisplay extends Activity implements View.OnClickListener {


    private Button ButtonSensorData, ButtonRefreshData;
    static List<Happening> databaseList;
    public TextView databaseTextView;
    static List<Integer> testList = new ArrayList <> ();
    static StringBuilder databaseText = new StringBuilder();

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


        //StringBuilder databaseText = new StringBuilder();
        //databaseText.setLength (0);
        //databaseText.append("Liste der Datenbankinhalte: \n \n");
        databaseText = initDatabaseView ();
        databaseTextView.setText(databaseText);







    }


    private void launchActivity(Class className) {

        Intent intent = new Intent(this, className);
        startActivity(intent);
    }



    private StringBuilder initDatabaseView ()
    {


        if (databaseList != null) {

            for (Happening currentHappening : databaseList ) {

                if (!testList.contains (currentHappening.getUid ()))
                {
                    testList.add (currentHappening.getUid ());
                    databaseText.append(currentHappening.getHappeningName ()).append(" || ").append(currentHappening.getStartDate ()).append(" || ").append (currentHappening.getInfo ()).append(" || ").append (currentHappening.getValue ()).append(
                            System.getProperty("line.separator"));

                }








            }
        }
        System.out.println (databaseText);
        return databaseText;


    }

    @Override
    public void onClick ( View view ) {
        switch (view.getId()) {

            case R.id.button_sensor_data:
                launchActivity(DataCollectionActivity.class);
                break;

            case R.id.button_refresh_data:

                //StringBuilder databaseTextButton = new StringBuilder();
                //databaseTextButton.setLength (0);
                //databaseTextButton.append("Liste der Datenbankinhalte: \n \n");
                databaseText = initDatabaseView ();
                databaseTextView.setText(databaseText);
                System.out.println (databaseText);


                break;


            default:
                break;
        }

    }
}


