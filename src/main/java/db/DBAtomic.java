package db;//STEP 1. Import required packages

import java.sql.*;

public class DBAtomic {
    private DBPostgres dbPostgres = DBPostgres.create();
    private StringBuilder sqlQueryBuild = new StringBuilder();

    public void close() {
        dbPostgres.close();
    }

    public void dropAllTable() {
        String sqlQuery = "DROP TABLE IF EXISTS accounts, moves";
        dbPostgres.execute(sqlQuery);
    }

    public void createEmptyTable() {
        String sqlQuery = "DROP TABLE IF EXISTS accounts, moves;" +
                "CREATE TABLE IF NOT EXISTS accounts (" +
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

        dbPostgres.execute(sqlQuery);
    }

    public void fillTestData() {
        String sqlQuery =
                "INSERT INTO accounts(id, name) VALUES(1, 'Kasa');" +
                        "INSERT INTO accounts(id, name) VALUES(2, 'Bank');" +
                        "INSERT INTO accounts(id, name) VALUES(3, 'Firm');" +

                        "INSERT INTO moves(id, accountFrom, accountTo, sum, describe) VALUES(1, 1, 2, 10.5, \'Move 1\');" +
                        "INSERT INTO moves(id, accountFrom, accountTo, sum, describe) VALUES(2, 2, 1, 8.1, \'Move 2\');" +
                        "INSERT INTO moves(id, accountFrom, accountTo, sum, describe) VALUES(3, 1, 2, 10, \'Move 3\');" +
                        "INSERT INTO moves(id, accountFrom, accountTo, sum, describe) VALUES(4, 1, 2, 10, \'Move 4\');" +
                        "INSERT INTO moves(id, accountFrom, accountTo, sum, describe) VALUES(5, 1, 2, 10, \'Move 5\');" +
                        "INSERT INTO moves(id, accountFrom, accountTo, sum, describe) VALUES(6, 1, 2, 10, \'Move 6\');" +
                        "INSERT INTO moves(id, accountFrom, accountTo, sum, describe) VALUES(7, 1, 2, 10, \'Move 7\');";

        dbPostgres.execute(sqlQuery);
    }

    public void insertAuthor(int id, String name) throws SQLException {
        String sqlQuery = String.format("INSERT INTO authors(id, name) VALUES(%d, \'%s\');", id, name);
        dbPostgres.execute(sqlQuery);
    }

    public void insertAuthorAdd(int id, String name) throws SQLException {
        String sqlQuery = String.format("INSERT INTO authors(id, name) VALUES(%d, \'%s\');", id, name);
        sqlQueryBuild.append(sqlQuery);
    }

    public void insertAuthorPush() throws SQLException {
        dbPostgres.execute(sqlQueryBuild.toString());
        sqlQueryBuild = new StringBuilder();
    }


    public void printAuthors() throws SQLException {
        ResultSet rs = dbPostgres.executeQuery("SELECT * FROM authors ORDER BY id");
        if (rs == null) return;
        while (rs.next()) {
            System.out.print(rs.getInt(1));
            System.out.print(": ");
            System.out.println(rs.getString(2));
        }
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
        if (id == -1) return;
        addAccountWithID(id, name);
        //System.out.println("Next id:" + id);
    }

    public void addAccountWithID(int id, String name) {
        String sqlQuery = String.format("INSERT INTO accounts(id, name) VALUES(%d, \'%s\');", id, name);
        dbPostgres.execute(sqlQuery);
    }
}
