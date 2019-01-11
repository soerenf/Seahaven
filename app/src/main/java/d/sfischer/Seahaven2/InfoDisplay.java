package d.sfischer.Seahaven2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class InfoDisplay extends Activity implements View.OnClickListener {


    private static final String TAG = "InfoDisplay";

    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate (savedInstanceState);

        setContentView (R.layout.activity_info);

        Button buttonSensorData = findViewById (R.id.button_sensor_data);

        buttonSensorData.setOnClickListener (this);



        StringBuilder infoText = new StringBuilder();

        infoText.append ("Diese App dient dem Aufzeigen möglicher Informationsgewinnung.\n");
        infoText.append ("Dem Nutzer soll eine Auswahl an Aspekten aufgezeigt werden, die jede installierte App von ihm oder ihr erheben kann, ohne dass dafür Anzeichen zu erkennen wären.\n");
        infoText.append ("Hierzu benötigt diese App keine Berechtigungen, die durch den Nutzer bestätigt werden müssten (wie z. B. GPS-Standort oder Zugriff auf die Kontakte).\n");


        infoText.append ("Um dem/r Nutzer/in die Möglichkeit zu geben, selbst zu entscheiden ob Daten gesammelt werden, führt die App Aktionen nur durch wenn Sie geöffnet ist.\n");
        infoText.append ("Dann jedoch ohne, dass weiteres durch den Nutzer getan werden muss. Der Button `Datensammlung starten` dient lediglich dazu, selbst eine weitere Sammelaktion zu starten.\n");
        infoText.append("Es schadet also nicht diesen Knopf regelmäßig zu nutzen.\n \n");

        infoText.append ("Um die weitere Sammlung von Daten zu unterbinden, muss die App lediglich geschlossen werden.\n");
        infoText.append ("Alle Daten die erhoben werden verbleiben auf dem Gerät und werden zu keinem Zeitpunkt versendet.\n \n");

        infoText.append ("Darüber hinaus können alle gespeicherten Einträge über den Button `Datenbankübersicht` im Menü erreicht werden.\n");
        infoText.append ("Vorher erkannte Daten, die zum Profiling genutzt werden könnten, werden nicht gespeichert.\n");
        infoText.append ("Stattdessen wird nur die Art der Aktion gespeichert (z. B. es wird nur festgehalten, dass der Standort erkannt wurde, aber nicht wo sich der/die Nutzer/in aufgehalten hat).\n");
        infoText.append ("Sollten trotzdem Informationen aufgeführt sein, die nicht in die Studie einfließen sollen, kann durch einen Klick auf den jeweiligen Eintrag eine Ansicht geöffnet werden, um den Eintrag zu löschen.\n \n \n");

        infoText.append ("Außerdem sind weitere Informationen einsehbar, die jedoch nicht durch die App gespeichert werden.\n");
        infoText.append ("Diese Informationen können folglich auch durch jede andere App erhoben werden.\n");
        infoText.append ("Über den Button `Gespeicherte WLANs` können die WLANs angezeigt werden mit denen das Gerät bereits mindestens einmal verbunden war.\n");
        infoText.append ("Dasselbe gilt für `Installierte Apps`.\n \n \n");

        infoText.append ("Der Name des Programms `Seahaven` stammt vom Namen der fiktiven Stadt aus dem Film `Die Truman Show`.\n");


        infoText.append ("\n \n \n \n");


        TextView infoTextView = findViewById(R.id.text_info);
        infoTextView.setText(infoText);



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
