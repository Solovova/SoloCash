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
        final int numAccounts = 10;
        final int numMoves = 10000;

        for (int i = 0; i < numAccounts; i++) {
            try {
                RecordAccounts.create(db, "account" + i, new Timestamp(0)).insert();
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
                RecordMoves.create(db, new Timestamp(System.currentTimeMillis()- (numMoves - i)*1000), RecordAccounts.create(db, accFrom), RecordAccounts.create(db, accTo), sum, "test" + i).insert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            RecordMoves.create(db, new Timestamp(System.currentTimeMillis() - (numMoves+20)*1000), RecordAccounts.create(db, 1), RecordAccounts.create(db, 2), 100, "test100").insert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recalculateTests(DBMain dbMain) {
//        try {
////            new RecordAccounts(dbMain, 12, "").recalculate(new Timestamp(1572798113000L));
//            //new RecordMoves(dbMain,23,null,null,0,null).delete();
//            new RecordMoves(dbMain,9,null,null,0,null).setSum(50);
//        } catch (DBException e) {
//            e.printStackTrace();
//        }
    }
}
