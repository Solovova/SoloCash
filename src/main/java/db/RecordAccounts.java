package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class RecordAccounts extends Record {
    final static private String tableName = "accounts";
    public String name;
    public Timestamp timeModify;

    RecordAccounts(DBMain db, int id, String name, Timestamp timeModify) {
        super(db, id);
        this.name = name;
        this.timeModify = timeModify;
    }

    public static RecordAccounts createExists(DBMain db, int id) throws DBException, SQLException {
        try (PreparedStatement pst = db.dbPostgres.getConnection().prepareStatement(
                String.format("SELECT %s FROM accounts WHERE id =%d;", "name, timeModify", id));
             ResultSet rs = pst.executeQuery()
        ) {
            if (rs.next()) {
                RecordAccounts recordAccounts = new RecordAccounts(db, id, rs.getString(1), rs.getTimestamp(2));
                rs.close();
                return recordAccounts;
            } else {
                rs.close();
                throw new DBException("Account " + id + " not exist!");
            }
        }
    }

    public static RecordAccounts createNew(DBMain db, String name, Timestamp timestamp) { //++
        return new RecordAccounts(db, -1, name, timestamp);
    }

    @Override
    protected void insert() throws DBException, SQLException { //++
        super.insert();
        try (PreparedStatement pst = getConnection().prepareStatement(
                String.format("INSERT INTO accounts(name, timemodify) VALUES(\'%s\', \'%s\') RETURNING id;", name, timeModify));
             ResultSet rs = pst.executeQuery()) {

            rs.next();
            this.setId(rs.getInt(1));
        }
    }

    public void modify(String nameNew, Timestamp timeModifyNew) throws SQLException {
        StringBuilder sb = new StringBuilder();

        if (nameNew != null && !name.equals(nameNew)) {
            sb.append(String.format("UPDATE accounts SET name = \'%s\' WHERE id = %d;", nameNew, getId()));
            this.name = nameNew;
        }

        if (timeModifyNew != null && !timeModify.equals(timeModifyNew)) {
            sb.append(String.format("UPDATE accounts SET timeModify = \'%s\' WHERE id = %d;" , timeModifyNew, getId()));
            this.timeModify = timeModifyNew;
        }

        String sqlQuery = sb.toString();
        if (!sqlQuery.isEmpty()) {
            getDb().dbPostgres.executeUpdate(sqlQuery);
        }
    }

    @Override
    public void delete() throws DBException, SQLException {
        super.delete();
        String sqlQuery = String.format("DELETE FROM accounts WHERE id = %d;", getId());
        getDb().dbPostgres.executeUpdate(sqlQuery);
    }


    public void recalculate(Timestamp ts) throws DBException, SQLException {
        if (!getDb().getAutoRecalculateAccount()) {
            return;
        }

        if (ts == null) ts = this.timeModify;

        String sqlQuery = String.format("WITH cte AS (SELECT id, sum, balance, LAG(balance,1) OVER(ORDER BY time, id) prev_balance, time FROM account WHERE account.accounts = %d ORDER BY time, id) SELECT id, sum, balance, prev_balance FROM cte WHERE time >= \'%s\';", this.getId(), ts);
        PreparedStatement pst = getConnection().prepareStatement(sqlQuery);
        ResultSet rs = pst.executeQuery();
        double balance = 0;

        try {
            StringBuilder packetSQLQuery = new StringBuilder();
            while (rs.next()) {
                if (rs.isFirst()) {
                    balance = rs.getDouble(4);
                }
                balance += rs.getDouble(2);
                if ((int) (balance * 100) == (int) (rs.getDouble(3) * 100)) continue;
                int recordAccountID = rs.getInt(1);
                String strBalance = new DecimalFormat("#.00#").format(balance).replace(',', '.');
                String sqlRequest = String.format("UPDATE account SET balance = %s WHERE id = %d;", strBalance, recordAccountID);
                packetSQLQuery.append(sqlRequest);
            }
            getDb().dbPostgres.executeUpdate(packetSQLQuery.toString());

            this.modify(null, new Timestamp(System.currentTimeMillis()));
        } finally {
            rs.close();
            pst.close();
        }
    }

    public double getBalance() throws SQLException {
        String sqlRequest = String.format("SELECT balance FROM account WHERE account.accounts = %d ORDER BY time DESC, id DESC;",this.getId());
        ResultSet rs = getDb().dbPostgres.executeQuery(sqlRequest);
        double sumBalance = -999999999;
        if (rs.next()) {
            sumBalance = rs.getDouble(1);
        }
        rs.close();
        return sumBalance;
    }
}
