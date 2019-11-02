package db;

import test.DBTestData;

import java.sql.*;

public class DB {
    private DBPostgres dbPostgres = DBPostgres.create();
    private StringBuilder sqlQueryBuild = new StringBuilder();

    public void close() {
        dbPostgres.close();
    }

    public void dropAllTable() {
        dbPostgres.execute(DBTestData.SQL_TEST_DROP_ALL_TABLE);
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

    public void addAccountWithID(int id, String name) {
        String sqlQuery = String.format("INSERT INTO accounts(id, name) VALUES(%d, \'%s\');", id, name);
        dbPostgres.execute(sqlQuery);

        String sqlQueryTableCreate = String.format(DBTestData.SQL_CREATE_EMPTY_ACCOUNT_TABLE, "account_"+id);
        dbPostgres.execute(sqlQueryTableCreate);
    }
}
