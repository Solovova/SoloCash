package db;

import java.sql.Timestamp;
import java.text.DecimalFormat;

public class RecordAccount extends Record{
    final static public String tableName = "account_";
    private RecordAccounts recordAccounts;
    private RecordMoves recordMoves;
    private double sum;

    RecordAccount(DBMain db, int id, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum) {
        super(db, id,tableName+recordAccounts.getId());
        this.recordAccounts = recordAccounts;
        this.recordMoves = recordMoves;
        this.sum = sum;
    }

    public static RecordAccount create(DBMain db, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum){
        return new RecordAccount(db, db.dbPostgres.getNextID(tableName+recordAccounts.getId()), recordMoves, recordAccounts, sum);
    }

    @Override
    public boolean checkPossibilityInsert() throws DBException {
        super.checkPossibilityInsert();
        //ToDo insert account+move check

        return true;
    }

    @Override
    public void insert() throws DBException {
        super.insert();
        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        String sqlQuery = String.format("INSERT INTO %s (id, moves, time , sum) VALUES(%d, %d, \'%s\' ,%s);", getTableAccountName(), getId(), recordMoves.getId(), recordMoves.time ,strSum);
        getDb().dbPostgres.executeSimple(sqlQuery);
        recordAccounts.modify(null,recordMoves.time);
        recordAccounts.recalculate(null);
    }

    private String getTableAccountName() {
        return tableName + recordAccounts.getId();
    }
}
