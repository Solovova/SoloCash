import db.DBException;
import db.DBMain;
import db.RecordAccounts;
import db.RecordMoves;

import java.sql.SQLException;
import java.sql.Timestamp;

public class TestDBPrepare {
    public static void fillTestData(DBMain db) {
        final int numAccounts = 10;
        final int numMoves = 1000;

        for (int i = 0; i < numAccounts; i++) {
            try {
                RecordAccounts.createNew(db, "account" + i, new Timestamp(0)).insert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long timeNow = System.currentTimeMillis();

        for (int i = 0; i < numMoves; i++) {
            int accFrom = (i % numAccounts) + 1;
            int accTo = ((i + 2) % numAccounts) + 1;
            double sum = (i % 20)+1;
            System.out.println(i);
            try {
                RecordMoves.createNew(db, new Timestamp(timeNow - (numMoves - i)*1000), RecordAccounts.createExists(db, accFrom), RecordAccounts.createExists(db, accTo), sum, "test" + i).insert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < numMoves; i++) {
            int accFrom = (i % numAccounts) + 1;
            int accTo = ((i + 2) % numAccounts) + 1;
            double sum = (i % 20)+1;
            System.out.println(i);
            try {
                RecordMoves.createNew(db, new Timestamp(timeNow - (numMoves - i)*1000-500), RecordAccounts.createExists(db, accFrom), RecordAccounts.createExists(db, accTo), sum, "test" + i).insert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void recalculateTests(DBMain dbMain) {
        try {
            //RecordMoves.createExists(dbMain,2).delete();
            //RecordMoves.createExists(dbMain,2).modify(null,null,RecordAccounts.createExists(dbMain,2), null,null);
            System.out.println(RecordAccounts.createExists(dbMain,2).getBalance());
        } catch (DBException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            dbMain.getSummaryBalance();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBException e) {
            e.printStackTrace();
        }
    }
}
