package test;


import db.DB;

public class DBTestData {
    public final static String SQL_TEST_CREATE_EMPTY_TABLE = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "id serial PRIMARY KEY," +
                    "name VARCHAR(25)" +
                    ");" +

                    "CREATE TABLE IF NOT EXISTS moves (" +
                    "id serial PRIMARY KEY," +
                    "time bigint," +
                    "accountFrom INT references accounts(id)," +
                    "accountTo INT references accounts(id)," +
                    "sum decimal ," +
                    "describe VARCHAR(25)" +
                    ");";

    public final static String SQL_TEST_DROP_ALL_TABLE = "DROP TABLE moves accounts";

    public final static String SQL_CREATE_EMPTY_ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS %s (" +
            "id serial PRIMARY KEY," +
            "moves INT references moves(id)," +
            "sum decimal ," +
            "balance decimal);";

    public static void fillTestData(DB db){
        db.dropAllTable();
        //db.createEmptyTable();
//        db.addAccountWithID(1,"visa");
//        db.addAccountWithID(2,"bank");
//        db.addAccountWithID(3,"pocket");
    }
}
