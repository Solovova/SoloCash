package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static sf.StaticFunctions.doubleToString;

public class RecordMoves extends Record {
    public Timestamp time;
    public RecordAccounts accountFrom;
    public RecordAccounts accountTo;
    public double sum;
    public String describe;

    RecordMoves(DBMain db, int id, Timestamp time, RecordAccounts accountFrom, RecordAccounts accountTo, double sum, String describe) {
        super(db, id);
        this.time = time;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.sum = sum;
        this.describe = describe;
    }

    public static RecordMoves createNew(DBMain db, Timestamp time, RecordAccounts accountFrom, RecordAccounts accountTo, double sum, String describe) {
        return new RecordMoves(db, -1, time, accountFrom, accountTo, sum, describe);
    }

    public static RecordMoves createExists(DBMain db, int id) throws DBException, SQLException {
        try (PreparedStatement pst = db.dbPostgres.getConnection().prepareStatement(
                String.format("SELECT time, accountFrom, accountTo, sum, describe FROM moves WHERE id =%d;", id));
             ResultSet rs = pst.executeQuery()
        ) {
            if (rs.next()) {
                RecordAccounts raFrom = RecordAccounts.createExists(db, rs.getInt(2));
                RecordAccounts raTo = RecordAccounts.createExists(db, rs.getInt(3));
                return new RecordMoves(db, id, rs.getTimestamp(1), raFrom, raTo, rs.getDouble(4), rs.getString(5));
            } else {
                throw new DBException("Moves " + id + " not exist!");
            }
        }
    }

    @Override
    protected void insert() throws DBException, SQLException { //++
        super.insert();

        String strSum = doubleToString(sum);
        String strSql = String.format("INSERT INTO moves(time, accountFrom, accountTo, sum, describe) VALUES(\'%s\', %d, %d, %s, \'%s\') RETURNING id;",
                time, accountFrom.getId(), accountTo.getId(), strSum, describe);
        try (
                PreparedStatement pst = getConnection().prepareStatement(strSql);
                ResultSet rs = pst.executeQuery();
        ) {
            rs.next();
            this.setId(rs.getInt(1));
        }

        RecordAccount.createNew(getDb(), this, accountFrom, -this.sum).insert();
        RecordAccount.createNew(getDb(), this, accountTo, this.sum).insert();
    }

    @Override
    public void delete() throws DBException, SQLException {
        super.delete();
        RecordAccount.createExists(getDb(), this, accountFrom, -this.sum).delete();
        RecordAccount.createExists(getDb(), this, accountTo, this.sum).delete();
        String sqlQuery = String.format("DELETE FROM moves WHERE id = %d;", getId());
        getDb().dbPostgres.executeUpdate(sqlQuery);
    }

    public void modify(Timestamp newTime, RecordAccounts newRaFrom, RecordAccounts newRaTo, Double newSum, String newDescribe) throws SQLException, DBException {
        StringBuilder sb = new StringBuilder();

        double oldSum = this.sum;
        RecordAccounts oldRaFrom = this.accountFrom;
        RecordAccounts oldRaTo = this.accountTo;
        boolean needRefresh = false;

        if (newTime != null && !newTime.equals(this.time)) {
            sb.append(String.format("UPDATE moves SET time = \'%s\' WHERE id = %d;", newTime, getId()));
            this.time = newTime;
            needRefresh = true;
        }

        if (newRaFrom != null && newRaFrom.getId() != this.accountFrom.getId()) {
            sb.append(String.format("UPDATE moves SET accountfrom = %d WHERE id = %d;", newRaFrom.getId(), getId()));
            this.accountFrom = newRaFrom;
            needRefresh = true;
        }

        if (newRaTo != null && newRaTo.getId() != this.accountTo.getId()) {
            sb.append(String.format("UPDATE moves SET accountto = %d WHERE id = %d;", newRaTo.getId(), getId()));
            this.accountTo = newRaTo;
            needRefresh = true;
        }

        if (newSum != null && newSum != this.sum) {
            String strSum = doubleToString(newSum);
            sb.append(String.format("UPDATE moves SET sum = %s WHERE id = %d;", strSum, getId()));
            this.sum = newSum;
            needRefresh = true;
        }

        if (newDescribe != null && !newDescribe.equals(this.describe)) {
            sb.append(String.format("UPDATE moves SET describe = \'%' WHERE id = %d;", newDescribe, getId()));
            this.describe = newDescribe;
        }

        String sqlQuery = sb.toString();
        if (!sqlQuery.isEmpty()) {
            getDb().dbPostgres.executeUpdate(sqlQuery);
        }


        if (needRefresh) {
            RecordAccount.createExists(getDb(), this, oldRaFrom, -oldSum).delete();
            RecordAccount.createExists(getDb(), this, oldRaTo, oldSum).delete();

            RecordAccount.createNew(getDb(), this, accountFrom, -this.sum).insert();
            RecordAccount.createNew(getDb(), this, accountTo, this.sum).insert();
        }
    }
}
