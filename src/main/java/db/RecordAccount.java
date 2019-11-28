package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class RecordAccount extends Record {
    static final String TABLE_NAME_PREFIX = "account_";
    private RecordAccounts recordAccounts;
    private RecordMoves recordMoves;
    public Timestamp time;
    public double sum;

    private RecordAccount(DBMain db, int id, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum) {
        super(db, id, TABLE_NAME_PREFIX + recordAccounts.getId());
        this.recordAccounts = recordAccounts;
        this.recordMoves = recordMoves;
        this.sum = sum;
    }

    static RecordAccount createNew(DBMain db, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum) {
        return new RecordAccount(db, -1, recordMoves, recordAccounts, sum);
    }

    static RecordAccount createExists(DBMain db, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum) throws SQLException, DBException {

        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        try (PreparedStatement pst = db.dbPostgres.getConnection().prepareStatement(
                String.format("SELECT id, sum FROM %s WHERE moves = %d AND sum = %s;", TABLE_NAME_PREFIX + recordAccounts.getId(), recordMoves.getId(), strSum));
             ResultSet rs = pst.executeQuery()
        ) {
            if (rs.next()) {
                return new RecordAccount(db, rs.getInt(1), recordMoves, recordAccounts, rs.getDouble(2));
            } else {
                throw new DBException("Record account for moves" + recordMoves.getId() + " not exist!");
            }
        }
    }

    @Override
    protected void insert() throws DBException, SQLException {  //++
        super.insert();
        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        try (PreparedStatement pst = getConnection().prepareStatement(
                String.format("INSERT INTO %s (moves, time , sum) VALUES(%d, \'%s\' ,%s) RETURNING id;",
                        getTableAccountName(), recordMoves.getId(), recordMoves.time, strSum));
             ResultSet rs = pst.executeQuery()
        ) {
            rs.next();
            this.setId(rs.getInt(1));
        }

        //ToDo
        if (recordAccounts.timeModify.after(recordMoves.time)) {
            recordAccounts.modify(null, recordMoves.time);
        }
        recordAccounts.recalculate(null);
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
        return TABLE_NAME_PREFIX + recordAccounts.getId();
    }
}
