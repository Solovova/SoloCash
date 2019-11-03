package db;

public class DBSQLRequests {
    final static String SQL_TEST_CREATE_EMPTY_TABLE = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "id serial PRIMARY KEY," +
                    "name VARCHAR(25)" +
                    ");" +

                    "CREATE TABLE IF NOT EXISTS moves (" +
                    "id serial PRIMARY KEY," +
                    "time timestamp," +
                    "accountFrom INT references accounts(id)," +
                    "accountTo INT references accounts(id)," +
                    "sum decimal ," +
                    "describe VARCHAR(25)" +
                    ");";

    final static String SQL_CREATE_EMPTY_ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS %s (" +
            "id serial PRIMARY KEY," +
            "moves INT references moves(id)," +
            "time timestamp," +
            "sum decimal ," +
            "balance decimal DEFAULT 0);";


}
