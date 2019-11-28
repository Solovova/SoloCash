package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class RecordAccount extends Record {
    private RecordAccounts recordAccounts;
    private RecordMoves recordMoves;
    public Timestamp time;
    public double sum;

    private RecordAccount(DBMain db, int id, RecordMoves recordMoves, RecordAccounts recordAccounts, double sum) {
        super(db, id);
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
                String.format("SELECT id, sum FROM account WHERE moves = %d AND sum = %s;", recordMoves.getId(), strSum));
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
                String.format("INSERT INTO account (accounts, moves, time , sum) VALUES(%d, %d, \'%s\' ,%s) RETURNING id;",
                        recordAccounts.getId(), recordMoves.getId(), recordMoves.time, strSum));
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
        String sqlQuery = String.format("DELETE FROM account WHERE id = %d;", getId());
        getDb().dbPostgres.executeUpdate(sqlQuery);

        if (recordAccounts.timeModify.after(recordMoves.time)) {
            recordAccounts.modify(null, recordMoves.time);
        }
        recordAccounts.recalculate(null);
    }
}
