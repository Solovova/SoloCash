package test;

import db.DBException;
import db.DBMain;
import db.RecordAccounts;
import db.RecordMoves;

import java.lang.reflect.Array;
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

    public static void fillTestData(DBMain dbMain){
        mes(dbMain.createEmptyTable());

        List<RecordAccounts> accounts = new ArrayList<RecordAccounts>();
        accounts.add(new RecordAccounts(dbMain,1,"visa"));
        accounts.add(new RecordAccounts(dbMain,-1,"bank"));
        accounts.add(new RecordAccounts(dbMain,-1,"pocket"));
        accounts.add(new RecordAccounts(dbMain,-1,"visa07"));

        for (int i = 0; i < 10; i++) {
            accounts.add(new RecordAccounts(dbMain,-1,"test" + i));
        }

        for (RecordAccounts recordAccount:accounts) {
            try {
                recordAccount.insert();
            } catch (DBException e) {
                e.printStackTrace();
            }
        }

        List<String[]> moves = new ArrayList<String[]>();
        for (int i = 0; i < 1000; i++) {
            int intFrom = getRandomNumberInRange(1,9);
            int intTo = getRandomNumberInRange(1,9);
            double sum = getRandomNumberInRange(1,100);
            String[] str = {"test"+intFrom,"test"+intTo,Double.toString(sum)};
            moves.add(str);
        }

        try {
            for (String[] move:moves) {
                new RecordMoves(dbMain, -1, new RecordAccounts(dbMain, -1, move[0]), new RecordAccounts(dbMain, -1, move[1]),Double.parseDouble(move[2])).insert();
            }
        } catch (DBException e) {
            e.printStackTrace();
        }

        //mes(dbMain.tableMoves.addMove("visa","bank", 10.56));
        //mes(dbMain.tableMoves.addMove("bank","visa", 12.30));
    }
}
