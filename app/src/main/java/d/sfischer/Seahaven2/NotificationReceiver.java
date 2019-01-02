package d.sfischer.Seahaven2;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import static d.sfischer.Seahaven2.DataCollectionActivity.callTiming;
import static d.sfischer.Seahaven2.DataCollectionActivity.gettime;
import static d.sfischer.Seahaven2.DataCollectionActivity.normalAudioTiming;
import static d.sfischer.Seahaven2.DataCollectionActivity.voipTiming;
//import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {




    // wie bekomme ich raus von welcher notfication ich komme über die ID von .notify?
    // close notification nach click
    // oder jedes eigenes Intent? also YES_ACTION_STILL, YES_ACTION_USB etc.?
    // vllt auch die FLAGS bei hedwig entscheident bei one shot immerhin nur einmal klicken...aber sonst keine verbesserung
    // immutable flag zeigt keine veränderung...
    //getActivity statt broadcast?
    // intent in xml anpassen zu singleXXX?


    @Override
    public void onReceive ( Context context, Intent intent ) {

        String action = intent.getAction ();

        Bundle extras = intent.getExtras ();

        // cancel brauch die ID mit der deliverNotification aufgerufen wurde containsKey den String aus add Extra also caller


        if (action != null && action.equals (Hedwig.YES_ACTION)) {

            System.out.println ("Yes was pressed ");

            if (extras != null) {

                if (extras.containsKey ("Driving")) {
                    System.out.println ("Driving ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (0);
                    }
                }
                if (extras.containsKey ("Bicycle")) {
                    System.out.println ("Bicycle ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (1);
                    }
                }
                if (extras.containsKey ("Foot")) {
                    System.out.println ("Foot ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (2);
                    }
                }
                if (extras.containsKey ("Running")) {
                    System.out.println ("Running ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (3);
                    }
                }
                if (extras.containsKey ("Still")) {
                    System.out.println ("Still ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);

                    //funktioniert, Zeit der Aktion wird festgehalten und nicht Zeit des klickens
                    //Long date = extras.getLong ("timing");

                    if (manager != null) {
                        manager.cancel (4);
                    }

                }

                if (extras.containsKey ("Tilting")) {
                    System.out.println ("Tilting ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (5);
                    }
                }
                if (extras.containsKey ("Walking")) {
                    System.out.println ("Walking ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (6);
                    }
                }
                if (extras.containsKey ("WTF")) {
                    System.out.println ("WTF ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (7);
                    }
                }
                if (extras.containsKey ("Battery Ok")) {
                    System.out.println ("Battery Ok ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (10);
                    }
                }
                if (extras.containsKey ("Battery Low")) {
                    System.out.println ("Battery Low ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (12);
                    }
                }
                if (extras.containsKey ("Plugged AC")) {
                    System.out.println ("AC ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (13);
                    }
                }
                if (extras.containsKey ("Plugged USB")) {
                    System.out.println ("USB ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (14);
                    }

                }

                if (extras.containsKey ("Wecker"))
                {
                    System.out.println ("Wecker ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    String time = extras.getString ("timing");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Wecker war gestellt auf: ",time, 0, 1, time);
                    if (manager != null) {
                        manager.cancel (99);
                    }
                }

                if (extras.containsKey ("Telefonat"))
                {
                    System.out.println ("Telefonat ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    String time = extras.getString ("timing");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Telefonat aktiv ",gettime (), 0, 1,"von: "+ callTiming + "bis: "+ normalAudioTiming);
                    if (manager != null) {
                        manager.cancel (98);
                    }
                }

                if (extras.containsKey ("VOIP-Telefonat"))
                {
                    System.out.println ("VOIP-Telefonat ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    String time = extras.getString ("timing");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"VOIP-Telefonat aktiv ",gettime (), 0, 1, "von: "+ voipTiming + "bis: "+ normalAudioTiming);
                    if (manager != null) {
                        manager.cancel (97);
                    }
                }
            }
        }
        if (action != null && action.equals (Hedwig.NO_ACTION)) {
            System.out.println ("No was pressed ");

            if (extras != null) {

                if (extras.containsKey ("Driving")) {
                    System.out.println ("Driving ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (0);
                    }
                }
                if (extras.containsKey ("Bicycle")) {
                    System.out.println ("Bicycle ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (1);
                    }
                }
                if (extras.containsKey ("Foot")) {
                    System.out.println ("Foot ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (2);
                    }
                }
                if (extras.containsKey ("Running")) {
                    System.out.println ("Running ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (3);
                    }
                }
                if (extras.containsKey ("Still")) {
                    System.out.println ("Still ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (4);
                    }
                }

                if (extras.containsKey ("Tilting")) {
                    System.out.println ("Tilting ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (5);
                    }
                }
                if (extras.containsKey ("Walking")) {
                    System.out.println ("Walking ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (6);
                    }
                }
                if (extras.containsKey ("WTF")) {
                    System.out.println ("WTF ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (7);
                    }
                }
                if (extras.containsKey ("Battery Ok")) {
                    System.out.println ("Battery Ok ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (10);
                    }
                }
                if (extras.containsKey ("Battery Low")) {
                    System.out.println ("Battery Low ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (12);
                    }
                }
                if (extras.containsKey ("Plugged AC")) {
                    System.out.println ("AC ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (13);
                    }
                }
                if (extras.containsKey ("Plugged USB")) {
                    System.out.println ("USB ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (14);
                    }
                }

                if (extras.containsKey ("Wecker"))
                {
                    System.out.println ("Wecker ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    String time = extras.getString ("timing");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Wecker war gestellt auf: ",time, 0, 0, time);
                    if (manager != null) {
                        manager.cancel (99);
                    }
                }

                if (extras.containsKey ("Telefonat"))
                {
                    System.out.println ("Telefonat ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    String time = extras.getString ("timing");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Telefonat aktiv ",gettime (), 0, 0,"von: "+ callTiming + "bis: "+ normalAudioTiming);
                    if (manager != null) {
                        manager.cancel (98);
                    }
                }

                if (extras.containsKey ("VOIP-Telefonat"))
                {
                    System.out.println ("VOIP-Telefonat ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    String time = extras.getString ("timing");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"VOIP-Telefonat aktiv ",gettime (), 0, 0, "von: "+ voipTiming + "bis: "+ normalAudioTiming);
                    if (manager != null) {
                        manager.cancel (97);
                    }
                }
            }
        }
        if (action != null && action.equals (Hedwig.MAYBE_ACTION)) {
            System.out.println ("Maybe was pressed ");
            if (extras != null) {

                if (extras.containsKey ("Driving")) {
                    System.out.println ("Driving ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (0);
                    }
                }
                if (extras.containsKey ("Bicycle")) {
                    System.out.println ("Bicycle ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (1);
                    }
                }
                if (extras.containsKey ("Foot")) {
                    System.out.println ("Foot ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (2);
                    }
                }
                if (extras.containsKey ("Running")) {
                    System.out.println ("Running ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (3);
                    }
                }
                if (extras.containsKey ("Still")) {
                    System.out.println ("Still ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (4);
                    }
                }

                if (extras.containsKey ("Tilting")) {
                    System.out.println ("Tilting ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (5);
                    }
                }
                if (extras.containsKey ("Walking")) {
                    System.out.println ("Walking ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (6);
                    }
                }
                if (extras.containsKey ("WTF")) {
                    System.out.println ("WTF ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (7);
                    }
                }
                if (extras.containsKey ("Battery Ok")) {
                    System.out.println ("Battery Ok ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (10);
                    }
                }
                if (extras.containsKey ("Battery Low")) {
                    System.out.println ("Battery Low ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (12);
                    }
                }
                if (extras.containsKey ("Plugged AC")) {
                    System.out.println ("AC ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (13);
                    }
                }
                if (extras.containsKey ("Plugged USB")) {
                    System.out.println ("USB ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel (14);
                    }
                }

                if (extras.containsKey ("Wecker"))
                {
                    System.out.println ("Wecker ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    String time = extras.getString ("timing");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Wecker war gestellt auf: ",time, 0, 2, time);
                    if (manager != null) {
                        manager.cancel (99);
                    }
                }

                if (extras.containsKey ("Telefonat"))
                {
                    System.out.println ("Telefonat ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    String time = extras.getString ("timing");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Telefonat aktiv ",gettime (), 0, 2,"von: "+ callTiming + "bis: "+ normalAudioTiming);
                    if (manager != null) {
                        manager.cancel (98);
                    }
                }

                if (extras.containsKey ("VOIP-Telefonat"))
                {
                    System.out.println ("VOIP-Telefonat ");
                    NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
                    String time = extras.getString ("timing");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"VOIP-Telefonat aktiv ",gettime (), 0, 2, "von: "+ voipTiming + "bis: "+ normalAudioTiming);
                    if (manager != null) {
                        manager.cancel (97);
                    }
                }
            }

        }
    }
}