package db;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static RecordAccount createNew(DBMain db, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum){
        return new RecordAccount(db, db.dbPostgres.getNextID(tableName+recordAccounts.getId()), recordMoves, recordAccounts, sum);
    }

    public static RecordAccount createExists(DBMain db, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum) throws SQLException, DBException {
        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        ResultSet rs = db.dbPostgres.executeQuery(String.format("SELECT id, sum FROM %s WHERE moves = %d AND sum = %s;", tableName+recordAccounts.getId(), recordMoves.getId(),strSum));
        if(rs.next()) {
            return new RecordAccount(db, rs.getInt(1), recordMoves, recordAccounts, rs.getDouble(2));
        }else {
            throw new DBException("Record account for moves" + recordMoves.getId() + " not exist!");
        }
    }

    @Override
    public void insert() throws DBException, SQLException {
        super.insert();
        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        String sqlQuery = String.format("INSERT INTO %s (id, moves, time , sum) VALUES(%d, %d, \'%s\' ,%s);", getTableAccountName(), getId(), recordMoves.getId(), recordMoves.time ,strSum);
        getDb().dbPostgres.executeSimple(sqlQuery);

        if(recordAccounts.timeModify.after(recordMoves.time)){
            recordAccounts.modify(null,recordMoves.time);
        }
        recordAccounts.recalculate(null);
    }

    @Override
    public void delete() throws DBException, SQLException {
        super.delete();
        if(recordAccounts.timeModify.after(recordMoves.time)){
            recordAccounts.modify(null,recordMoves.time);
        }
        recordAccounts.recalculate(null);
    }

    private String getTableAccountName() {
        return tableName + recordAccounts.getId();
    }
}
