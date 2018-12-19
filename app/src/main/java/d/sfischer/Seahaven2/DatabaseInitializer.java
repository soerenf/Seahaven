package d.sfischer.Seahaven2;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import static d.sfischer.Seahaven2.DataCollectionActivity.gettime;


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
        happening.setHappeningName ("TestData");
        happening.setStartDate (gettime());
        happening.setValue (0);
        addHappening(db, happening);

        List<Happening> happeningList = db.happeningDao().getAll();
        //System.out.println ("############################# Happening-List:  "+ happeningList.get (0));
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + happeningList.size());
    }




    public static void getfromAsync(@NonNull final AppDatabase db,String happeningName) {
        GetFromDbAsync task = new GetFromDbAsync (db, happeningName);
        task.execute();
    }

    private static void getFromDb (AppDatabase db,String happeningName){
        Happening happening = db.happeningDao().findByName (happeningName);
        if(happening != null)
        {
            System.out.println ("############################# Happening Name :  "+ happening.getHappeningName ());
        }
        else
            {
                System.out.println ("############################# No such happening found!");
                //Hedwig.deliverNotification ("No such happening found!",465, DataCollectionActivity.getAppContext (),"getfromDB");
            }

     }

    public static void getAllfromAsync(@NonNull final AppDatabase db) {
        GetAllFromDbAsync task = new GetAllFromDbAsync (db);
        task.execute();
    }

    private static void getAllFromDb (AppDatabase db){
        List<Happening> happeningList = db.happeningDao().getAll ();
        if(happeningList != null)
        {
            //System.out.println ("############################# Happening List size "+ happeningList.size ());
            DatabaseListDisplay.databaseList = happeningList;
           /* for (int i = 0; i < happeningList.size (); i++)
            {
                Happening test= (happeningList.get (i));
                System.out.println (test.getHappeningName ());
            } */
        }
        else
        {
            System.out.println ("############################# No such happening List found!");

        }

    }


    public static void addToAsync(@NonNull final AppDatabase db, String happeningName, String startDate, int value, int userApproved, String info) {
        AddToDbAsync task = new AddToDbAsync (db, happeningName,startDate, value,userApproved,info);
        task.execute();
    }

    private static void addToDb (AppDatabase db, String happeningName, String startDate, int value, int userApproved, String info){
        Happening happening = new Happening ();
        //happening.setFirstName (firstName);
        //happening.setLastName (lastName);
        //happening.setAge (age);
        happening.setHappeningName (happeningName);
        happening.setStartDate (startDate);
        //happening.setEndDate ();
        happening.setValue (value);
        happening.setUserApproved (userApproved);
        happening.setInfo (info);
        addHappening (db, happening);
        //System.out.println ("############################# Happening Name:  "+ happening.getHappeningName ()+" Happening Start: "+ happening.getStartDate ());
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
        //private final String mFirstName;
        //private final String mLastName;
        private final String mHappening;



        GetFromDbAsync(AppDatabase db, String happening) {
            mDb = db;
            //mFirstName = firstName;
            //mLastName = lastName;
            mHappening = happening;

        }

        @Override
        protected Void doInBackground(final Void... params) {
            getFromDb (mDb, mHappening);
            return null;
        }

    }


    private static class GetAllFromDbAsync extends AsyncTask<Void, Void,Void>{

        private final AppDatabase mDb;

        GetAllFromDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            getAllFromDb (mDb);
            return null;
        }

    }



    private static class AddToDbAsync extends AsyncTask<String, Void,Void>{

        private final AppDatabase mDb;
        //private final String mFirstName;
        //private final String mLastName;
        //private final int mAge;
        private final String mHappening;
        private final String mStartDate;
        //private final long mEndDate;
        private final int mValue;
        private final int mUserApproved;
        private final String mInfo;


        AddToDbAsync ( AppDatabase db, String happeningName, String startDate,int value, int userApproved, String info  ) {       // long endDate // ,String firstName, String lastName, int age
            mDb = db;
            //mFirstName = firstName;
            //mLastName = lastName;
            //mAge = age;
            mHappening = happeningName;
            mStartDate = startDate;
            //mEndDate = endDate;
            mValue = value;
            mUserApproved = userApproved;
            mInfo = info;

        }

        @Override
        protected Void doInBackground(final String... params) {
            addToDb (mDb, mHappening, mStartDate, mValue, mUserApproved, mInfo);                                     //  mHappening, mStartDate, mEndDate, mUserApproved
            return null;
        }

    }

}