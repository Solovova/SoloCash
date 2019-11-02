package db;

import test.DBTestData;

import java.sql.*;
import java.text.DecimalFormat;

public class DB {
    private DBPostgres dbPostgres = DBPostgres.create();
    private StringBuilder sqlQueryBuild = new StringBuilder();

    public void close() {
        dbPostgres.close();
    }

    public void dropAllTable() {
        if (dbPostgres.isTable("accounts")) {
            ResultSet rs = dbPostgres.executeQuery("SELECT id FROM accounts;");
            try {
                while (rs.next()) {
                    System.out.println(String.format("DROP TABLE IF EXISTS %s;", "account_"+rs.getInt(1)));
                    dbPostgres.execute(String.format("DROP TABLE IF EXISTS %s;", "account_"+rs.getInt(1)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        dbPostgres.execute("DROP TABLE IF EXISTS moves;");
        dbPostgres.execute("DROP TABLE IF EXISTS accounts;");
    }

    public void createEmptyTable() {
        this.dropAllTable();
        dbPostgres.execute(DBTestData.SQL_TEST_CREATE_EMPTY_TABLE);
    }

    public boolean isConnection() {
        return dbPostgres.isConnection();
    }

    public boolean existsAccount(String name) {
        ResultSet rs = dbPostgres.executeQuery(String.format("SELECT name FROM accounts WHERE name='%s';", name));
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getAccountID(String name) {
        ResultSet rs = dbPostgres.executeQuery(String.format("SELECT id, name FROM accounts WHERE name='%s';", name));
        try {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addAccount(String name, boolean testExists) {
        if (testExists && existsAccount(name)) {
            System.out.println("Account " + name + " already exists!");
            return;
        }
        int id = dbPostgres.getNextID("accounts");
        if (id != -1) {
            addAccountWithID(id, name);
        }
    }

    //ToDo control ID
    public void addAccountWithID(int id, String name) {
        String sqlQuery = String.format("INSERT INTO accounts(id, name) VALUES(%d, \'%s\');", id, name);
        dbPostgres.execute(sqlQuery);

        String sqlQueryTableCreate = String.format(DBTestData.SQL_CREATE_EMPTY_ACCOUNT_TABLE, "account_" + id);
        dbPostgres.execute(sqlQueryTableCreate);
    }

    public void addMoveWithID(int idFrom, int idTo, double sum){
        int id = dbPostgres.getNextID("moves");
        if (id != -1) {
            String strSum = new DecimalFormat("#.00#").format(sum).replace(',','.');
            String sqlQuery = String.format("INSERT INTO moves(id, accountFrom, accountTo, sum, time, describe) VALUES(%d, %d, %d, %s, %d, \'%s\');", id, idFrom, idTo, strSum, 0, "ddd");
            dbPostgres.execute(sqlQuery);
        }
    }

    public void addMove(String nameFrom, String nameTo, double sum){
        int idFrom = getAccountID(nameFrom);
        if (idFrom == -1) {
            System.out.println("Account " + nameFrom + " not exist!");
            return;
        }

        int idTo = getAccountID(nameTo);
        if (idTo == -1) {
            System.out.println("Account " + nameTo + " not exist!");
            return;
        }
        addMoveWithID(idFrom,idTo,sum);
    }
}
