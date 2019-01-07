package d.sfischer.Seahaven2;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static d.sfischer.Seahaven2.DataCollectionActivity.gettime;

// von: https://www.journaldev.com/13629/okhttp-android-example-tutorial

public class OkHttpHandler extends AsyncTask <String, Void, String>{

    OkHttpClient client = new OkHttpClient ();

    @Override
    protected String doInBackground(String...params) {

        Request.Builder builder = new Request.Builder();
        builder.url(params[0]);
        Request request = builder.build();


        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //txtString.setText(s);
        //System.out.println(s);

        Gson gson = new GsonBuilder ().create();
        ResponseParser2 responseParser2 = gson.fromJson(s, ResponseParser2.class);

        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (DataCollectionActivity.getAppContext ()),"OkHttpHandler",gettime (), 0, 0, s);
        if(!(DataCollectionActivity.homeWlan.equals (DataCollectionActivity.connectedToSSID)  || DataCollectionActivity.homeBssid.equals (DataCollectionActivity.bssid)))
        {
            Hedwig.deliverNotification ("In Stadt: " + responseParser2.getCity (), 501, DataCollectionActivity.getAppContext (), "Location");
        }
        if(DataCollectionActivity.homeWlan.equals (DataCollectionActivity.connectedToSSID)  || DataCollectionActivity.homeBssid.equals (DataCollectionActivity.bssid))
        {
            Hedwig.deliverNotification ("Heimatstadt: " + responseParser2.getCity (), 2001, DataCollectionActivity.getAppContext (), "Home City");
        }
        // hier operationen auf s um das zu splitten? vllt abhängig von call?
    }




}

