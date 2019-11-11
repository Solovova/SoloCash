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

    public static RecordMoves createNew(DBMain db, Timestamp time, RecordAccounts accountFrom, RecordAccounts accountTo, double sum, String describe){
        return new RecordMoves(db, db.dbPostgres.getNextID(tableName), time, accountFrom,accountTo,sum,describe);
    }

    public static RecordMoves createExists(DBMain db, int id) throws DBException, SQLException {
        ResultSet rs = db.dbPostgres.getRowByIDFromTable(tableName, id, "time, accountFrom, accountTo, sum, describe");
        if (rs.next()) {
            RecordAccounts raFrom = RecordAccounts.createExists(db,rs.getInt(2));
            RecordAccounts raTo = RecordAccounts.createExists(db,rs.getInt(3));
            RecordMoves recordMoves = new RecordMoves(db, id, rs.getTimestamp(1), raFrom, raTo, rs.getDouble(4), rs.getString(5));
            rs.close();
            return recordMoves;
        } else {
            throw new DBException("Moves " + id + " not exist!");
        }
    }

    @Override
    public void insert()  {
        try {
            getConnection().setAutoCommit(false);

            super.insert();
            String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
            String sqlQuery = String.format("INSERT INTO moves(id, time, accountFrom, accountTo, sum, describe) " +
                    "VALUES(%d, \'%s\', %d, %d, %s, \'%s\');", getId(), time, accountFrom.getId(), accountTo.getId(),strSum,describe);
            getDb().dbPostgres.executeSimple(sqlQuery);

            RecordAccount.createNew(getDb(),this,accountFrom,-this.sum).insert();
            RecordAccount.createNew(getDb(),this,accountTo,this.sum).insert();

            getConnection().commit();
        }
        catch (DBException | SQLException e) {
            e.printStackTrace();
            try {
                getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }finally {
            try {
                getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void delete() throws DBException, SQLException {
        RecordAccount.createExists(getDb(),this,accountFrom,-this.sum).delete();
        RecordAccount.createExists(getDb(),this,accountTo, this.sum).delete();
        super.delete();
    }

    public void modify(Timestamp newTime, RecordAccounts newRaFrom, RecordAccounts newRaTo, Double newSum, String newDescribe) throws SQLException, DBException {
        StringBuilder sb = new StringBuilder();

        double oldSum = this.sum;
        RecordAccounts oldRaFrom = this.accountFrom;
        RecordAccounts oldRaTo = this.accountTo;
        boolean needRefresh = false;

        if (newTime != null && !newTime.equals(this.time)) {
            sb.append(String.format("UPDATE %s SET time = \'%s\' WHERE id = %d;", getTable(), newTime, getId()));
            this.time = newTime;
            needRefresh = true;
        }

        if (newRaFrom != null && newRaFrom.getId()!=this.accountFrom.getId()) {
            sb.append(String.format("UPDATE %s SET accountfrom = %d WHERE id = %d;", getTable(), newRaFrom.getId(), getId()));
            this.accountFrom = newRaFrom;
            needRefresh = true;
        }

        if (newRaTo != null && newRaTo.getId()!=this.accountTo.getId()) {
            sb.append(String.format("UPDATE %s SET accountto = %d WHERE id = %d;", getTable(), newRaTo.getId(), getId()));
            this.accountTo = newRaTo;
            needRefresh = true;
        }

        if (newSum != null && newSum != this.sum) {
            String strSum = new DecimalFormat("#.00#").format(newSum).replace(',', '.');
            sb.append(String.format("UPDATE %s SET sum = %s WHERE id = %d;", getTable(), strSum, getId()));
            this.sum = newSum;
            needRefresh = true;
        }

        if (newDescribe != null && !newDescribe.equals(this.describe)) {
            sb.append(String.format("UPDATE %s SET describe = \'%' WHERE id = %d;", getTable(), newDescribe, getId()));
            this.describe = newDescribe;
        }

        String sqlQuery = sb.toString();
        //System.out.println(sqlQuery);
        if (!sqlQuery.isEmpty()) {
            getDb().dbPostgres.executeSimple(sqlQuery);
        }

        if (needRefresh) {
            RecordAccount.createExists(getDb(),this,oldRaFrom,-oldSum).delete();
            RecordAccount.createExists(getDb(),this,oldRaTo, oldSum).delete();

            RecordAccount.createNew(getDb(),this,accountFrom,-this.sum).insert();
            RecordAccount.createNew(getDb(),this,accountTo,this.sum).insert();
        }
    }
}
