import db.DBAtomic;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DBAtomic dbAtomic = new DBAtomic();
        if (!dbAtomic.isConnection()) {
            return;
        }

        dbAtomic.createEmptyTable();
        dbAtomic.fillTestData();
        dbAtomic.addAccount("Test1", true);

        for (int i = 10; i < 100; i++) {
            dbAtomic.addAccountWithID(i*2," Account " + i);
        }

        dbAtomic.addAccount("Test2", true);
        dbAtomic.addAccount("Test2", false);


//
//        DataBase.printAuthors();
        dbAtomic.close();
    }
}
