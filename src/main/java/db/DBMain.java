package db;

import java.sql.*;
import java.text.DecimalFormat;

public class DBMain {
    public DBPostgres dbPostgres;
    public TableMoves tableMoves;
    public TableAccounts tableAccounts;
    public TableAccount tableAccount;



    public DBMain(){
        dbPostgres = DBPostgres.create();
        if (!dbPostgres.isConnection()) return;
        tableMoves = new TableMoves(this);
        tableAccounts = new TableAccounts(this);
        tableAccount = new TableAccount(this);
    }

    public void close() {
        dbPostgres.close();
    }

    private void dropAllTable() {
        if (dbPostgres.isTable("accounts")) {
            ResultSet rs = dbPostgres.executeQuery("SELECT id FROM accounts;");
            try {
                while (rs.next()) {
                    //System.out.println(String.format("DROP TABLE IF EXISTS %s;", "account_" + rs.getInt(1)));
                    dbPostgres.execute(String.format("DROP TABLE IF EXISTS %s;", "account_" + rs.getInt(1)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        dbPostgres.execute("DROP TABLE IF EXISTS moves;");
        dbPostgres.execute("DROP TABLE IF EXISTS accounts;");
    }

    public String createEmptyTable() {
        this.dropAllTable();
        dbPostgres.execute(DBSQLRequests.SQL_TEST_CREATE_EMPTY_TABLE);
        return null;
    }

    public boolean isConnection() {
        return dbPostgres.isConnection();
    }
}
