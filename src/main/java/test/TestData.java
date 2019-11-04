package test;

import db.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestData {
    public static void fillTestData(DBMain db) {
        final int numAccounts = 3;
        final int numMoves = 10;

        for (int i = 0; i < numAccounts; i++) {
            try {
                RecordAccounts.createNew(db, "account" + i, new Timestamp(0)).insert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < numMoves; i++) {
            int accFrom = (i % numAccounts) + 1;
            int accTo = ((i + 3) % numAccounts) + 1;
            double sum = (i % 20)+1;
            System.out.println(i);
            try {
                RecordMoves.createNew(db, new Timestamp(System.currentTimeMillis()- (numMoves - i)*1000), RecordAccounts.createExists(db, accFrom), RecordAccounts.createExists(db, accTo), sum, "test" + i).insert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            RecordMoves.createNew(db, new Timestamp(System.currentTimeMillis() - (numMoves+20)*1000), RecordAccounts.createExists(db, 1), RecordAccounts.createExists(db, 2), 100, "test100").insert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recalculateTests(DBMain dbMain) {
        try {
            RecordMoves.createExists(dbMain,2).delete();
        } catch (DBException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
