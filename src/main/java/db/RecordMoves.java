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
    public void insert() throws DBException {
        super.insert();
        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        String sqlQuery = String.format("INSERT INTO moves(id, time, accountFrom, accountTo, sum, describe) " +
                "VALUES(%d, \'%s\', %d, %d, %s, \'%s\');", getId(), time, accountFrom.getId(), accountTo.getId(),strSum,describe);
        getDb().dbPostgres.executeSimple(sqlQuery);

        RecordAccount.create(getDb(),this,accountFrom,-this.sum).insert();
        RecordAccount.create(getDb(),this,accountTo,this.sum).insert();
    }

//
//    public boolean checkPossibilityInsert() throws DBException {
//        if (!getDb().dbPostgres.isTable("moves")) {
//            throw new DBException("Table moves not exist!");
//        }
//
//        if (getId() == -1) {
//            this.setId((getDb().dbPostgres.getNextID("moves")));
//        } else {
//            if (getDb().dbPostgres.isIDInTable("moves", getId())) {
//                throw new DBException("Id " + getId() + " is already present in table moves!");
//            }
//        }
//
//        if (getId() == -1) throw new DBException("Next id in tables moves = -1");
//        return true;
//    }
//
//    public boolean checkTableExists() throws DBException {
//        if (!getDb().dbPostgres.isTable("moves")) {
//            throw new DBException("Table moves not exist!");
//        }
//        return true;
//    }
//
//    public boolean checkPossibilityDelete() throws DBException {
//        checkTableExists();
//
//        if (getId() == -1) throw new DBException("Cant delete id = -1");
//
//        if (!getDb().dbPostgres.isIDInTable("moves", getId())) {
//            throw new DBException("Cant delete " + getId() + " moves isnt in table!");
//        }
//        return true;
//    }
//
//    public void insert() throws DBException {
//        if (checkPossibilityInsert()) {
//            String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
//            accountFrom.conformityFromDB();
//            accountTo.conformityFromDB();
//            String sqlQuery = String.format("INSERT INTO moves(id, accountFrom, accountTo, sum, time, describe) VALUES(%d, %d, %d, %s, \'%s\', \'%s\');", getId(), accountFrom.getId(), accountTo.getId(), strSum, timestamp, "ddd");
//            getDb().dbPostgres.executeSimple(sqlQuery);
//
//            new RecordAccount(getDb(), -1,this, accountFrom, -sum).insert();
//            accountFrom.recalculate(timestamp);
//            new RecordAccount(getDb(), -1,this, accountTo, sum).insert();
//            accountTo.recalculate(timestamp);
//        }
//    }
//
//    public void conformityFromDB() throws DBException {
//        checkTableExists();
//        if (getId() == -1) {
//            throw new DBException("Delete move id = -1 ");
//        }
//
//        ResultSet rs = getDb().dbPostgres.getRowByIDFromTable("moves", getId(), "time, accountfrom, accountto, sum");
//        try {
//            if (rs.next()) {
//                this.timestamp = rs.getTimestamp(1);
//                this.accountFrom = new RecordAccounts(getDb(),rs.getInt(2),"");
//                this.accountTo = new RecordAccounts(getDb(),rs.getInt(3),"");
//                this.sum = rs.getDouble(4);
//            } else {
//                throw new DBException("Account " + getId() + " not exist!");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new DBException("Account " + getId() + " not exist!");
//        }
//
//    }
//
//    public void delete() throws DBException {
//        if (checkPossibilityDelete()) {
//            conformityFromDB();
//            this.accountFrom.delete(getId());
//            this.accountFrom.recalculate(timestamp);
//            this.accountTo.delete(getId());
//            this.accountTo.recalculate(timestamp);
//
//            String sqlQuery = String.format("DELETE FROM moves WHERE id = %d;", getId());
//            getDb().dbPostgres.executeSimple(sqlQuery);
//        }
//    }
}
