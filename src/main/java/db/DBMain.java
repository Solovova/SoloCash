package db;

import java.sql.*;
import java.text.DecimalFormat;

public class DBMain {
    private DBPostgres dbPostgres = DBPostgres.create();

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

    public String addAccount(String name) {
        if (dbPostgres.isFieldInTableByFilter("accounts", "name", name))
            return "Account " + name + " already exists!";

        int id = dbPostgres.getNextID("accounts");
        if (id == -1) return "Next id in tables accounts = -1";
        return addAccountByID(id, name);
    }

    public String addAccountByID(int id, String name) {
        if (dbPostgres.isIDInTable("accounts", id)) return "Id " + id + "is already present in table accounts";

        String sqlQuery = String.format("INSERT INTO accounts(id, name) VALUES(%d, \'%s\');", id, name);
        dbPostgres.execute(sqlQuery);

        String sqlQueryTableCreate = String.format(DBSQLRequests.SQL_CREATE_EMPTY_ACCOUNT_TABLE, "account_" + id);
        dbPostgres.execute(sqlQueryTableCreate);
        return null;
    }

    private String addMoveByID(int idFrom, int idTo, double sum) {
        int id = dbPostgres.getNextID("moves");
        if (id == -1) return "Next id in tables moves = -1";

        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        String sqlQuery = String.format("INSERT INTO moves(id, accountFrom, accountTo, sum, time, describe) VALUES(%d, %d, %d, %s, %d, \'%s\');", id, idFrom, idTo, strSum, 0, "ddd");
        dbPostgres.execute(sqlQuery);

        return null;
    }

    public String addMove(String nameFrom, String nameTo, double sum) {
        int idFrom = dbPostgres.getIDFromTableByFiler("accounts", "name", nameFrom);
        if (idFrom == -1) return "Account " + nameFrom + " not exist!";

        int idTo = dbPostgres.getIDFromTableByFiler("accounts", "name", nameTo);
        if (idTo == -1) return "Account " + nameTo + " not exist!";

        return addMoveByID(idFrom, idTo, sum);
    }
}
