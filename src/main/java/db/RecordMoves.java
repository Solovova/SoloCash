package db;

import java.text.DecimalFormat;

public class RecordMoves {
    public int id;
    private DBMain db;
    public RecordAccounts accountFrom;
    public RecordAccounts accountTo;
    public double sum;

    public RecordMoves(DBMain db, int id, RecordAccounts accountFrom, RecordAccounts accountTo, double sum) {
        this.db = db;
        this.id = id;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.sum = sum;
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
            String sqlQuery = String.format("INSERT INTO moves(id, accountFrom, accountTo, sum, time, describe) VALUES(%d, %d, %d, %s, %d, \'%s\');", id, accountFrom.id, accountTo.id, strSum, 0, "ddd");
            db.dbPostgres.execute(sqlQuery);

            new RecordAccount(db,this,accountFrom,-1,-sum).insert();
            new RecordAccount(db,this,accountTo,-1,sum).insert();
        }
    }
}
