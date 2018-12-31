package d.sfischer.Seahaven2;

import android.app.Activity;
import android.content.Context;
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


    private static final String TAG = "DatabaseListDisplay";
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

        databaseTextList = initDatabaseView ();
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databaseTextList);

        //ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databaseTextList);
        databaseListView.setAdapter (adapter);

        ( (ArrayAdapter) adapter ).notifyDataSetChanged ();



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


                String[] tokens = name.split  ("#");
                System.out.println ("!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + tokens[0]);
                //DatabaseInitializer.getfromAsync (AppDatabase.getAppDatabase (this),tokens[1]);
                //DatabaseInitializer.getAllfromAsync (AppDatabase.getAppDatabase (DataCollectionActivity.getAppContext ()));



                //Cursor data = mDatabaseHelper.getItemID(name); //get the id associated with that name
                //Cursor data = mDatabaseHelper. (name); //get the id associated with that name
                //DatabaseInitializer.getfromAsync (AppDatabase.getAppDatabase (this),name);


                //int itemID = -1;


                /*
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                */
                if(Integer.parseInt(tokens[0]) > -1){
                    Log.d(TAG, "onItemClick: The ID is: " + tokens[0]);
                    Intent DatabaseEditIntent = new Intent(DatabaseListDisplay.this, DatabaseEditor.class);
                    DatabaseEditIntent.putExtra("id",Integer.parseInt(tokens[0]));


                    DatabaseEditIntent.putExtra("name",tokens[2]);
                    DatabaseEditIntent.putExtra("text",name);

                    startActivity(DatabaseEditIntent);
                }
                else{
                    toastMessage("Fehler bei der Verarbeitung");
                }
            }
        });
        databaseTextList = initDatabaseView ();
        //( (ArrayAdapter) adapter ).clear ();
        //( (ArrayAdapter) adapter ).addAll (databaseTextList);
        //( (ArrayAdapter) adapter ).notifyDataSetChanged ();

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

                    testList.add (currentHappening.getUid ());
                    System.out.println ("Adding Stuff!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+ databaseList.size ());

                    //databaseText.append(currentHappening.getHappeningName ()).append(" || ").append(currentHappening.getStartDate ()).append(" || ").append (currentHappening.getInfo ()).append(" || ").append (currentHappening.getValue ()).append(
                    //        System.getProperty("line.separator"));
                    //databaseText[i]=(currentHappening.getHappeningName ()+(" || ")+(currentHappening.getStartDate ()+(" || ")+ (currentHappening.getInfo ())+(" || ")+ (currentHappening.getValue ())));
                    databaseTextList.add(currentHappening.getUid ()+("#")+currentHappening.getHappeningName ()+("#")+ (currentHappening.getInfo ()));

                    // anpassen nur für relevante sachen, aber bachten das das split dadurch geänert sein könnte
                    // databaseTextList.add(currentHappening.getUid ()+(" || ")+currentHappening.getHappeningName ()+(" || ")+(currentHappening.getStartDate ()+(" || ")+ (currentHappening.getInfo ())+(" || ")+ (currentHappening.getValue ())));

                    System.out.println ("Adding Stuff!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+ databaseTextList.size ());


                }

                //
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


                databaseListView.setAdapter (adapter);
                databaseTextList = initDatabaseView ();
                ( (ArrayAdapter) adapter ).notifyDataSetChanged ();
                break;


            default:
                break;
        }

    }




    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //databaseTextList = null;
        databaseTextList = initDatabaseView ();
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databaseTextList);


        ( (ArrayAdapter) adapter ).notifyDataSetChanged ();
    }

    // Function to call update on adapter and remove one item
    public static void listViewChanged ( Context context, String name ) {
        ListAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, databaseTextList);

        ( (ArrayAdapter) adapter ).remove (name);

        ( (ArrayAdapter) adapter ).notifyDataSetChanged ();
    }
}


