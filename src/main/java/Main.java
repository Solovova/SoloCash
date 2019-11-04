import db.DBMain;
import test.TestData;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DBMain dbMain = new DBMain();
        if (!dbMain.isConnection()) {
            return;
        }

        try {
            dbMain.createEmptyTable();
            TestData.fillTestData(dbMain);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //TestData.recalculateTests(dbMain);

        dbMain.close();
    }
}
