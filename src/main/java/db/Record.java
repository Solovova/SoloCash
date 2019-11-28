package db;

import db.dataclas.TransactionType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class Record {
    private int id;
    private DBMain db;

    public Record(DBMain db, int id) {
        this.db = db;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) throws DBException {
        if (this.id != -1) {
            throw new DBException("Set id when it not -1");
        }
        this.id = id;
    }

    public DBMain getDb() {
        return db;
    }

    public Connection getConnection() {
        return db.dbPostgres.getConnection();
    }

    protected void insert() throws DBException, SQLException {
    }

    protected void delete() throws DBException, SQLException {
    }

    protected void modify() throws DBException, SQLException {
    }


    public void transaction(TransactionType nameTransaction, HashMap<String, Object> args) {
        try {
            getConnection().setAutoCommit(false);

            switch (nameTransaction) {
                case INSERT:
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
