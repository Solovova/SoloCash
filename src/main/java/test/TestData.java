package test;

import db.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestData {
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private static void mes(String message) {
        if (message != null) System.out.println(message);
    }

    public static void fillTestData(DBMain db) {
        List<String> accounts = Arrays.asList("bank", "pocket", "visa") ;
        for (String account:accounts) {
            try {
                RecordAccounts.create(db,account,new Timestamp(0)).insert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        try {
//            RecordAccounts recordAccounts = RecordAccounts.create(db,"visa", new Timestamp(0));
//            recordAccounts.insert();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        try {
//            RecordAccounts recordAccounts = RecordAccounts.create(db,2);
//            recordAccounts.modify("solov",new Timestamp(System.currentTimeMillis()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            RecordAccounts.create(db,1).delete();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            RecordMoves.create(db,new Timestamp(System.currentTimeMillis()),RecordAccounts.create(db,1),RecordAccounts.create(db,2),10.4,"test1").insert();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            RecordAccounts.create(db,1).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < 10; i++) {
//            accounts.add(new RecordAccounts(dbMain, -1, "test" + i));
//        }
//
//        for (RecordAccounts recordAccount : accounts) {
//            try {
//                recordAccount.insert();
//            } catch (DBException e) {
//                e.printStackTrace();
//            }
//        }
//
//        List<String[]> moves = new ArrayList<String[]>();
//        for (int i = 0; i < 2000; i++) {
//            int intFrom = getRandomNumberInRange(1, 9);
//            int intTo = getRandomNumberInRange(1, 9);
//            double sum = getRandomNumberInRange(1, 100);
//            String[] str = {"test" + intFrom, "test" + intTo, Double.toString(sum)};
//            moves.add(str);
//        }
//
//        try {
//            for (int i = 0; i < moves.size(); i++) {
//                String [] move = moves.get(i);
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis() - (moves.size() - i)*1000);
//                new RecordMoves(dbMain, -1, new RecordAccounts(dbMain, -1, move[0]), new RecordAccounts(dbMain, -1, move[1]), Double.parseDouble(move[2]), timestamp).insert();
//                System.out.println(i);
//            }
//        } catch (DBException e) {
//            e.printStackTrace();
//        }
//
//        moves.clear();
//        for (int i = 0; i < 1000; i++) {
//            int intFrom = getRandomNumberInRange(1, 9);
//            int intTo = getRandomNumberInRange(1, 9);
//            double sum = getRandomNumberInRange(1, 100);
//            String[] str = {"test" + intFrom, "test" + intTo, Double.toString(sum)};
//            moves.add(str);
//        }
//
//        try {
//            for (int i = 0; i < moves.size(); i++) {
//                String [] move = moves.get(i);
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis() - (moves.size() - i)*1000-500);
//                new RecordMoves(dbMain, -1, new RecordAccounts(dbMain, -1, move[0]), new RecordAccounts(dbMain, -1, move[1]), Double.parseDouble(move[2]), timestamp).insert();
//                System.out.println(i);
//            }
//        } catch (DBException e) {
//            e.printStackTrace();
//        }
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
