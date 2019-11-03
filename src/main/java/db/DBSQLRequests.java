package db;

public class DBSQLRequests {
    final static String SQL_TEST_CREATE_EMPTY_TABLE = "CREATE TABLE IF NOT EXISTS accounts (" +
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

    public final static String SQL_TEST_DROP_ALL_TABLE = "DROP TABLE IF EXISTS accounts";

    final static String SQL_CREATE_EMPTY_ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS %s (" +
            "id serial PRIMARY KEY," +
            "moves INT references moves(id)," +
            "sum decimal ," +
            "balance decimal);";


}
