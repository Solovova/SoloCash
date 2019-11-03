package test;

import db.DBException;
import db.DBMain;
import db.RecordAccounts;

import java.util.ArrayList;
import java.util.List;

public class TestData {
    private static void mes(String message) {
        if (message != null) System.out.println(message);
    }

    public static void fillTestData(DBMain dbMain){
        mes(dbMain.createEmptyTable());

        RecordAccounts[] accounts = {
                new RecordAccounts(dbMain,1,"visa"),
                new RecordAccounts(dbMain,-1,"bank"),
                new RecordAccounts(dbMain,-1,"pocket"),
                new RecordAccounts(dbMain,-1,"pocket"),
                new RecordAccounts(dbMain,-1,"visa2")
        };

        for (RecordAccounts recordAccount:accounts) {
            try {
                recordAccount.insert();
            } catch (DBException e) {
                e.printStackTrace();
            }
        }


        //mes(dbMain.tableMoves.addMove("visa","bank", 10.56));
        //mes(dbMain.tableMoves.addMove("bank","visa", 12.30));
    }
}
