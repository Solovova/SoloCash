package db;

import java.sql.*;

public class DBMain {
    final private static String SQL_CREATE_EMPTY_TABLE = "CREATE TABLE IF NOT EXISTS accounts (" +
            "id serial PRIMARY KEY," +
            "name VARCHAR(25)," +
            "timeModify timestamp" +
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

    public DBPostgres dbPostgres;

    public DBMain() {
        dbPostgres = DBPostgres.create();
        if (!dbPostgres.isConnection()) return;
    }

    public void close() {
        dbPostgres.close();
    }

    private void dropAllTable() throws SQLException {
        ResultSet rs = dbPostgres.executeQuery(String.format("SELECT TABLE_NAME FROM cashflow.INFORMATION_SCHEMA.TABLES;"));
        while (rs.next()) {
            if (rs.getString(1).startsWith(RecordAccount.tableName)) {
                dbPostgres.executeSimple(String.format("DROP TABLE IF EXISTS %s;", rs.getString(1)));
            }

        }
        dbPostgres.executeSimple("DROP TABLE IF EXISTS moves;");
        dbPostgres.executeSimple("DROP TABLE IF EXISTS accounts;");
    }

    public void createEmptyTable() throws SQLException {
        this.dropAllTable();
        dbPostgres.executeSimple(SQL_CREATE_EMPTY_TABLE);
    }

    public boolean isConnection() {
        return dbPostgres.isConnection();
    }
}
