package d.sfischer.sensorcollector;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


// https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9
// bzw: https://github.com/ajaysaini-sgvu/room-persistence-sample/blob/master/app/src/main/java/com/nagarro/persistence/

@Entity(tableName = "happening")
public class Happening {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "age")
    private int age;

    @ColumnInfo(name = "happening_name")
    private String happeningName;

    @ColumnInfo(name = "start_date")
    private long startDate;

    // wie feststellen? durch nächste neue Aktion? oder gar nicht als eintrag sondern über query? -> wird ungenau sein
    //@ColumnInfo(name = "end_date")
    //private long endDate;

    // three values, yes = 1, no = 0 and maybe = 2  -> default value = no ??
    @ColumnInfo(name = "user_approved")
    private int userApproved;



    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHappeningName() {
        return happeningName;
    }

    public void setHappeningName(String happening) {
        this.happeningName = happening;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
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


}
