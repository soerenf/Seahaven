package d.sfischer.Seahaven2;


class WlanNameAndCount {


    WlanNameAndCount ( ) {

    }


    private String mSsid;
    private int mTimesConnectedOverall;
    private int mTimesConnectedAtNight;


    String getSsid ( ) {
        return mSsid;
    }

    int getTimesConnectedOverall ( ) {
        return mTimesConnectedOverall;
    }

    int getTimesConnectedAtNight ( ) {
        return mTimesConnectedAtNight;
    }

    void setSsid ( String ssid ) {

        mSsid = ssid;
    }

    void setTimesConnectedOverall ( ) {
        mTimesConnectedOverall = mTimesConnectedOverall+1;

    }

    void setTimesConnectedAtNight ( ) {
        mTimesConnectedAtNight = mTimesConnectedAtNight+1;
    }


}

