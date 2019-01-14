package d.sfischer.Seahaven2;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;


// >https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9
// bzw: https://github.com/ajaysaini-sgvu/room-persistence-sample/blob/master/app/src/main/java/com/nagarro/persistence/

@Dao
public interface
HappeningDao {

    @Query("SELECT * FROM Happening")
    List<Happening> getAll();

    @Query("SELECT * FROM Happening where happening_name LIKE  :happeningName")
    Happening findByName( String happeningName);

    @Query("SELECT * FROM Happening where uid LIKE  :uid")
    Happening findByUID( int uid);

    @Query("SELECT COUNT(*) from Happening")
    int countHappenings();

    //@Query ("SELECT * FROM Happening where happening LIKE :happenIng")
    //Happening findByHappeningName( String happenIng);

    //@Query ("SELECT * FROM Happening where happening LIKE :happenIng and end_date < :actualDate ")
    //Happening findByHappeningNameAndEndDate( String happenIng, long actualDate);

    @Insert
    void insertAll(Happening... happenings );

    @Delete
    void delete(Happening happening );
}
