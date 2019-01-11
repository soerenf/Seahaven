package d.sfischer.Seahaven2;

// aus https://github.com/mitchtabian/SaveReadWriteDeleteSQLite



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;


public class DatabaseEditor extends AppCompatActivity {

    private static final String TAG = "DatabaseEditor";

    private TextView editable_item;

    private String selectedName;
    private String selectedText;
    private int selectedID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Button btnCancel = findViewById (R.id.btnCancel);
        Button btnDelete = findViewById (R.id.btnDelete);
        editable_item = findViewById (R.id.editable_item);
        //mDatabaseHelper = new DatabaseHelper(this);

        //get the intent extra from the ListDataActivity
        Intent receivedIntent = getIntent();

        //now get the itemID we passed as an extra
        selectedID = receivedIntent.getIntExtra("id",-1); //NOTE: -1 is just the default value

        //now get the name we passed as an extra
        selectedName = receivedIntent.getStringExtra("name");

        selectedText =  receivedIntent.getStringExtra("text");

        //set the text to show the current selected name
        //editable_item.setText(selectedID+ " " + selectedName+ " " + selectedText);
        editable_item.setText (selectedText);
        btnCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {


                Intent DatabaseListDisplayIntent = new Intent(DatabaseEditor.this, DatabaseListDisplay.class);
                    startActivity(DatabaseListDisplayIntent);


            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editable_item.setText("");

                System.out.println ("++++++++++++++ Deleting entry with UID: "+ selectedID+" and name " +selectedName);
                DatabaseInitializer.removeFromAsync (AppDatabase.getAppDatabase (DataCollectionActivity.getAppContext ()), selectedID);
                toastMessage ("Eintrag: " + selectedID + " aus Datenbank entfernt.");


                //DatabaseListDisplay.databaseTextList = DatabaseListDisplay.initDatabaseView ();
                // muss ich via singleton machen oder wie????
                DatabaseListDisplay.listViewChanged(DataCollectionActivity.getAppContext (),selectedText);


                Intent DatabaseListDisplayIntent = new Intent(DatabaseEditor.this, DatabaseListDisplay.class);
                startActivity(DatabaseListDisplayIntent);

            }
        });

    }


    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}


