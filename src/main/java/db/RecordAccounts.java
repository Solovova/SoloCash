package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class RecordAccounts extends Record {
    final static private String tableName = "accounts";
    public String name;
    public Timestamp timeModify;

    RecordAccounts(DBMain db, int id, String name, Timestamp timeModify) {
        super(db, id, tableName);
        this.name = name;
        this.timeModify = timeModify;
    }

    public static RecordAccounts create(DBMain db, int id) throws DBException, SQLException {
        ResultSet rs = db.dbPostgres.getRowByIDFromTable(tableName, id, "name, timeModify");
        if (rs.next()) {
            return new RecordAccounts(db, id, rs.getString(1), rs.getTimestamp(2));
        } else {
            throw new DBException("Account " + id + " not exist!");
        }
    }

    public static RecordAccounts create(DBMain db, String name, Timestamp timestamp) {
        return new RecordAccounts(db, db.dbPostgres.getNextID(tableName), name, timestamp);
    }

    @Override
    public void insert() throws DBException, SQLException {
        super.insert();
        String sqlQuery = String.format("INSERT INTO accounts(id, name, timemodify) VALUES(%d, \'%s\', \'%s\');", getId(), name, timeModify);
        getDb().dbPostgres.executeSimple(sqlQuery);

        String sqlQueryTableCreate = String.format(getDb().SQL_CREATE_EMPTY_ACCOUNT_TABLE, getTableAccountName());
        getDb().dbPostgres.executeSimple(sqlQueryTableCreate);
    }

    public void modify(String nameNew, Timestamp timeModifyNew) throws SQLException {
        StringBuilder sb = new StringBuilder();

        if (nameNew != null && !name.equals(nameNew)) {
            sb.append(String.format("UPDATE %s SET name = \'%s\' WHERE id = %d;", getTable(), nameNew, getId()));
            this.name = nameNew;
        }

        if (timeModifyNew != null && !timeModify.equals(timeModifyNew)) {
            sb.append(String.format("UPDATE %s SET timeModify = \'%s\' WHERE id = %d;", getTable(), timeModifyNew, getId()));
            this.timeModify = timeModifyNew;
        }

        String sqlQuery = sb.toString();
        //System.out.println(sqlQuery);
        if (!sqlQuery.isEmpty()) {
            getDb().dbPostgres.executeSimple(sqlQuery);
        }
    }

    @Override
    public void delete() throws DBException, SQLException {
        super.delete();
        getDb().dbPostgres.executeSimple(String.format("DROP TABLE IF EXISTS %s;", getTableAccountName()));
    }

    private String getTableAccountName() {
        return RecordAccount.tableName + getId();
    }

    public void recalculate(Timestamp ts) throws DBException {
        String tableAccount = getTableAccountName();
        if (!getDb().dbPostgres.isTable(tableAccount)) {
            throw new DBException("Table " + tableAccount + " not exist!");
        }
        if (ts == null) ts = this.timeModify;

        String sqlR = String.format("WITH cte AS (SELECT id, sum, balance, LAG(balance,1) OVER(ORDER BY time, id) prev_balance, time FROM %s ORDER BY time, id) SELECT id, sum, balance, prev_balance FROM cte WHERE time >= \'%s\';", tableAccount, ts);
        ResultSet rs = getDb().dbPostgres.executeQuery(sqlR);
        double balance = 0;

        try {
            while (rs.next()) {
                if (rs.isFirst()) {
                    balance = rs.getDouble(4);
                }
                balance += rs.getDouble(2);
                if ((int) (balance * 100) == (int) (rs.getDouble(3) * 100)) continue;
                int recordAccountID = rs.getInt(1);
                String strBalance = new DecimalFormat("#.00#").format(balance).replace(',', '.');
                String sqlRequest = String.format("UPDATE %s SET balance = %s WHERE id = %d;", tableAccount, strBalance, recordAccountID);
                getDb().dbPostgres.setPacketSQLQueryAdd(sqlRequest);
            }
            getDb().dbPostgres.setPacketSQLQueryExecute();

            this.modify(null,new Timestamp(System.currentTimeMillis()));
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
