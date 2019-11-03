package db;

public class RecordAccounts {
    public int id;
    public String name;
    private DBMain db;

    public RecordAccounts(DBMain db, int id, String name) {
        this.db = db;
        this.id = id;
        this.name = name;
    }

    public boolean checkPossibilityInsert() throws DBException {
        if (!db.dbPostgres.isTable("accounts")) {
            throw new DBException("Table accounts not exist!");
        }

        if (db.dbPostgres.isFieldInTableByFilter("accounts", "name", name)) {
            throw new DBException("Account " + name + " already exists!");
        }

        if (id == -1) {
            id = db.dbPostgres.getNextID("accounts");
        }else {
            if (db.dbPostgres.isIDInTable("accounts", id)) {
                throw new DBException("Id " + id + "is already present in table accounts");
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
