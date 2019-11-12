package db;

import db.dataclas.TransactionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class Record {
    private int id;
    private DBMain db;
    private String table;

    public Record(DBMain db, int id, String table) {
        this.db = db;
        this.id = id;
        this.table = table;
    }

    public int getId() {
        return id;
    }

    public DBMain getDb() {
        return db;
    }

    public Connection getConnection() {
        return db.dbPostgres.getConnection();
    }

    public String getTable() {
        return table;
    }

    protected void insert() throws DBException, SQLException {
    }

    protected void delete() throws DBException, SQLException {
        String sqlQuery = String.format("DELETE FROM %s WHERE id = %d;", getTable(), getId());
        getDb().dbPostgres.executeUpdate(sqlQuery);
    }

    protected void modify() throws DBException, SQLException {
    }


    public void transaction(TransactionType nameTransaction, HashMap<String, Object> args) {
        try {
            getConnection().setAutoCommit(false);

            switch (nameTransaction) {
                case insert:
                    this.insert();
            }
            getConnection().commit();
        } catch (DBException | SQLException e) {
            e.printStackTrace();
            try {
                getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
