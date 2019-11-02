import db.DB;
import test.DBTestData;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DB db = new DB();
        if (!db.isConnection()) {
            return;
        }

        DBTestData.fillTestData(db);

        db.close();
    }
}
