package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class RecordMoves {
    public int id;
    private DBMain db;
    public RecordAccounts accountFrom;
    public RecordAccounts accountTo;
    public double sum;
    public Timestamp timestamp;

    public RecordMoves(DBMain db, int id, RecordAccounts accountFrom, RecordAccounts accountTo, double sum, Timestamp timestamp) {
        this.db = db;
        this.id = id;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.sum = sum;
        this.timestamp = timestamp;
    }

    public boolean checkPossibilityInsert() throws DBException {
        if (!db.dbPostgres.isTable("moves")) {
            throw new DBException("Table moves not exist!");
        }

        if (id == -1) {
            id = db.dbPostgres.getNextID("moves");
        } else {
            if (db.dbPostgres.isIDInTable("moves", id)) {
                throw new DBException("Id " + id + " is already present in table moves!");
            }
        }

        if (id == -1) throw new DBException("Next id in tables moves = -1");
        return true;
    }

    public boolean checkTableExists() throws DBException {
        if (!db.dbPostgres.isTable("moves")) {
            throw new DBException("Table moves not exist!");
        }
        return true;
    }

    public boolean checkPossibilityDelete() throws DBException {
        checkTableExists();

        if (id == -1) throw new DBException("Cant delete id = -1");

        if (!db.dbPostgres.isIDInTable("moves", id)) {
            throw new DBException("Cant delete " + id + " moves isnt in table!");
        }
        return true;
    }

    public void insert() throws DBException {
        if (checkPossibilityInsert()) {
            String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
            accountFrom.conformityFromDB();
            accountTo.conformityFromDB();
            String sqlQuery = String.format("INSERT INTO moves(id, accountFrom, accountTo, sum, time, describe) VALUES(%d, %d, %d, %s, \'%s\', \'%s\');", id, accountFrom.id, accountTo.id, strSum, timestamp, "ddd");
            db.dbPostgres.executeSimple(sqlQuery);

            new RecordAccount(db, this, accountFrom, -1, -sum).insert();
            accountFrom.recalculate(timestamp);
            new RecordAccount(db, this, accountTo, -1, sum).insert();
            accountTo.recalculate(timestamp);
        }
    }

    public void conformityFromDB() throws DBException {
        checkTableExists();
        if (id == -1) {
            throw new DBException("Delete move id = -1 ");
        }

        ResultSet rs = db.dbPostgres.getRowByIDFromTable("moves", id, "time, accountfrom, accountto, sum");
        try {
            if (rs.next()) {
                this.timestamp = rs.getTimestamp(1);
                this.accountFrom = new RecordAccounts(db,rs.getInt(2),"");
                this.accountTo = new RecordAccounts(db,rs.getInt(3),"");
                this.sum = rs.getDouble(4);
            } else {
                throw new DBException("Account " + id + " not exist!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("Account " + id + " not exist!");
        }

    }

    public void delete() throws DBException {
        if (checkPossibilityDelete()) {
            conformityFromDB();
            System.out.println(this.accountFrom.id);
            System.out.println(this.accountTo.id);

            this.accountFrom.delete(id);
            this.accountFrom.recalculate(timestamp);
            this.accountTo.delete(id);
            this.accountTo.recalculate(timestamp);

            String sqlQuery = String.format("DELETE FROM moves WHERE id = %d;", id);
            db.dbPostgres.executeSimple(sqlQuery);
        }
    }

    public void setSum(double sum) throws DBException {
        conformityFromDB();
        if (this.sum == sum) return;
        delete();
        this.sum = sum;
        insert();
    }
}
