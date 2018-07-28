package d.sfischer.sensorcollector;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;


// https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9
// bzw: https://github.com/ajaysaini-sgvu/room-persistence-sample/blob/master/app/src/main/java/com/nagarro/persistence/utils/DatabaseInitializer.java#L10

public class DatabaseInitializer {

    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static Happening addHappening( final AppDatabase db, Happening happening ) {
        db.happeningDao().insertAll(happening);
        return happening;
    }

    private static void populateWithTestData(AppDatabase db) {
        Happening happening = new Happening ();
        happening.setFirstName("Ajay");
        happening.setLastName("Saini");
        happening.setAge(25);
        addHappening(db, happening);

        List<Happening> happeningList = db.happeningDao().getAll();
        System.out.println ("############################# Happening-List:  "+ happeningList.get (0));
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + happeningList.size());
    }






    public static void getfromAsync(@NonNull final AppDatabase db,String firstName, String lastName) {
        GetFromDbAsync task = new GetFromDbAsync (db, firstName, lastName);
        task.execute();
    }

    private static void getFromDb (AppDatabase db,String firstName, String lastName){
        Happening happening = db.happeningDao().findByName (firstName, lastName);
        if(happening != null)
        {
            System.out.println ("############################# Name und Alter :  "+ happening.getFirstName ()+" "+ happening.getLastName ()+" "+ happening.getAge ());
        }
        else
            {
                System.out.println ("############################# No such happening found!");
                //Hedwig.deliverNotification ("No such happening found!",465, DataCollectionActivity.getAppContext (),"getfromDB");
            }

     }







    public static void addToAsync(@NonNull final AppDatabase db, String firstName, String lastName, int age) {
        AddToDbAsync task = new AddToDbAsync (db, firstName,lastName,age);
        task.execute();
    }

    private static void addToDb (AppDatabase db, String firstName, String lastName, int age){
        Happening happening = new Happening ();
        happening.setFirstName (firstName);
        happening.setLastName (lastName);
        happening.setAge (age);
        addHappening (db, happening);
        System.out.println ("############################# Name:  "+ happening.getFirstName ()+" "+ happening.getLastName ());
    }








     private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }

    }







    private static class GetFromDbAsync extends AsyncTask<Void, Void,Void>{

        private final AppDatabase mDb;
        private final String mFirstName;
        private final String mLastName;


        GetFromDbAsync(AppDatabase db, String firstName, String lastName) {
            mDb = db;
            mFirstName = firstName;
            mLastName = lastName;

        }

        @Override
        protected Void doInBackground(final Void... params) {
            getFromDb (mDb, mFirstName, mLastName);
            return null;
        }

    }









    private static class AddToDbAsync extends AsyncTask<String, Void,Void>{

        private final AppDatabase mDb;
        private final String mFirstName;
        private final String mLastName;
        private final int mAge;

        AddToDbAsync ( AppDatabase db, String firstName, String lastName, int age ) {
            mDb = db;
            mFirstName = firstName;
            mLastName = lastName;
            mAge = age;

        }

        @Override
        protected Void doInBackground(final String... params) {
            addToDb (mDb, mFirstName, mLastName, mAge);
            return null;
        }

    }

}