package db;

import java.text.DecimalFormat;

public class TableMoves {
    private DBMain db;

    TableMoves(DBMain db){
        this.db = db;
    }

    private String addMoveToAccount(int accountID, int moveID, double sum) {
        String table = "account_" + accountID;
        if (!db.dbPostgres.isTable(table)) return "Table " + table +" not exist!";

        int id = db.dbPostgres.getNextID(table);
        if (id == -1) return "Next id in " + table + " = -1";

        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        String sqlQuery = String.format("INSERT INTO %s (id, moves, sum) VALUES(%d, %d, %s);", table, id, moveID, strSum);
        db.dbPostgres.execute(sqlQuery);

        return null;
    }

    private String addMoveByID(int idFrom, int idTo, double sum) {


        int id = db.dbPostgres.getNextID("moves");
        if (id == -1) return "Next id in tables moves = -1";

        String strSum = new DecimalFormat("#.00#").format(sum).replace(',', '.');
        String sqlQuery = String.format("INSERT INTO moves(id, accountFrom, accountTo, sum, time, describe) VALUES(%d, %d, %d, %s, %d, \'%s\');", id, idFrom, idTo, strSum, 0, "ddd");
        db.dbPostgres.execute(sqlQuery);

        String result = null;
        result = addMoveToAccount(idFrom,id,-sum);
        if(result != null) return result;
        result = addMoveToAccount(idTo,id,sum);
        return result;
    }

    public String addMove(String nameFrom, String nameTo, double sum) {
        int idFrom = db.dbPostgres.getIDFromTableByFiler("accounts", "name", nameFrom);
        if (idFrom == -1) return "Account " + nameFrom + " not exist!";

        int idTo = db.dbPostgres.getIDFromTableByFiler("accounts", "name", nameTo);
        if (idTo == -1) return "Account " + nameTo + " not exist!";

        return addMoveByID(idFrom, idTo, sum);
    }
}
