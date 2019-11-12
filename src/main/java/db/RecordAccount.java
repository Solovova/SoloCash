package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class RecordAccount extends Record {
    final static public String tableName = "account_";
    private RecordAccounts recordAccounts;
    private RecordMoves recordMoves;
    private Timestamp time;
    private double sum;

    RecordAccount(DBMain db, int id, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum) {
        super(db, id, tableName + recordAccounts.getId());
        this.recordAccounts = recordAccounts;
        this.recordMoves = recordMoves;
        this.sum = sum;
    }

    public static RecordAccount createNew(DBMain db, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum) {
        return new RecordAccount(db, -1 , recordMoves, recordAccounts, sum);
    }

    public static RecordAccount createExists(DBMain db, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum) throws SQLException, DBException {
        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        ResultSet rs = db.dbPostgres.executeQuery(String.format("SELECT id, sum FROM %s WHERE moves = %d AND sum = %s;", tableName + recordAccounts.getId(), recordMoves.getId(), strSum));
        if (rs.next()) {
            RecordAccount recordAccount = new RecordAccount(db, rs.getInt(1), recordMoves, recordAccounts, rs.getDouble(2));
            rs.close();
            return recordAccount;
        } else {
            throw new DBException("Record account for moves" + recordMoves.getId() + " not exist!");
        }
    }

    @Override
    protected void insert() throws DBException, SQLException {  //++
        super.insert();
        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        try(PreparedStatement pst = getConnection().prepareStatement(
                String.format("INSERT INTO %s (moves, time , sum) VALUES(%d, \'%s\' ,%s) RETURNING id;",
                        getTableAccountName(), recordMoves.getId(), recordMoves.time, strSum)
        )) {
            ResultSet i = pst.executeQuery();
            i.next();
            this.setId(i.getInt(1));
        }

        //ToDo
        if (recordAccounts.timeModify.after(recordMoves.time)) {
            recordAccounts.modify(null, recordMoves.time);
        }
        recordAccounts.recalculate( null);
    }

    @Override
    public void delete() throws DBException, SQLException {
        super.delete();
        if (recordAccounts.timeModify.after(recordMoves.time)) {
            recordAccounts.modify(null, recordMoves.time);
        }
        recordAccounts.recalculate(null);
    }

    private String getTableAccountName() {
        return tableName + recordAccounts.getId();
    }
}
