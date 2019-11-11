package db;

import java.sql.Connection;
import java.sql.SQLException;

public class Record {
    private int id;
    private DBMain db;
    private String table;

    public Record(DBMain db, int id, String table){
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

    public void checkTableExists() throws DBException {
        if (!db.dbPostgres.isTable(table)) {
            throw new DBException("Table " + table + " not exist!");
        }
    }

    public boolean checkPossibilityInsert() throws DBException {
        checkTableExists();

        if (getId() == -1) throw new DBException("Next id in tables "+getTable() +" = -1");

        if (getDb().dbPostgres.isIDInTable(getTable(), getId())) {
            throw new DBException("Id " + getId() + " is already present in table " + getTable() + "!");
        }

        return true;
    }

    public void insert() throws DBException, SQLException {
        checkPossibilityInsert();
    }

    public void delete() throws DBException, SQLException {
        checkPossibilityDelete();
        String sqlQuery = String.format("DELETE FROM %s WHERE id = %d;", getTable(), getId());
        getDb().dbPostgres.executeSimple(sqlQuery);
    }

    public boolean checkPossibilityDelete() throws DBException {
        checkTableExists();
        if (getId() == -1) throw new DBException("Cant delete " + getTable() +" row id = -1");

        if (!getDb().dbPostgres.isIDInTable(getTable(), getId())) {
            throw new DBException("Cant delete " + getTable() + " row id " + getId() + " not exists!");
        }
        return true;
    }
}
