package db;

import java.sql.*;

public class DBMain {
    public DBPostgres dbPostgres;

    public DBMain(){
        dbPostgres = DBPostgres.create();
        if (!dbPostgres.isConnection()) return;
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
                    dbPostgres.executeSimple(String.format("DROP TABLE IF EXISTS %s;", "account_" + rs.getInt(1)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        dbPostgres.executeSimple("DROP TABLE IF EXISTS moves;");
        dbPostgres.executeSimple("DROP TABLE IF EXISTS accounts;");
    }

    public String createEmptyTable() {
        this.dropAllTable();
        dbPostgres.executeSimple(DBSQLRequests.SQL_TEST_CREATE_EMPTY_TABLE);
        return null;
    }

    public boolean isConnection() {
        return dbPostgres.isConnection();
    }
}
