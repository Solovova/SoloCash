package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class TableAccount {
    private DBMain db;
    private RecordAccounts recordAccounts;

    public TableAccount(DBMain db, RecordAccounts recordAccounts) {
        this.db = db;
        this.recordAccounts = recordAccounts;
    }


}
