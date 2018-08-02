package d.sfischer.sensorcollector;

import android.os.AsyncTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static d.sfischer.sensorcollector.DataCollectionActivity.gettime;

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
        System.out.println(s);
        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (DataCollectionActivity.getAppContext ()),"OkHttpHandler",gettime (), 0, 0, s);
    }




}

