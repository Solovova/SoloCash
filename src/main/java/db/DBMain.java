package db;

import db.requests.RequestContainer;

import java.sql.*;
import java.util.logging.Logger;

public class DBMain {
    private RequestContainer requestContainer;
    private boolean autoRecalculateAccount = true;

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
            ");" +

            "CREATE TABLE IF NOT EXISTS account (" +
            "id serial PRIMARY KEY," +
            "accounts INT references accounts(id)," +
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
        dbPostgres.executeUpdate("DROP TABLE IF EXISTS account;");
        dbPostgres.executeUpdate("DROP TABLE IF EXISTS moves;");
        dbPostgres.executeUpdate("DROP TABLE IF EXISTS accounts;");
    }

    public void createEmptyTable() throws SQLException {
        this.dropAllTable();
        dbPostgres.executeUpdate(SQL_CREATE_EMPTY_TABLE);
    }

    public double getSummaryBalance() throws SQLException, DBException {
        ResultSet rs = dbPostgres.executeQuery("SELECT id FROM accounts;");
        double summaryBalance = 0.0;
        while (rs.next()) {
            double balance = RecordAccounts.createExists(this, rs.getInt(1)).getBalance();
            summaryBalance += balance;
            Logger.getGlobal().info("Account " + rs.getInt(1) + " : " + balance);
        }
        rs.close();
        System.out.println("Summary balance  : " + summaryBalance);
        return summaryBalance;
    }

    public void recalculateAccounts() {  //++
        try (PreparedStatement pst = this.dbPostgres.getConnection()
                .prepareStatement(String.format("SELECT id FROM accounts;"));
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                try {
                    RecordAccounts recordAccounts = RecordAccounts.createExists(this, rs.getInt(1));
                    recordAccounts.recalculate(null);
                } catch (SQLException | DBException e) {
                    e.printStackTrace();
                }

                System.out.println("Account recalculate : " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnection() {
        return dbPostgres.isConnection();
    }

    public String getAnswer(String request) {
        return requestContainer.getAnswer(request);
    }

    public boolean getAutoRecalculateAccount() {
        return autoRecalculateAccount;
    }

    public void setAutoRecalculateAccount(boolean autoRecalculateAccount) {
        if (!this.autoRecalculateAccount && autoRecalculateAccount) {
            this.autoRecalculateAccount = true;
            this.recalculateAccounts();
        }
        this.autoRecalculateAccount = autoRecalculateAccount;
    }
}
