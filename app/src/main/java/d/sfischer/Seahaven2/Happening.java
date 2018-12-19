package d.sfischer.Seahaven2;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


// https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9
// bzw: https://github.com/ajaysaini-sgvu/room-persistence-sample/blob/master/app/src/main/java/com/nagarro/persistence/

@Entity(tableName = "happening")
public class Happening {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "happening_name")
    private String happeningName;

    @ColumnInfo(name = "start_date")
    private String startDate;

    // wie feststellen? durch nächste neue Aktion? oder gar nicht als eintrag sondern über query? -> wird ungenau sein
    //@ColumnInfo(name = "end_date")
    //private long endDate;

    // three values, yes = 1, no = 0 and maybe = 2  -> default value = no ??
    @ColumnInfo(name = "user_approved")
    private int userApproved;


    //z.B. Activity Confidence oder Sensor Value
    @ColumnInfo(name = "value")
    private int value;

    //z.B. Activity Confidence oder Sensor Value
    @ColumnInfo(name = "info")
    private String info;





    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getHappeningName() {
        return happeningName;
    }

    public void setHappeningName(String happening) {
        this.happeningName = happening;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    //public long getEndDate() {return startDate; }

    //public void setEndDate(long endDate) { this.endDate = endDate; }

    public int getUserApproved() {
        return userApproved;
    }

    public void setUserApproved(int userApproved) {
        this.userApproved = userApproved;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


}
