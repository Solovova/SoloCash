package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class RecordAccounts {
    public int id;
    public String name;
    private DBMain db;

    public void recalculate(Timestamp ts) throws DBException {
        String table = getTableAccountName();
        checkAccountTableExists();
        //String sqlR = String.format("SELECT id, sum, balance, LAG(balance,1) OVER(ORDER BY time) prev_balance FROM %s WHERE time >= \'%s\' ORDER BY time;", table, ts);
        String sqlR = String.format("WITH cte AS (SELECT id, sum, balance, LAG(balance,1) OVER(ORDER BY time) prev_balance, time FROM %s ORDER BY time) SELECT id, sum, balance, prev_balance FROM cte WHERE time >= \'%s\';", table, ts);


        //System.out.println(sqlR);
        ResultSet rs = db.dbPostgres.executeQuery(sqlR);


        double balance = 0;

        StringBuilder sb = new StringBuilder();
        try {

            boolean first = true;
            while (rs.next()) {
                //if (rs.getTimestamp(5).before(ts)) continue;

                if (first) {
                    first = false;
                    balance = rs.getDouble(4);
//
//                    System.out.println(rs.getDouble(2));
//                    System.out.println(rs.getDouble(3));
//                    System.out.println(rs.getDouble(4));
                }

                balance += rs.getDouble(2);
                if ((int)(balance*100) == (int)(rs.getDouble(3)*100)) continue;
                int recordAccountID = rs.getInt(1);
                String strBalance = new DecimalFormat("#.00#").format(balance).replace(',', '.');
                String sqlRequest =  String.format("UPDATE %s SET balance = %s WHERE id = %d;",table,strBalance,recordAccountID);
                sb.append(sqlRequest);
            }

            db.dbPostgres.execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RecordAccounts(DBMain db, int id, String name) {
        this.db = db;
        this.id = id;
        this.name = name;
    }

    public void conformityFromDB() throws DBException {
        checkAccountsTableExists();
        if (id == -1 && !name.equals("")) {
            id = db.dbPostgres.getIDFromTableByFiler("accounts", "name", name);
            if (id == -1) throw new DBException("Account " + name + " not exist!");
        }

        if (name.equals("")) {
            ResultSet rs = db.dbPostgres.getRowByIDFromTable("accounts", id, "name");
            try {
                if(rs.next()) {
                    this.name = rs.getString(1);
                }else {
                    throw new DBException("Account " + id + " not exist!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DBException("Account " + id + " not exist!");
            }
        }
    }

    public void checkAccountsTableExists() throws DBException {
        if (!db.dbPostgres.isTable("accounts")) {
            throw new DBException("Table accounts not exist!");
        }
    }

    public void checkAccountTableExists() throws DBException {
        if (!db.dbPostgres.isTable(getTableAccountName())) {
            throw new DBException("Table " + getTableAccountName() +" not exist!");
        }
    }

    public String getTableAccountName() {
        return "account_" + this.id;
    }

    public boolean checkPossibilityInsert() throws DBException {
        checkAccountsTableExists();

        if (db.dbPostgres.isStrFieldInTableByFilter("accounts", "name", name)) {
            throw new DBException("Account " + name + " already exists!");
        }

        if (id == -1) {
            id = db.dbPostgres.getNextID("accounts");
        }else {
            if (db.dbPostgres.isIDInTable("accounts", id)) {
                throw new DBException("Id " + id + " is already present in table accounts!");
            }
        }

        if(id==-1) throw new DBException("Next id in tables accounts = -1");
        return true;
    }

    public void insert() throws DBException {
        if (checkPossibilityInsert()){
            String sqlQuery = String.format("INSERT INTO accounts(id, name) VALUES(%d, \'%s\');", id, name);
            db.dbPostgres.execute(sqlQuery);

            String sqlQueryTableCreate = String.format(DBSQLRequests.SQL_CREATE_EMPTY_ACCOUNT_TABLE, "account_" + id);
            db.dbPostgres.execute(sqlQueryTableCreate);
        }
    }
}
