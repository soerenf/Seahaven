package d.sfischer.Seahaven2;

import java.util.List;

public class WlanNameAndCount {


    public WlanNameAndCount()
    {

    }



    String mSsid;
    int mTimesConnectedOverall;
    int mTimesConnectedAtNight;




    public String getSsid() {
        return mSsid;
    }

    public int getTimesConnectedOverall() {
        return mTimesConnectedOverall;
    }

    public int getTimesConnectedAtNight() {
        return mTimesConnectedAtNight;
    }

    public void setSsid(String ssid) {

        mSsid = ssid;
    }

    public void setTimesConnectedOverall() {
        mTimesConnectedOverall = mTimesConnectedOverall+1;

    }

    public void setTimesConnectedAtNight() {
        mTimesConnectedAtNight = mTimesConnectedAtNight+1;
    }







}

