package test;

import db.DBMain;

public class TestData {
    private static void mes(String message) {
        if (message != null) System.out.println(message);
    }

    public static void fillTestData(DBMain dbMain){
        mes(dbMain.createEmptyTable());
        mes(dbMain.addAccountByID(1,"visa"));
        mes(dbMain.addAccountByID(2,"bank"));
        mes(dbMain.addAccountByID(3,"pocket"));
        mes(dbMain.addAccountByID(4,"bank2"));

        mes(dbMain.addMove("visa","bank", 10.56));
        mes(dbMain.addMove("bank","visa", 12.30));
    }
}
