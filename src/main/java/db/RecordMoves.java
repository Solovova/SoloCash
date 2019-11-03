package db;

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
        }else {
            if (db.dbPostgres.isIDInTable("moves", id)) {
                throw new DBException("Id " + id + " is already present in table moves!");
            }
        }

        if(id==-1) throw new DBException("Next id in tables moves = -1");
        return true;
    }

    public void insert() throws DBException {
        if (checkPossibilityInsert()) {
            String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
            accountFrom.conformityFromDB();
            accountTo.conformityFromDB();
            String sqlQuery = String.format("INSERT INTO moves(id, accountFrom, accountTo, sum, time, describe) VALUES(%d, %d, %d, %s, \'%s\', \'%s\');", id, accountFrom.id, accountTo.id, strSum,timestamp , "ddd");
            db.dbPostgres.execute(sqlQuery);

            new RecordAccount(db,this,accountFrom,-1,-sum).insert();
            accountFrom.recalculate(timestamp);
            new RecordAccount(db,this,accountTo,-1,sum).insert();
            accountTo.recalculate(timestamp);
        }
    }
}
