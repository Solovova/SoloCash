package db;

import db.requests.RequestContainer;

import java.sql.*;

public class DBMain {
    private RequestContainer requestContainer;

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

    public DBMain(String dbName) {
        dbPostgres = DBPostgres.create(dbName);
        requestContainer = new RequestContainer(this);
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
        rs.close();
        dbPostgres.executeSimple("DROP TABLE IF EXISTS moves;");
        dbPostgres.executeSimple("DROP TABLE IF EXISTS accounts;");
    }

    public void createEmptyTable() throws SQLException {
        this.dropAllTable();
        dbPostgres.executeSimple(SQL_CREATE_EMPTY_TABLE);
    }

    public double getSummaryBalance() throws SQLException, DBException {
        ResultSet rs = dbPostgres.executeQuery(String.format("SELECT id FROM accounts;"));
        double summaryBalance = 0.0;
        while (rs.next()) {
            double balance = RecordAccounts.createExists(this,rs.getInt(1)).getBalance();
            summaryBalance += balance;
            System.out.println("Account " + rs.getInt(1) + " : " + balance);
        }
        rs.close();
        System.out.println("Summary balance  : " + summaryBalance);
        return summaryBalance;
    }

    public boolean isConnection() {
        return dbPostgres.isConnection();
    }

    public String getAnswer(String request) {
        return requestContainer.getAnswer(request);
    }
}
