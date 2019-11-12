import db.DBException;
import db.DBMain;
import db.RecordMoves;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDBCreateExists {
    @Test
    public void testDB11() {
        DBMain dbMain = new DBMain("cashflow");
        if (!dbMain.isConnection()) {
            return;
        }

        try {
            RecordMoves recordMoves = RecordMoves.createExists(dbMain,299);
            assertEquals(19,recordMoves.sum,"recordMoves Sum");
            assertEquals("test298",recordMoves.describe,"recordMoves Describe");
            assertEquals(9,recordMoves.accountFrom.getId(),"recordMoves AccountFrom");
            assertEquals(1,recordMoves.accountTo.getId(),"recordMoves AccountTo");
            assertEquals("account8",recordMoves.accountFrom.name,"recordMoves AccountFrom name");
        } catch (DBException | SQLException e) {
            e.printStackTrace();
        }
    }
}
