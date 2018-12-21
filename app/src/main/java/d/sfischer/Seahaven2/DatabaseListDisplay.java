package d.sfischer.Seahaven2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DatabaseListDisplay extends Activity implements View.OnClickListener {


    private Button ButtonSensorData, ButtonRefreshData;
    static List<Happening> databaseList;
    public ListView databaseListView;
    //public TextView databaseListView;
    static List<Integer> testList = new ArrayList <> ();
    //static StringBuilder databaseText = new StringBuilder();
    String[] databaseText;
    static List<String> databaseTextList = new ArrayList <> ();

    DatabaseInitializer mDatabaseHelper;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {


        //setup



        super.onCreate (savedInstanceState);


        setContentView (R.layout.activity_third);

        ButtonSensorData = findViewById(R.id.button_sensor_data);

        ButtonSensorData.setOnClickListener(this);


        ButtonRefreshData = findViewById(R.id.button_refresh_data);

        ButtonRefreshData.setOnClickListener (this);


        databaseListView = findViewById(R.id.text_database_list);


        //ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databaseList);


        //StringBuilder databaseText = new StringBuilder();
        //databaseText.setLength (0);
        //databaseText.append("Liste der Datenbankinhalte: \n \n");


        //databaseText = initDatabaseView (); databaseTextList


        /*
        if (databaseText != null){
            final ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < databaseText.length; ++i) {
                list.add(databaseText[i]);
            }
            ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            databaseListView.setAdapter (adapter);
        }

        */
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databaseTextList);

        //ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databaseTextList);
        databaseListView.setAdapter (adapter);

        //adapter.notifyDataSetChanged();
        //databaseListView.setText(databaseText);


        //String currentDBPath = getDatabasePath("happening-database").getAbsolutePath();
        //System.out.println (currentDBPath);





        databaseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: You Clicked on " + name);
                System.out.println ("!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + name);

                //Cursor data = mDatabaseHelper.getItemID(name); //get the id associated with that name
                //Cursor data = mDatabaseHelper. (name); //get the id associated with that name
                //DatabaseInitializer.getfromAsync (AppDatabase.getAppDatabase (this),name);
                int itemID = -1;


                /*
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                */
                if(itemID > -1){
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Intent DatabaseEditIntent = new Intent(DatabaseListDisplay.this, DatabaseEditor.class);
                    DatabaseEditIntent.putExtra("id",itemID);
                    DatabaseEditIntent.putExtra("name",name);
                    startActivity(DatabaseEditIntent);
                }
                else{
                    toastMessage("Fehler bei der Verarbeitung");
                }
            }
        });
        databaseTextList = initDatabaseView ();

    }


    private void launchActivity(Class className) {

        Intent intent = new Intent(this, className);
        startActivity(intent);
    }



    private List <String> initDatabaseView ()
    {
        //vllt adapter übergeben und statt liste sachen add direkt auf den adapter?

        DatabaseInitializer.getAllfromAsync (AppDatabase.getAppDatabase (this));

        if (databaseList != null) {

            for (Happening currentHappening : databaseList ) {

                if (!testList.contains (currentHappening.getUid ()))
                {
                    //int i=0;
                    testList.add (currentHappening.getUid ());
                    System.out.println ("Adding Stuff!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+ databaseList.size ());

                    //databaseText.append(currentHappening.getHappeningName ()).append(" || ").append(currentHappening.getStartDate ()).append(" || ").append (currentHappening.getInfo ()).append(" || ").append (currentHappening.getValue ()).append(
                    //        System.getProperty("line.separator"));
                    //databaseText[i]=(currentHappening.getHappeningName ()+(" || ")+(currentHappening.getStartDate ()+(" || ")+ (currentHappening.getInfo ())+(" || ")+ (currentHappening.getValue ())));
                    databaseTextList.add(currentHappening.getHappeningName ()+(" || ")+(currentHappening.getStartDate ()+(" || ")+ (currentHappening.getInfo ())+(" || ")+ (currentHappening.getValue ())));
                    //i=i+1;
                    System.out.println ("Adding Stuff!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+ databaseTextList.size ());


                }
            }

        }
        //System.out.println (databaseText);
        /* List<Happening> tail = null;
        if (databaseList != null) {
            tail = databaseList.subList(Math.max(databaseList.size() - 10, 0), databaseList.size());
        }
        if (tail != null) {
            for (Happening currentHappening : tail )
            {
                System.out.println ("+++++++++++++++++++++++++++++" + currentHappening.getHappeningName() +(" || ")+ currentHappening.getStartDate ()+ " || "+ currentHappening.getInfo ()+" || "+currentHappening.getValue ());
            }
        }*/


        return databaseTextList;


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
                ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databaseTextList);
                //ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databaseList);


                /*
                if (databaseText != null){
                    final ArrayList<String> list = new ArrayList<String>();
                    for (int i = 0; i < databaseText.length; ++i) {
                        list.add(databaseText[i]);
                    }
                    ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databaseTextList);
                    databaseListView.setAdapter (adapter);
                }
                */

                //notifyDataSetChanged();
                databaseListView.setAdapter (adapter);

                //mAdapter.notifyDataSetChanged();
                //databaseListView.setText(databaseText);

                //System.out.println (databaseText);

                databaseTextList = initDatabaseView ();
                break;


            default:
                break;
        }

    }


    //set an onItemClickListener to the ListView

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}


