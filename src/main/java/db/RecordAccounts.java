package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class RecordAccounts {
    public int id;
    public String name;
    private DBMain db;

    public void recalculate() throws DBException {
        String table = getTableAccountName();
        checkAccountTableExists();
        ResultSet rs = db.dbPostgres.executeQuery(String.format("SELECT id, sum  FROM %s ORDER BY time;", table));

        double balance = 0.0;

        StringBuilder sb = new StringBuilder();
        try {
            while (rs.next()) {
                balance += rs.getDouble(2);
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
