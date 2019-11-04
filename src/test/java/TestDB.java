import db.DBException;
import db.DBMain;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDB {

    @Test
    public void testDB11(){
        DBMain dbMain = new DBMain("cashflow");
        if (!dbMain.isConnection()) {
            return;
        }

        try {
            dbMain.createEmptyTable();
            TestDBPrepare.fillTestData(dbMain);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assertEquals(0,dbMain.getSummaryBalance(),"Summary balance test");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBException e) {
            e.printStackTrace();
        }

        dbMain.close();
    }
}
