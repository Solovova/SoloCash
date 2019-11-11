package db.requests;

import com.google.gson.Gson;
import db.DBMain;
import db.RecordAccounts;
import db.dataclas.AccountAnsRequest;
import db.dataclas.GsonContainer;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RequestAccounts extends RequestParent {
    static String IDENTIFIER = "RequestAccounts";

    public RequestAccounts(DBMain db) {
        super(db);
    }

    @Override
    public String answer(GsonContainer gsonContainer) {
        System.out.println(gsonContainer.params);

        ResultSet rs = db.dbPostgres.executeQuery("SELECT * FROM accounts");

        List<AccountAnsRequest> accountAnsRequest = new ArrayList();
        try {
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                double balance = RecordAccounts.createExists(db,id).getBalance();
                accountAnsRequest.add(new AccountAnsRequest(id,name,balance));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return formatParamsToAnswer(new Gson().toJson(accountAnsRequest));
    }
}
