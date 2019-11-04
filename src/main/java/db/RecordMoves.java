package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class RecordMoves extends Record{
    final static private String tableName = "moves";
    public Timestamp time;
    public RecordAccounts accountFrom;
    public RecordAccounts accountTo;
    public double sum;
    public String describe;

    RecordMoves(DBMain db, int id, Timestamp time, RecordAccounts accountFrom, RecordAccounts accountTo, double sum, String describe) {
        super(db, id,tableName);
        this.time = time;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.sum = sum;
        this.describe = describe;
    }

    public static RecordMoves create(DBMain db, Timestamp time, RecordAccounts accountFrom, RecordAccounts accountTo, double sum, String describe){
        return new RecordMoves(db, db.dbPostgres.getNextID(tableName), time, accountFrom,accountTo,sum,describe);
    }

    public static RecordMoves create(DBMain db, int id) throws DBException, SQLException {
        ResultSet rs = db.dbPostgres.getRowByIDFromTable(tableName, id, "time, accountFrom, accountTo, sum, describe");
        if (rs.next()) {
            RecordAccounts raFrom = RecordAccounts.create(db,rs.getInt(2));
            RecordAccounts raTo = RecordAccounts.create(db,rs.getInt(3));
            return new RecordMoves(db, id, rs.getTimestamp(1), raFrom, raTo, rs.getDouble(4), rs.getString(5));
        } else {
            throw new DBException("Moves " + id + " not exist!");
        }
    }

    @Override
    public void insert() throws DBException, SQLException {
        super.insert();
        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        String sqlQuery = String.format("INSERT INTO moves(id, time, accountFrom, accountTo, sum, describe) " +
                "VALUES(%d, \'%s\', %d, %d, %s, \'%s\');", getId(), time, accountFrom.getId(), accountTo.getId(),strSum,describe);
        getDb().dbPostgres.executeSimple(sqlQuery);

        RecordAccount.create(getDb(),this,accountFrom,-this.sum).insert();
        RecordAccount.create(getDb(),this,accountTo,this.sum).insert();
    }
}
