import db.DBException;
import db.DBMain;
import db.RecordAccounts;
import db.RecordMoves;
import db.dataclas.TransactionType;

import java.sql.SQLException;
import java.sql.Timestamp;

public class TestDBPrepare {
    public static void fillTestData(DBMain db) {
        final int numAccounts = 10;
        final int numMoves = 1000;

        for (int i = 0; i < numAccounts; i++) {
            try {
                RecordAccounts.createNew(db, "account" + i, new Timestamp(0))
                        .transaction(TransactionType.insert, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long timeNow = System.currentTimeMillis();



        for (int i = 0; i < numMoves; i++) {
            int accFrom = (i % numAccounts) + 1;
            int accTo = ((i + 2) % numAccounts) + 1;
            double sum = (i % 20) + 1;
            System.out.println(i);
            try {
                RecordMoves.createNew(db, new Timestamp(timeNow - (numMoves - i) * 1000),
                        RecordAccounts.createExists(db, accFrom),
                        RecordAccounts.createExists(db, accTo),
                        sum, "test" + i)
                        .transaction(TransactionType.insert, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < numMoves; i++) {
            int accFrom = (i % numAccounts) + 1;
            int accTo = ((i + 2) % numAccounts) + 1;
            double sum = (i % 20) + 1;
            System.out.println(i);
            try {
                RecordMoves.createNew(db, new Timestamp(timeNow - (numMoves - i) * 1000 - 500),
                        RecordAccounts.createExists(db, accFrom),
                        RecordAccounts.createExists(db, accTo),
                        sum, "test" + i)
                        .transaction(TransactionType.insert, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void recalculateTests(DBMain dbMain) throws DBException, SQLException {
        RecordMoves.createNew(dbMain, new Timestamp(System.currentTimeMillis()),
                RecordAccounts.createExists(dbMain, 2),
                RecordAccounts.createExists(dbMain, 2),
                300000.0, "test")
                .transaction(TransactionType.insert, null);
    }
}


//        new HashMap<String,Object>() {{
//        put("rteer",1);
//        put("rteer1","fdf");
//
//        }};
