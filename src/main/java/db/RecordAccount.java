package db;

import java.text.DecimalFormat;

public class RecordAccount {
    private RecordAccounts recordAccounts;
    private RecordMoves recordMoves;
    public int id;
    private DBMain db;
    private double sum;

    RecordAccount(DBMain db, RecordMoves recordMoves, RecordAccounts recordAccounts, int id, double sum) {
        this.db = db;
        this.id = id;
        this.recordAccounts = recordAccounts;
        this.recordMoves = recordMoves;
        this.sum = sum;
    }

    public void insert() throws DBException {
        String table = recordAccounts.getTableAccountName();
        recordAccounts.checkAccountTableExists();

        int id = db.dbPostgres.getNextID(table);
        if (id == -1) throw new DBException("Next id in tables accounts = -1");

        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        String sqlQuery = String.format("INSERT INTO %s (id, moves, time , sum) VALUES(%d, %d, \'%s\' ,%s);", table, id, recordMoves.id, recordMoves.timestamp ,strSum);
        db.dbPostgres.execute(sqlQuery);
    }
}
